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


import com.higherfrequencytrading.clock.IClock;

import java.util.logging.Logger;

/**
 * JNI-based implementation, trying to use rdtsc() system call
 * to access the most precise timer available
 *
 * @author cheremin
 * @since 29.12.11,  18:56
 */
public enum JNIClock implements IClock {
    INSTANCE;

    private static final Logger LOGGER = Logger.getLogger(JNIClock.class.getName());
    public static final boolean LOADED;
    private static final int FACTOR_BITS = 17;
    private static long RDTSC_FACTOR = 1 << FACTOR_BITS;
    private static long CPU_FREQUENCY = 1000;
    private static final long START;

    static {
        boolean loaded;
        long start;
        try {
            System.loadLibrary("affinity");
            estimateFrequency(50);
            estimateFrequency(200);
            LOGGER.info("Estimated clock frequency was " + CPU_FREQUENCY + " MHz");
            start = rdtsc0();
            loaded = true;
        } catch (UnsatisfiedLinkError ule) {
            LOGGER.fine("Unable to find libaffinity in [" + System.getProperty("java.library.path") + "] " + ule);
            start = 0;
            loaded = false;
        }
        LOADED = loaded;
        START = start;
    }

    public long nanoTime() {
        return tscToNano(rdtsc0() - START);
    }


    static long tscToNano(final long tsc) {
        return (tsc * RDTSC_FACTOR) >> FACTOR_BITS;
    }

    private static void estimateFrequency(int factor) {
        final long start = System.nanoTime();
        long now;
        while ((now = System.nanoTime()) == start) {
        }

        long end = start + factor * 1000000;
        final long start0 = rdtsc0();
        while ((now = System.nanoTime()) < end) {
        }
        long end0 = rdtsc0();
        end = now;

        RDTSC_FACTOR = ((end - start) << FACTOR_BITS) / (end0 - start0) - 1;
        CPU_FREQUENCY = (end0 - start0 + 1) * 1000 / (end - start);
    }

    native static long rdtsc0();
}
