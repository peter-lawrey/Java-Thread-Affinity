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

package vanilla.java.busywaiting;

import vanilla.java.busywaiting.impl.JNIBusyWaiting;
import vanilla.java.busywaiting.impl.JavaBusyWaiting;

/**
 * @author peter.lawrey
 */
public class BusyWaiter {
    private static final IBusyWaiter BUSY_WAITER;

    static {
        if (JNIBusyWaiting.LOADED)
            BUSY_WAITER = JNIBusyWaiting.INSTANCE;
        else
            BUSY_WAITER = JavaBusyWaiting.INSTANCE;
    }

    /**
     * Pause in the most efficient way available.
     */
    public static void pause() {
        BUSY_WAITER.pause();
    }

    /**
     * poll a long value until it changes (or the iterations have been reached)
     *
     * @param obj        Object to examine or null for raw address space.
     * @param address    offset in the Object or address pointer
     * @param iterations number of times to try before giving up.
     * @param value      value to test for, return as soon a the value changes.
     * @return the value value found.
     */
    public static long whileEqual(Object obj, long address, int iterations, long value) {
        return BUSY_WAITER.whileEqual(obj, address, iterations, value);
    }

    /**
     * poll a long value while it is less than a value (or the iterations have been reached)
     *
     * @param obj        Object to examine or null for raw address space.
     * @param address    offset in the Object or address pointer
     * @param iterations number of times to try before giving up.
     * @param value      value to test for, return as soon a the value is equal or more than.
     * @return the value value found.
     */
    public static long whileLessThan(Object obj, long address, int iterations, long value) {
        return BUSY_WAITER.whileLessThan(obj, address, iterations, value);
    }
}
