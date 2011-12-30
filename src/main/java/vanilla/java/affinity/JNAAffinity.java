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

import com.sun.jna.*;
import com.sun.jna.ptr.LongByReference;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author peter.lawrey
 * @author BegemoT
 */
public enum JNAAffinity implements AffinitySupport.IAffinity {
    INSTANCE;
    public static final Boolean LOADED;
    private static final Logger LOGGER = Logger.getLogger(JNAAffinity.class.getName());
    private static final String LIBRARY_NAME = Platform.isWindows() ? "msvcrt" : "c";

    /**
     * @author BegemoT
     * @author jbellis
     */
    private interface CLibrary extends Library {
        public static final CLibrary INSTANCE = (CLibrary)
                Native.loadLibrary(LIBRARY_NAME, CLibrary.class);

        public int sched_setaffinity(final int pid, final int cpusetsize, final PointerType cpuset) throws LastErrorException;

        public int sched_getaffinity(final int pid, final int cpusetsize, final PointerType cpuset) throws LastErrorException;

//        public int sched_getcpu();
    }

    static {
        boolean loaded = false;
        try {
            INSTANCE.getAffinity();
            loaded = true;
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Unable to load jna library", e);
        }
        LOADED = loaded;
    }

    @Override
    public long getAffinity() {
        final CLibrary lib = CLibrary.INSTANCE;
        final LongByReference cpuset = new LongByReference(0L);
        final int ret = lib.sched_getaffinity(0, Long.SIZE / 8, cpuset);
        assert ret == 0;
        return cpuset.getValue();
    }

    @Override
    public void setAffinity(long affinity) {
        final CLibrary lib = CLibrary.INSTANCE;
        final int ret = lib.sched_setaffinity(0, Long.SIZE / 8, new LongByReference(affinity));
        assert ret == 0;
    }

    @Override
    public long nanoTime() {
        return System.nanoTime();
    }
}
