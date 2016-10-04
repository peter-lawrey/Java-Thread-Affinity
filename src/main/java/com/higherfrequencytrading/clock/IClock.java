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

/**
 * High precision time source
 *
 * @author cheremin
 * @since 29.12.11,  18:53
 */
public interface IClock {

    /**
     * The general contract is same, as {@link System#nanoTime()}:
     *
     * Returns the current value of the most precise available system
     * timer, in nanoseconds.
     *
     * <p>This method can only be used to measure elapsed time and is
     * not related to any other notion of system or wall-clock time.
     * The value returned represents nanoseconds since some fixed but
     * arbitrary time (perhaps in the future, so values may be
     * negative).  This method provides nanosecond precision, but not
     * necessarily nanosecond accuracy. No guarantees are made about
     * how frequently values change. Differences in successive calls
     * that span greater than approximately 292 years (2<sup>63</sup>
     * nanoseconds) will not accurately compute elapsed time due to
     * numerical overflow.</p>
     *
     * <p> For example, to measure how long some code takes to execute:
     * <pre>
     *   long startTime = clock.nanoTime();
     *   // ... the code being measured ...
     *   long estimatedTime = clock.nanoTime() - startTime;
     * </pre>
     *
     * @return The current value of the system timer, in nanoseconds.
     */
    public long nanoTime();
}
