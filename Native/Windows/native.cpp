#include "native.h"

#define IOCTL_GET_STATUS CTL_CODE(FILE_DEVICE_UNKNOWN, 0x807, METHOD_BUFFERED, FILE_ANY_ACCESS)

static GUID rcmDeviceInterface = {0xA5DCBF10L, 0x6530, 0x11D2, {0x90, 0x1F, 0x00, 0xC0, 0x4F, 0xB9, 0x51, 0xED}};
static TCHAR deviceVIDPID[] = _T("vid_0955&pid_7321");

WinHandle getDevice;

typedef struct
{
	unsigned int timeout;
	unsigned int recipient;
	unsigned int index;
	unsigned int status;
} getStatusRequest;

JNIEXPORT jboolean JNICALL Java_rcm_RCM_smashTheStack(JNIEnv* env, jclass cl) {
	bool found = false;

	HDEVINFO deviceInfo;
	SP_DEVICE_INTERFACE_DATA deviceInterfaceData;
	PSP_DEVICE_INTERFACE_DETAIL_DATA deviceInterfaceDetailData;
	SP_DEVINFO_DATA deviceData;

	DWORD size, memberIndex;

	deviceInfo = SetupDiGetClassDevs(&rcmDeviceInterface, NULL, 0, DIGCF_DEVICEINTERFACE | DIGCF_PRESENT);

	if (deviceInfo != INVALID_HANDLE_VALUE) {
		deviceInterfaceData.cbSize = sizeof(SP_DEVICE_INTERFACE_DATA);
		memberIndex = 0;

		SetupDiEnumDeviceInterfaces(deviceInfo, NULL, &rcmDeviceInterface, memberIndex, &deviceInterfaceData);

		while (GetLastError() != ERROR_NO_MORE_ITEMS) {
			deviceData.cbSize = sizeof(deviceData);

			SetupDiGetDeviceInterfaceDetail(deviceInfo, &deviceInterfaceData, NULL, 0, &size, NULL);

			deviceInterfaceDetailData = reinterpret_cast<PSP_DEVICE_INTERFACE_DETAIL_DATA> (HeapAlloc(GetProcessHeap(), HEAP_ZERO_MEMORY, size));
			deviceInterfaceDetailData->cbSize = sizeof(SP_DEVICE_INTERFACE_DETAIL_DATA);

			if (SetupDiGetDeviceInterfaceDetail(deviceInfo, &deviceInterfaceData, deviceInterfaceDetailData, size, &size, &deviceData)) {
				if (NULL != _tcsstr((TCHAR*) deviceInterfaceDetailData -> DevicePath, deviceVIDPID)) {
					found = true;
					unsigned char requestBuffer[24] = {0};

					getStatusRequest* request;

					request = (getStatusRequest*) &requestBuffer;
					request->timeout = 1000;
					request->recipient = 0x02;
					request->index = 0;
					request->status = 0;

					unsigned char outBuffer[28672];

					OVERLAPPED overlapped;
					memset(&overlapped, 0, sizeof(overlapped));

					HANDLE handler = CreateFile(deviceInterfaceDetailData -> DevicePath, GENERIC_READ | GENERIC_WRITE, FILE_SHARE_READ | FILE_SHARE_WRITE, NULL, OPEN_EXISTING, FILE_FLAG_OVERLAPPED, NULL);

					if (handler == INVALID_HANDLE_VALUE) {
						setRCMStatus("ERROR");
						appendLog("Invalid handle value\nFailed to smash the stack");
						break;
					}

					bool smash = DeviceIoControl(handler, IOCTL_GET_STATUS, (LPVOID) &requestBuffer, 24, (LPVOID) &outBuffer, 28672, NULL, &overlapped);
					
					if (smash == 0) {
						setRCMStatus("RCM_LOADED");
						appendLog("Smashed the stack");
						return true;
					}

					CloseHandle(handler);
				}
			}

			HeapFree(GetProcessHeap(), 0, deviceInterfaceDetailData);

			if (found) {
				break;
			}

			SetupDiEnumDeviceInterfaces(deviceInfo, NULL, &rcmDeviceInterface, ++memberIndex, &deviceInterfaceData);
		}

		SetupDiDestroyDeviceInfoList(deviceInfo);
	}
}

bool checkForDevice() {
	unsigned index;
	HDEVINFO deviceInfo;
	SP_DEVINFO_DATA deviceInfoData;
	TCHAR hardwareID[1024];

	deviceInfo = SetupDiGetClassDevs(NULL, TEXT("USB"), NULL, DIGCF_PRESENT | DIGCF_ALLCLASSES);

	for (index = 0; ; index++) {
		deviceInfoData.cbSize = sizeof(deviceInfoData);

		if (!SetupDiEnumDeviceInfo(deviceInfo, index, &deviceInfoData)) {
			return false;
		}

		SetupDiGetDeviceRegistryProperty(deviceInfo, &deviceInfoData, SPDRP_HARDWAREID, NULL, (BYTE*) hardwareID, sizeof(hardwareID), NULL);

		if (_tcsstr(hardwareID, _T("VID_0955&PID_7321"))) {
			return true;
		}
	}
}

bool driverPrompted = false;

bool checkForDeviceUsingLstK() {
	KLST_HANDLE deviceList = nullptr;
	KLST_DEVINFO_HANDLE deviceInfo = nullptr;

	if (!LstK_Init(&deviceList, KLST_FLAG_NONE)) {
		appendLog("Failed to get device list");
	} else {
		if (LstK_FindByVidPid(deviceList, 0x0955, 0x7321, &deviceInfo) == FALSE) {
			if (checkForDevice()) {
				setRCMStatus("DRIVER_MISSING");
				appendLog("APX driver missing\nPlease install libusbK (v3.0.7.0)");
			}
		} else {
			if (deviceInfo != nullptr) {
				if (deviceInfo -> DriverID != KUSB_DRVID_LIBUSBK) {
					setRCMStatus("DRIVER_MISSING");
					appendLog("Incorrect APX driver installed\nPlease install libusbK (v3.0.7.0)");
					return false;
				}
				else {
					return true;
				}
			}
		}
	}

	deviceInfo = nullptr;
	LstK_Free(deviceList);

	return false;
}

void KUSB_API hotplugCallback(KHOT_HANDLE deviceHandle, KLST_DEVINFO_HANDLE deviceInfo, KLST_SYNC_FLAG notificationType) {
	if (notificationType == KLST_SYNC_FLAG_ADDED && deviceInfo->Common.Vid == 0x0955 && deviceInfo->Common.Pid == 0x7321) {
		if (deviceInfo -> DriverID != KUSB_DRVID_LIBUSBK) {
			setRCMStatus("DRIVER_MISSING");
			appendLog("Incorrect APX driver installed\nPlease install libusbK (v3.0.7.0)");
		} else {
			setRCMStatus("RCM_DETECTED");
		}
	}

	if (notificationType == KLST_SYNC_FLAG_REMOVED && deviceInfo->Common.Vid == 0x0955 && deviceInfo->Common.Pid == 0x7321) {
		setRCMStatus("RCM_UNDETECTED");
	}
}

JNIEXPORT void JNICALL Java_rcm_RCM_startDeviceListener(JNIEnv* env, jclass cl) {
	setStaticJVM(env);

	if (checkForDeviceUsingLstK()) {
		setRCMStatus("RCM_DETECTED");
	}

	KHOT_HANDLE hotplugHandle;
	KHOT_PARAMS hotplugParameters;

	memset(&hotplugParameters, 0, sizeof(hotplugParameters));
	hotplugParameters.OnHotPlug = hotplugCallback;
	hotplugParameters.Flags = KHOT_FLAG_NONE;

	if (!HotK_Init(&hotplugHandle, &hotplugParameters)) {
		setRCMStatus("ERROR");
		appendLog("Unable to start device listener");
	}
	else {
		getDevice = CreateEvent(nullptr, TRUE, FALSE, nullptr);
		WaitForSingleObject(getDevice.get(), INFINITE);
	}
}
