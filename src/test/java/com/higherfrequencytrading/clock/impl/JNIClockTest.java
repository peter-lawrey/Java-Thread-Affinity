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

package com.higherfrequencytrading.clock.impl;

import org.junit.Assume;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

/**
 * fixme: Class DefaultClockTest is for test
 *
 * @author cheremin
 * @since 29.12.11,  19:12
 */
public class JNIClockTest {
    @BeforeClass
    public static void checkJniLibraryPresent() {
        Assume.assumeTrue(JNIClock.LOADED);
    }

    @Test
    public void testRdtsc() throws Exception {
        long l1 = JNIClock.rdtsc0();
        long l2 = JNIClock.rdtsc0();
        assertTrue(l2 > l1);
        assertTrue(l2 < l1 + 1000000);
    }

    @Test
    public void testRdtscPerf() {
        final int runs = 10 * 1000 * 1000;
        JNIClock.rdtsc0();
        long start = System.nanoTime();
        long start0 = JNIClock.rdtsc0();
        for (int i = 0; i < runs; i++)
            JNIClock.rdtsc0();
        long time = System.nanoTime() - start;
        final long time0 = JNIClock.rdtsc0() - start0;
        long time2 = JNIClock.tscToNano(time0);
        System.out.printf("Each call took %.1f ns and the ratio was %.5f%n", (double) time / runs, (double) time2 / time);
    }
}