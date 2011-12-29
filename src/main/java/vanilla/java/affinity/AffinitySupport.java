package vanilla.java.affinity;

public class NativeAffinity {
    public static final boolean LOADED;
    private static final int FACTOR_BITS = 17;
    private static long RDTSC_FACTOR = 1 << FACTOR_BITS;
    private static long CPU_FREQUENCY = 1000;
    private static final long START;

    static {
        boolean loaded;
        long start;
        try {
            System.loadLibrary("affinity");
            estimateFrequency(50);
            estimateFrequency(200);
            System.out.println("Estimated clock frequency was " + CPU_FREQUENCY + " MHz");
            start = rdtsc();
            loaded = true;
        } catch (UnsatisfiedLinkError ule) {
            System.err.println("Unable to find libaffinity in " + System.getProperty("java.library.path"));
            start = 0;
            loaded = false;
        }
        LOADED = loaded;
        START = start;
    }

    private static void estimateFrequency(int factor) {
        long now, start = System.nanoTime();
        while ((now = System.nanoTime()) == start) ;
        long start0 = rdtsc();
        long end = start + factor * 1000000;
        while ((now = System.nanoTime()) < end) ;
        long end0 = rdtsc();
        end = now;
        RDTSC_FACTOR = ((end - start) << FACTOR_BITS) / (end0 - start0) - 1;
        CPU_FREQUENCY = (end0 - start0 + 1) * 1000 / (end - start);
    }

    public native static long getAffinity();

    public native static void setAffinity(long affinity);

    public native static long rdtsc();

    public static long nanoTime() {
        return LOADED ? tscToNano(rdtsc() - START) : System.nanoTime();
    }

    public static long tscToNano(long tsc) {
        return (tsc * RDTSC_FACTOR) >> FACTOR_BITS;
    }
}
