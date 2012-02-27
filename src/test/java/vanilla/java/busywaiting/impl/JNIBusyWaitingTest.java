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

package vanilla.java.busywaiting.impl;

import com.sun.jna.Platform;
import org.junit.Test;
import sun.nio.ch.DirectBuffer;

import java.nio.ByteBuffer;

import static junit.framework.Assert.assertEquals;

/**
 * @author peter.lawrey
 */
public class JNIBusyWaitingTest {
    @Test
    public void testPause() {
        if (!Platform.isLinux()) return;

        int runs = 2000000;
        long start = System.nanoTime();
        for (int i = 0; i < runs; i++)
            JNIBusyWaiting.INSTANCE.pause();
        long time = System.nanoTime() - start;
        System.out.printf("The average time to pause was %.1f ns%n", (double) time / runs);
    }

    @Test
    public void testWhileEqual() {
        if (!Platform.isLinux()) return;

        DirectBuffer buffer = (DirectBuffer) ByteBuffer.allocateDirect(8);
        int runs = 100000000;
        long start = System.nanoTime();
        assertEquals(0, JNIBusyWaiting.INSTANCE.whileEqual(null, buffer.address(), runs, 0));
        long time = System.nanoTime() - start;
        System.out.printf("The average time to poll equals was %.1f ns%n", (double) time / runs);
    }

    @Test
    public void testWhileLessThan() {
        if (!Platform.isLinux()) return;

        DirectBuffer buffer = (DirectBuffer) ByteBuffer.allocateDirect(8);
        int runs = 100000000;
        long start = System.nanoTime();
        assertEquals(0, JNIBusyWaiting.INSTANCE.whileLessThan(null, buffer.address(), runs, 1));
        long time = System.nanoTime() - start;
        System.out.printf("The average time to poll less than was %.1f ns%n", (double) time / runs);
    }
}
