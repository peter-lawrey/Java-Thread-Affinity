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

package com.higherfrequencytrading.clock;


import com.higherfrequencytrading.clock.impl.JNIClock;
import com.higherfrequencytrading.clock.impl.SystemClock;

/**
 * Static factory for available {@link IClock} interface implementation
 *
 * @author cheremin
 * @since 29.12.11,  19:02
 */
public final class ClockSupport {
    public static final IClock INSTANCE;

    static {
        if (JNIClock.LOADED) {
            INSTANCE = JNIClock.INSTANCE;
        } else {
            INSTANCE = SystemClock.INSTANCE;
        }
    }

    /**
     * @return The current value of the system timer, in nanoseconds.
     * @see com.higherfrequencytrading.clock.IClock#nanoTime()
     */
    public static long nanoTime() {
        return INSTANCE.nanoTime();
    }
}
