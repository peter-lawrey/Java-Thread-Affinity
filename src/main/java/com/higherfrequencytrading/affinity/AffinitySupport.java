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

package com.higherfrequencytrading.affinity;

import com.higherfrequencytrading.affinity.impl.NativeAffinity;
import com.higherfrequencytrading.affinity.impl.NullAffinity;
import com.higherfrequencytrading.affinity.impl.PosixJNAAffinity;
import com.higherfrequencytrading.affinity.impl.WindowsJNAAffinity;

import java.util.logging.Logger;

/**
 * Library to wrap low level JNI or JNA calls.  Can be called without needing to know the actual implementation used.
 *
 * @author peter.lawrey
 */
public enum AffinitySupport {
    ;
    private static final IAffinity AFFINITY_IMPL;

    private static final Logger LOGGER = Logger.getLogger(AffinitySupport.class.getName());
    private static Boolean JNAAvailable;

    static {
        if (NativeAffinity.LOADED) {
            LOGGER.fine("Using JNI-based affinity control implementation");
            AFFINITY_IMPL = NativeAffinity.INSTANCE;
        } else if (NativeAffinity.isWindows() && isJNAAvailable() && WindowsJNAAffinity.LOADED) {
            LOGGER.fine("Using Windows JNA-based affinity control implementation");
            AFFINITY_IMPL = WindowsJNAAffinity.INSTANCE;
        } else if (isJNAAvailable() && PosixJNAAffinity.LOADED) {
            LOGGER.fine("Using Posix JNA-based affinity control implementation");
            AFFINITY_IMPL = PosixJNAAffinity.INSTANCE;
        } else {
            LOGGER.info("Using dummy affinity control implementation");
            AFFINITY_IMPL = NullAffinity.INSTANCE;
        }
    }

    public static long getAffinity() {
        return AFFINITY_IMPL.getAffinity();
    }

    public static void setAffinity(final long affinity) {
        AFFINITY_IMPL.setAffinity(affinity);
    }

    public static boolean isJNAAvailable() {
        if (JNAAvailable == null)
            try {
                Class.forName("com.sun.jna.Platform");
                JNAAvailable = true;
            } catch (ClassNotFoundException ignored) {
                JNAAvailable = false;
            }
        return JNAAvailable;
    }
}
