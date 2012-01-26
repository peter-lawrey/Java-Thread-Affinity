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

import sun.misc.Unsafe;
import vanilla.java.busywaiting.IBusyWaiter;

import java.lang.reflect.Field;

/**
 * @author peter.lawrey
 */
public enum JavaBusyWaiting implements IBusyWaiter {
    INSTANCE;

    static final Unsafe UNSAFE;


    @Override
    public void pause() {
        Thread.yield();
    }

    @Override
    public long whileEqual(Object obj, long address, int iterations, long value) {
        long value2 = UNSAFE.getLongVolatile(null, address);
        while (value2 == value && iterations-- > 0) {
            pause();
            value2 = UNSAFE.getLongVolatile(obj, address);
        }
        return value2;
    }

    @Override
    public long whileLessThan(Object obj, long address, int iterations, long value) {
        long value2 = UNSAFE.getLongVolatile(null, address);
        while (value2 < value && iterations-- > 0) {
            pause();
            value2 = UNSAFE.getLongVolatile(obj, address);
        }
        return value2;
    }

    static {
        try {
            Field theUnsafe = Unsafe.class.getDeclaredField("theUnsafe");
            theUnsafe.setAccessible(true);
            UNSAFE = (Unsafe) theUnsafe.get(null);
        } catch (Exception e) {
            throw new AssertionError(e);
        }
    }
}
