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

package vanilla.java.affinity;

/**
 * @author peter.lawrey
 */
public enum NativeAffinity implements AffinitySupport.IAffinity {
    INSTANCE;

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
            System.out.println("Estimated clock frequency was " + CPU_FREQUENCY + " MHz");
            start = rdtsc0();
            loaded = true;
        } catch (UnsatisfiedLinkError ule) {
            System.out.println("Debug: Unable to find libaffinity in " + System.getProperty("java.library.path") + " " + ule);
            start = 0;
            loaded = false;
        }
        LOADED = loaded;
        START = start;
    }

    private static void estimateFrequency(int factor) {
        long now, start = System.nanoTime();
        while ((now = System.nanoTime()) == start) ;
        long start0 = rdtsc0();
        long end = start + factor * 1000000;
        while ((now = System.nanoTime()) < end) ;
        long end0 = rdtsc0();
        end = now;
        RDTSC_FACTOR = ((end - start) << FACTOR_BITS) / (end0 - start0) - 1;
        CPU_FREQUENCY = (end0 - start0 + 1) * 1000 / (end - start);
    }

    private native static long getAffinity0();

    private native static void setAffinity0(long affinity);

    native static long rdtsc0();


    @Override
    public long getAffinity() {
        return getAffinity0();
    }

    @Override
    public void setAffinity(long affinity) {
        setAffinity0(affinity);
    }

    public long nanoTime() {
        return tscToNano(rdtsc0() - START);
    }

    public static long tscToNano(long tsc) {
        return (tsc * RDTSC_FACTOR) >> FACTOR_BITS;
    }
}
