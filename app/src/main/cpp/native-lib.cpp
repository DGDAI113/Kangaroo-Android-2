#include <jni.h>
#include <string>
extern "C" JNIEXPORT jstring JNICALL
Java_com_dgdai113_kangaroo_MainActivity_nativeEngineStatus(JNIEnv* env, jobject) {
    const std::string status =
        "Native bridge loaded. Upstream Kangaroo CPU engine is not linked yet; "
        "search execution remains disabled until the ARM64 port is compiled and tested.";
    return env->NewStringUTF(status.c_str());
}
