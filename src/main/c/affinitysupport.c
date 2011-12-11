#include "vanilla_java_affinity_AffinitySupport.h"
/*
 * Class:     vanilla_java_affinity_AffinitySupport
 * Method:    getAffinity
 * Signature: ()J
 */
JNIEXPORT jlong JNICALL Java_vanilla_java_affinity_AffinitySupport_getAffinity
  (JNIEnv *env, jclass c) {
      return (jlong) 0;
  }

/*
 * Class:     vanilla_java_affinity_AffinitySupport
 * Method:    setAffinity
 * Signature: (J)V
 */
JNIEXPORT void JNICALL Java_vanilla_java_affinity_AffinitySupport_setAffinity
  (JNIEnv *env, jclass c, jlong affinity) {
        return;
  }

/*
 * Class:     vanilla_java_affinity_AffinitySupport
 * Method:    rdtsc
 * Signature: ()J
 */
JNIEXPORT jlong JNICALL Java_vanilla_java_affinity_AffinitySupport_rdtsc
  (JNIEnv *env, jclass c) {
  return (jlong) 0;
  }
