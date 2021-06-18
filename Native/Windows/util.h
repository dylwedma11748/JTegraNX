#pragma once
#include <jni.h>

void setStaticJVM(JNIEnv* env);
const char* parseString(jstring string);
void callStaticVoid(jclass cl, const char* name, const char* signature);
void callStaticVoidWithStringArg(jclass cl, const char* name, const char* signature, const char* string);
void appendLog(const char* line);
void setRCMStatus(const char* status);
void promptDriverInstall();