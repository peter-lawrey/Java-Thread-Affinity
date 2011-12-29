package vanilla.java.affinity;

import com.sun.jna.*;
import com.sun.jna.ptr.LongByReference;

public enum JNAAffinity implements AffinitySupport.IAffinity {
    INSTANCE;
    public static final Boolean LOADED;
    public static final String LIBRARY_NAME = Platform.isWindows() ? "msvcrt" : "c";

    private interface CLibrary extends Library {
        public static final CLibrary INSTANCE = (CLibrary)
                Native.loadLibrary(LIBRARY_NAME, CLibrary.class);

        public int sched_setaffinity(final int pid, final int cpusetsize, final PointerType cpuset);

        public int sched_getaffinity(final int pid, final int cpusetsize, final PointerType cpuset);

//        public int sched_getcpu();
    }

    static {
        boolean loaded = false;
        try {
            INSTANCE.getAffinity();
            loaded = true;
        } catch (Exception e) {
            System.out.println("Unable to load jna library " + e);
        }
        LOADED = loaded;
    }

    @Override
    public long getAffinity() {
        final CLibrary lib = CLibrary.INSTANCE;
        final LongByReference cpuset = new LongByReference(0L);
        final int ret = lib.sched_getaffinity(0, Long.SIZE / 8, cpuset);
        if (ret < 0) {
            final int errNo = getErrorNo();
            throw new IllegalStateException("sched_getaffinity((" + Long.SIZE / 8 + ") , &(" + cpuset + ") ) return " + ret + ", errno() = " + errNo);
        }
        return cpuset.getValue();
    }

    @Override
    public void setAffinity(long affinity) {
        final CLibrary lib = CLibrary.INSTANCE;
        final int ret = lib.sched_setaffinity(0, Long.SIZE / 8, new LongByReference(affinity));
        if (ret < 0) {
            final int errNo = getErrorNo();
            throw new IllegalStateException("sched_setaffinity((" + Long.SIZE / 8 + ") , &(" + affinity + ") ) return " + ret + ", errno() = " + errNo);
        }
    }

    private static int getErrorNo() {
        final NativeLibrary nativeLib = NativeLibrary.getInstance(LIBRARY_NAME);
        final Pointer pErrNo = nativeLib.getFunction("errno");
        return pErrNo.getInt(0);
    }

    @Override
    public long nanoTime() {
        return System.nanoTime();
    }
}
