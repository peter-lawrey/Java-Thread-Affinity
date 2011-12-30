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

import com.sun.jna.LastErrorException;
import org.junit.Assume;
import org.junit.BeforeClass;
import org.junit.Test;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.fail;

/**
 * @author peter.lawrey
 */
public class JNAAffinityTest {
    @BeforeClass
    public static void checkJniLibraryPresent() {
        Assume.assumeTrue(JNAAffinity.LOADED);
    }

    @Test
    public void getSetAffinity() {
        long init = JNAAffinity.INSTANCE.getAffinity();
        JNAAffinity.INSTANCE.setAffinity(0x01);
        assertEquals(0x01, JNAAffinity.INSTANCE.getAffinity());

        try {
            JNAAffinity.INSTANCE.setAffinity(0x00);
            fail("This should fail");
        } catch (LastErrorException expected) {
            assertEquals("errno was " + expected.getErrorCode(), expected.getMessage());
        }

        JNAAffinity.INSTANCE.setAffinity(init);
        assertEquals(init, JNAAffinity.INSTANCE.getAffinity());
    }

    @Test
    public void testNanoTimePerf() {
        final int runs = 10 * 1000 * 1000;
        JNAAffinity.INSTANCE.nanoTime();
        long start = System.nanoTime();
        long start0 = JNAAffinity.INSTANCE.nanoTime();
        for (int i = 0; i < runs; i++)
            JNAAffinity.INSTANCE.nanoTime();
        long time = System.nanoTime() - start;
        final long time0 = JNAAffinity.INSTANCE.nanoTime() - start0;
        long time2 = NativeAffinity.tscToNano(time0);
        System.out.printf("Each call took %.1f ns and the ratio was %.5f%n", (double) time / runs, (double) time2 / time);
    }
}
