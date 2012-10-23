#include <jni.h>
#include <cpu-features.h>

#define JNI_TRUE 1
#define JNI_FALSE 0

jboolean Java_com_kr_busan_cw_cinepox_player_CinePlayer_isNeon(JNIEnv* env,
		jobject thiz) {
	uint64_t features;
	if (android_getCpuFamily() != ANDROID_CPU_FAMILY_ARM) {
		return JNI_FALSE;
	}

	features = android_getCpuFeatures();
	if ((features & ANDROID_CPU_ARM_FEATURE_ARMv7) == 0) {
		return JNI_FALSE;
	}

	/* HAVE_NEON is defined in Android.mk ! */
#ifdef HAVE_NEON
	if ((features & ANDROID_CPU_ARM_FEATURE_NEON) == 0) {
		return JNI_FALSE;
	}

#else
	return JNI_FALSE;
#endif
	return JNI_TRUE;
}
