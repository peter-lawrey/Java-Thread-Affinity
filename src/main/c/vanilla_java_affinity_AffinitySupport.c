#define _GNU_SOURCE
#include <jni.h>
#include <sched.h>
#include "vanilla_java_affinity_AffinitySupport.h"
/*
 * Class:     vanilla_java_affinity_AffinitySupport
 * Method:    getAffinity
 * Signature: ()J
 */
JNIEXPORT jlong JNICALL Java_vanilla_java_affinity_AffinitySupport_getAffinity
  (JNIEnv *env, jclass c) {
    cpu_set_t mask;
    int ret = sched_getaffinity(0, sizeof(mask), &mask);
    if (ret < 0) return ~0LL;
    long long mask2 = 0, i;
    for(i=0;i<sizeof(mask2)*8;i++)
        if (CPU_ISSET(i, &mask))
            mask2 |= 1L << i;
    return (jlong) mask2;
}

/*
 * Class:     vanilla_java_affinity_AffinitySupport
 * Method:    setAffinity
 * Signature: (J)V
 */
JNIEXPORT void JNICALL Java_vanilla_java_affinity_AffinitySupport_setAffinity
  (JNIEnv *env, jclass c, jlong affinity) {
    int i;
    cpu_set_t mask;
    CPU_ZERO(&mask);
    for(i=0;i<sizeof(affinity)*8;i++)
        if ((affinity >> i) & 1)
            CPU_SET(i, &mask);
    sched_setaffinity(0, sizeof(mask), &mask);
}

#if defined(__i386__)
static __inline__ unsigned long long rdtsc(void) {
  unsigned long long int x;
     __asm__ volatile (".byte 0x0f, 0x31" : "=A" (x));
     return x;
}

#elif defined(__x86_64__)
static __inline__ unsigned long long rdtsc(void) {
  unsigned hi, lo;
  __asm__ __volatile__ ("rdtsc" : "=a"(lo), "=d"(hi));
  return ( (unsigned long long)lo)|( ((unsigned long long)hi)<<32 );
}

#elif defined(__MIPS_32__)
#define rdtsc(dest) \
   _ _asm_ _ _ _volatile_ _("mfc0 %0,$9; nop" : "=r" (dest))

#elif defined(__MIPS_SGI__)
#include <time.h>

static __inline__ unsigned long long rdtsc (void) {
  struct timespec tp;
  clock_gettime (CLOCK_SGI_CYCLE, &tp);
  return (unsigned long long)(tp.tv_sec * (unsigned long long)1000000000) + (unsigned long long)tp.tv_nsec;
}
#endif

/*
 * Class:     vanilla_java_affinity_AffinitySupport
 * Method:    rdtsc
 * Signature: ()J
 */
JNIEXPORT jlong JNICALL Java_vanilla_java_affinity_AffinitySupport_rdtsc
  (JNIEnv *env, jclass c) {
  return (jlong) rdtsc();
}
