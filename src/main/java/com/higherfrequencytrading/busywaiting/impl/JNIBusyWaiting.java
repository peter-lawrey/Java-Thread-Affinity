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

package com.higherfrequencytrading.busywaiting.impl;

import com.higherfrequencytrading.busywaiting.IBusyWaiter;

/**
 * @author peter.lawrey
 */
public enum JNIBusyWaiting implements IBusyWaiter {
    INSTANCE;

    public static final boolean LOADED;

    static {
        boolean loaded;
        try {
            System.loadLibrary("affinity");
            loaded = true;
        } catch (UnsatisfiedLinkError ule) {
            loaded = false;
        }
        LOADED = loaded;
    }

    public static native void pause0();

    public static native long whileEqual0(long address, int iterations, long value);

    public static native long whileLessThan0(long address, int iterations, long value);

    @Override
    public void pause() {
        pause0();
    }

    @Override
    public long whileEqual(Object obj, long address, int iterations, long value) {
        if (obj == null)
            return whileEqual0(address, iterations, value);

        return whileEqual0(obj, address, iterations, value);
    }

    private static long whileEqual0(Object obj, long address, int iterations, long value) {
        long value2 = JavaBusyWaiting.UNSAFE.getLongVolatile(null, address);
        while (value2 == value && iterations-- > 0) {
            pause0();
            value2 = JavaBusyWaiting.UNSAFE.getLongVolatile(obj, address);
        }
        return value2;
    }

    @Override
    public long whileLessThan(Object obj, long address, int iterations, long value) {
        if (obj == null)
            return whileLessThan0(address, iterations, value);

        return whileLessThan0(obj, address, iterations, value);
    }

    private static long whileLessThan0(Object obj, long address, int iterations, long value) {
        long value2 = JavaBusyWaiting.UNSAFE.getLongVolatile(null, address);
        while (value2 < value && iterations-- > 0) {
            pause0();
            value2 = JavaBusyWaiting.UNSAFE.getLongVolatile(obj, address);
        }
        return value2;
    }
}
