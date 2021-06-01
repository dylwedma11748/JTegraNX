#include <jni.h>

#include <iostream>
#include <windows.h>
#include <tchar.h>
#include <setupapi.h>

#include "libusbk_int.h"
#include "WinHandle.h"
#include "util.h"

#ifndef _Included_rcm_RCM
#define _Included_rcm_RCM
#ifdef __cplusplus
extern "C" {
#endif

JNIEXPORT jboolean JNICALL Java_rcm_RCM_smashTheStack(JNIEnv* env, jclass cl);
JNIEXPORT void JNICALL Java_rcm_RCM_startDeviceListener(JNIEnv* env, jclass cl);

#ifdef __cplusplus
}
#endif
#endif
