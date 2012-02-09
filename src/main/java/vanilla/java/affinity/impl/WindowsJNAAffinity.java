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

import com.sun.jna.LastErrorException;
import com.sun.jna.Library;
import com.sun.jna.Native;
import com.sun.jna.PointerType;
import com.sun.jna.platform.win32.WinDef;
import com.sun.jna.ptr.LongByReference;
import vanilla.java.affinity.IAffinity;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Implementation of {@link IAffinity} based on JNA call of
 * sched_SetThreadAffinityMask/GetProcessAffinityMask from Windows 'kernel32' library. Applicable for
 * most windows platforms
 * <p/> *
 *
 * @author andre.monteiro
 */
public enum WindowsJNAAffinity implements IAffinity {
    INSTANCE;
    private static final Logger LOGGER = Logger.getLogger(WindowsJNAAffinity.class.getName());

    public static final boolean LOADED;

    /**
     * @author BegemoT
     */
    private interface CLibrary extends Library {
        public static final CLibrary INSTANCE = (CLibrary) Native.loadLibrary("kernel32", CLibrary.class);

        public int GetProcessAffinityMask(final int pid, final PointerType lpProcessAffinityMask, final PointerType lpSystemAffinityMask) throws LastErrorException;

        public void SetThreadAffinityMask(final int pid, final WinDef.DWORD lpProcessAffinityMask) throws LastErrorException;

        public int GetCurrentThread() throws LastErrorException;
    }

    static {
        boolean loaded = false;
        try {
            INSTANCE.getAffinity();
            loaded = true;
        } catch (UnsatisfiedLinkError e) {
            LOGGER.log(Level.WARNING, "Unable to load jna library " + e);
        }
        LOADED = loaded;
    }

    @Override
    public long getAffinity() {
        final CLibrary lib = CLibrary.INSTANCE;
        final LongByReference cpuset1 = new LongByReference(0);
        final LongByReference cpuset2 = new LongByReference(0);
        try {

            final int ret = lib.GetProcessAffinityMask(-1, cpuset1, cpuset2);
            if (ret < 0)
                throw new IllegalStateException("sched_getaffinity((" + Long.SIZE / 8 + ") , &(" + cpuset1 + ") ) return " + ret);

            return cpuset1.getValue();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    @Override
    public void setAffinity(final long affinity) {
        final CLibrary lib = CLibrary.INSTANCE;
        try {

            WinDef.DWORD aff = new WinDef.DWORD(affinity);

            lib.SetThreadAffinityMask(lib.GetCurrentThread(), aff);

        } catch (LastErrorException e) {
            e.printStackTrace();
            throw new IllegalStateException("sched_getaffinity((" + Long.SIZE / 8 + ") , &(" + affinity + ") ) errorNo=" + e.getErrorCode(), e);
        }
    }
}
