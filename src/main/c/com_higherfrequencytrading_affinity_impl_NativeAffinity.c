/*
 * Copyright 2011 Peter Lawrey
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

#define _GNU_SOURCE
#include <jni.h>
#include <sched.h>
#include "com_higherfrequencytrading_affinity_impl_NativeAffinity.h"
#include "com_higherfrequencytrading_busywaiting_impl_JNIBusyWaiting.h"
#include "com_higherfrequencytrading_clock_impl_JNIClock.h"
/*
 * Class:     com_higherfrequencytrading_affinity_impl_NativeAffinity
 * Method:    getAffinity0
 * Signature: ()J
 */
JNIEXPORT jlong JNICALL Java_com_higherfrequencytrading_affinity_impl_NativeAffinity_getAffinity0
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
 * Class:     com_higherfrequencytrading_affinity_NativeAffinity
 * Method:    setAffinity0
 * Signature: (J)V
 */
JNIEXPORT void JNICALL Java_com_higherfrequencytrading_affinity_impl_NativeAffinity_setAffinity0
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
 * Class:     com_higherfrequencytrading_affinity_NativeAffinity
 * Method:    rdtsc0
 * Signature: ()J
 */
JNIEXPORT jlong JNICALL Java_com_higherfrequencytrading_clock_impl_JNIClock_rdtsc0
  (JNIEnv *env, jclass c) {
  return (jlong) rdtsc();
}

// From http://locklessinc.com/articles/locks/
/* Compile read-write barrier */
#define barrier() asm volatile("": : :"memory")

/* Pause instruction to prevent excess processor bus usage */
#define cpu_relax() asm volatile("pause\n": : :"memory")

/*
 * Class:     com_higherfrequencytrading_busywaiting_impl_JNIBusyWaiting
 * Method:    pause0
 * Signature: ()V
 */
JNIEXPORT void JNICALL Java_com_higherfrequencytrading_busywaiting_impl_JNIBusyWaiting_pause0
  (JNIEnv *env, jclass c) {
  cpu_relax();
}

/*
 * Class:     com_higherfrequencytrading_busywaiting_impl_JNIBusyWaiting
 * Method:    whileEqual0
 * Signature: (JIJ)J
 */
JNIEXPORT jlong JNICALL Java_com_higherfrequencytrading_busywaiting_impl_JNIBusyWaiting_whileEqual0
  (JNIEnv *env, jclass c, jlong address0, jint iterations, jlong value) {
  volatile jlong * address = (volatile jlong *) address0;
  barrier();
  jlong value2 = *address;
  while(value2 == value && iterations-- > 0) {
      cpu_relax();
      barrier();
      value2 = *address;
  }
  return value2;
}

/*
 * Class:     com_higherfrequencytrading_busywaiting_impl_JNIBusyWaiting
 * Method:    whileLessThan0
 * Signature: (JIJ)J
 */
JNIEXPORT jlong JNICALL Java_com_higherfrequencytrading_busywaiting_impl_JNIBusyWaiting_whileLessThan0
  (JNIEnv *env, jclass c, jlong address0, jint iterations, jlong value) {
  volatile jlong * address = (volatile jlong *) address0;
  barrier();
  jlong value2 = *address;
  while(value2 < value && iterations-- > 0) {
      cpu_relax();
      barrier();
      value2 = *address;
  }
  return value2;
}
