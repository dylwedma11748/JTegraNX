#include <jni.h>

#include <stdlib.h>
#include <string.h>
#include <fcntl.h>
#include <unistd.h>
#include <sys/ioctl.h>
#include <linux/usbdevice_fs.h>
#include <linux/usb/ch9.h>
#include <errno.h>

#ifndef _Included_rcm_RCM
#define _Included_rcm_RCM
#ifdef __cplusplus
extern "C" {
#endif

JNIEXPORT jboolean JNICALL Java_rcm_RCM_smashTheStack(JNIEnv* env, jclass cl, jint bus_id, jint device_address);

#ifdef __cplusplus
}
#endif
#endif
