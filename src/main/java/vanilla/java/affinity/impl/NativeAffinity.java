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

package vanilla.java.affinity.impl;


import java.util.logging.Logger;

/**
 * @author peter.lawrey
 */
public enum NativeAffinity implements IAffinityImpl {
    INSTANCE;
    private static final Logger LOGGER = Logger.getLogger( NativeAffinity.class.getName() );

    public static final boolean LOADED;

    static {
        boolean loaded;
        long start;
        try {
            System.loadLibrary("affinity");
            loaded = true;
        } catch (UnsatisfiedLinkError ule) {
            System.out.println("Debug: Unable to find libaffinity in " + System.getProperty("java.library.path") + " " + ule);
            loaded = false;
        }
        LOADED = loaded;
    }

    private native static long getAffinity0();

    private native static void setAffinity0(long affinity);

    public native static long rdtsc0();

    @Override
    public long getAffinity() {
        return getAffinity0();
    }

    @Override
    public void setAffinity(final long affinity) {
        setAffinity0(affinity);
    }
}
