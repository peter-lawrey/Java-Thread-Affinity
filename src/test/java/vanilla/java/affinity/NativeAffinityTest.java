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

import org.junit.Assume;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * @author peter.lawrey
 */
public class NativeAffinityTest {
    @BeforeClass
    public static void checkJniLibraryPresent() {
        Assume.assumeTrue(NativeAffinity.LOADED);
    }

    @Test
    public void testGetAffinity() throws Exception {
        long a = NativeAffinity.INSTANCE.getAffinity();
        assertFalse(a == 0);
        assertFalse(a == -1);
    }

    @Test
    public void testSetAffinity() throws Exception {
        NativeAffinity.INSTANCE.setAffinity(0x1);
        assertEquals(0x1, NativeAffinity.INSTANCE.getAffinity());

        NativeAffinity.INSTANCE.setAffinity(0x2);
        assertEquals(0x2, NativeAffinity.INSTANCE.getAffinity());
    }

    @Test
    public void testRdtsc() throws Exception {
        long l1 = NativeAffinity.rdtsc0();
        long l2 = NativeAffinity.rdtsc0();
        assertTrue(l2 > l1);
        assertTrue(l2 < l1 + 1000000);
    }

    @Test
    public void testRdtscPerf() {
        final int runs = 10 * 1000 * 1000;
        NativeAffinity.rdtsc0();
        long start = System.nanoTime();
        long start0 = NativeAffinity.rdtsc0();
        for (int i = 0; i < runs; i++)
            NativeAffinity.rdtsc0();
        long time = System.nanoTime() - start;
        final long time0 = NativeAffinity.rdtsc0() - start0;
        long time2 = NativeAffinity.tscToNano(time0);
        System.out.printf("Each call took %.1f ns and the ratio was %.5f%n", (double) time / runs, (double) time2 / time);
    }
}
