#include "util.h"

JavaVM* jvm;

void setStaticJVM(JNIEnv* env) {
	(*env).GetJavaVM(&jvm);
}

const char* parseString(jstring string) {
	JNIEnv* env;
	(*jvm).AttachCurrentThread((void**)&env, NULL);
	const char* str = (*env).GetStringUTFChars(string, NULL);
	return str;
}

void callStaticVoid(jclass cl, const char* name, const char* signature) {
	JNIEnv* env;
	(*jvm).AttachCurrentThread((void**)&env, NULL);
	jmethodID id = (*env).GetStaticMethodID(cl, name, signature);
	(*env).CallStaticVoidMethod(cl, id);
}

void callStaticVoidWithStringArg(jclass cl, const char* name, const char* signature, const char* string) {
	JNIEnv* env;
	(*jvm).AttachCurrentThread((void**)&env, NULL);
	jmethodID id = (*env).GetStaticMethodID(cl, name, signature);
	jstring value = (*env).NewStringUTF(string);
	(*env).CallStaticVoidMethod(cl, id, value);
}

void appendLog(const char* line) {
	JNIEnv* env;
	(*jvm).AttachCurrentThread((void**)&env, NULL);
	jclass c = (*env).FindClass("rcm/RCM");

	callStaticVoidWithStringArg(c, "appendLog", "(Ljava/lang/String;)V", line);
}

void setRCMStatus(const char* status) {
	JNIEnv* env;
	(*jvm).AttachCurrentThread((void**)&env, NULL);
	jclass c = (*env).FindClass("rcm/RCM");

	callStaticVoidWithStringArg(c, "setRCMStatus", "(Ljava/lang/String;)V", status);
}

void promptDriverInstall() {
	JNIEnv* env;
	(*jvm).AttachCurrentThread((void**)&env, NULL);
	jclass c = (*env).FindClass("rcm/RCM");

	callStaticVoid(c, "promptDriverInstall", "()V");
}
