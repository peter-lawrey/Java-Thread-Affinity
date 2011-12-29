package test.threads;

import com.sun.jna.*;
import com.sun.jna.ptr.LongByReference;

import java.text.ParseException;
import java.util.StringTokenizer;

/**
 * For attaching threads to cores
 *
 * @author cheremin
 * @since 25.10.11,  14:18
 */
public class ThreadAffinity {

    private static final Core[] cores;

    static {
        final int coresCount = Runtime.getRuntime().availableProcessors();
        cores = new Core[coresCount];

        for (int i = 0; i < cores.length; i++) {
            cores[i] = new Core(i);
        }
    }


    public static final class Core {
        private final int sequence;

        public Core(final int sequence) {
            this.sequence = sequence;
            if (sequence > Integer.SIZE) {
                throw new IllegalStateException("Too many cores (" + sequence + ") for integer mask");
            }
        }

        public int sequence() {
            return sequence;
        }

        public void attachTo() throws Exception {

            final long mask = mask();

            setCurrentThreadAffinityMask(mask);
        }

        public void attach(final Thread thread) throws Exception {

            final long mask = mask();

            setThreadAffinityMask(thread.getId(), mask);
        }

        private int mask() {
            return 1 << sequence;
        }


        @Override
        public String toString() {
            return String.format("Core[#%d]", sequence());
        }

    }

    public static void setCurrentThreadAffinityMask(final long mask) throws Exception {
        final CLibrary lib = CLibrary.INSTANCE;
        final int cpuMaskSize = Long.SIZE / 8;
        try {
            final int ret = lib.sched_setaffinity(0, cpuMaskSize, new LongByReference(mask));
            if (ret < 0) {
                final int errNo = getErrorNo();
                throw new Exception("sched_setaffinity( 0, (" + cpuMaskSize + ") , &(" + mask + ") ) return " + ret + ", errno() = " + errNo);
            }
        } catch (Throwable e) {
            throw new Exception(e);
        }
    }

    public static void setThreadAffinityMask(final long threadID,
                                             final long mask) throws Exception {
        final CLibrary lib = CLibrary.INSTANCE;
        final int cpuMaskSize = Long.SIZE / 8;
        try {
            final int ret = lib.sched_setaffinity((int) threadID, cpuMaskSize, new LongByReference(mask));
            if (ret < 0) {
                final int errNo = getErrorNo();
                throw new Exception("sched_setaffinity( " + threadID + ", (" + cpuMaskSize + ") , &(" + mask + ") ) return " + ret + ", errno() = " + errNo);
            }
        } catch (Throwable e) {
            throw new Exception(e);
        }
    }


    private static int getErrorNo() {
        final NativeLibrary nativeLib = NativeLibrary.getInstance("c");
        final Pointer pErrNo = nativeLib.getFunction("errno");
        return pErrNo.getInt(0);
    }


    public static Core[] cores() {
        return cores.clone();
    }

    public static Core currentCore() {
        final int cpuSequence = CLibrary.INSTANCE.sched_getcpu();
        return cores[cpuSequence];
    }

    public static void nice(final int increment) throws Exception {
        final CLibrary lib = CLibrary.INSTANCE;
        try {
            final int ret = lib.nice(increment);
            if (ret < 0) {
                final int errNo = getErrorNo();
                throw new Exception("nice( " + increment + " ) return " + ret + ", errno() = " + errNo);
            }
        } catch (Throwable e) {
            throw new Exception(e);
        }
    }

    private interface CLibrary extends Library {
        public static final CLibrary INSTANCE = (CLibrary)
                Native.loadLibrary((Platform.isWindows() ? "msvcrt" : "c"), CLibrary.class);

        public void printf(final String format,
                           final Object... args);

        public int nice(final int increment);

        public int sched_setaffinity(final int pid,
                                     final int cpusetsize,
                                     final PointerType cpuset);

        public int sched_getcpu();
    }

    public static void main(final String[] args) throws Exception {

        final Core currentCore = currentCore();
//        System.out.printf( "currentCore() -> %s\n", currentCore );

//        final int niceRet = lib.nice( -20 );
//        System.out.printf( "nice -> %d\n", niceRet );


        for (final Core core : cores()) {
            new Thread() {
                @Override
                public void run() {
                    try {
                        core.attachTo();
                        System.out.printf("currentCore() -> %s\n", currentCore());
                        for (int i = 0; i < Integer.MAX_VALUE; i++) {
                            i--;
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }.start();


        }

    }

    public static int[] parseCoresIndexes(final String str,
                                          final int[] defaults) throws ParseException {
        final StringTokenizer stok = new StringTokenizer(str, ",");
        final int size = stok.countTokens();
        if (size == 0) {
            return defaults;
        }

        final int maxIndex = Runtime.getRuntime().availableProcessors() - 1;
        final int[] indexes = new int[size];
        for (int i = 0; stok.hasMoreTokens(); i++) {
            final String token = stok.nextToken();
            final int index;
            try {
                index = Integer.parseInt(token);
            } catch (NumberFormatException e) {
                throw new ParseException("Can't parse [" + i + "]='" + token + "' as Integer", i);
            }
            if (index > maxIndex || index < 0) {
                throw new ParseException("Index[" + i + "]=" + index + " is out of bounds [0," + maxIndex + "]", i);
            }
            indexes[i] = index;
        }
        return indexes;
    }

    public static Core[] parseCores(final String str,
                                    final int[] defaults) throws ParseException {
        final int[] indexes = parseCoresIndexes(str, defaults);
        final Core[] cores = new Core[indexes.length];
        for (int i = 0; i < cores.length; i++) {
            cores[i] = cores()[indexes[i]];
        }
        return cores;
    }
}
