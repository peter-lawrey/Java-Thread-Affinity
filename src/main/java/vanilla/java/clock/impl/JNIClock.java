package vanilla.java.clock.impl;


import vanilla.java.affinity.impl.NativeAffinity;
import vanilla.java.clock.IClock;

/**
 * JNI-based implementation, trying to use rdtsc() system call
 * to access the most precise timer available
 *
 * @author cheremin
 * @since 29.12.11,  18:56
 */
public enum JNIClock implements IClock {
    INSTANCE;


    public static final boolean LOADED;
    private static final int FACTOR_BITS = 17;
    private static long RDTSC_FACTOR = 1 << FACTOR_BITS;
    private static long CPU_FREQUENCY = 1000;
    private static final long START;

    static {
        boolean loaded;
        long start;
        try {
            System.loadLibrary( "affinity" );
            estimateFrequency( 50 );
            estimateFrequency( 200 );
            System.out.println( "Estimated clock frequency was " + CPU_FREQUENCY + " MHz" );
            start = rdtsc0();
            loaded = true;
        } catch ( UnsatisfiedLinkError ule ) {
            System.out.println( "Debug: Unable to find lib affinity in " + System.getProperty( "java.library.path" ) + " " + ule );
            start = 0;
            loaded = false;
        }
        LOADED = loaded;
        START = start;
    }

    public long nanoTime() {
        return tscToNano( rdtsc0() - START );
    }


    static long tscToNano( final long tsc ) {
        return ( tsc * RDTSC_FACTOR ) >> FACTOR_BITS;
    }

    private static void estimateFrequency( int factor ) {
        final long start = System.nanoTime();
        long now;
        while ( ( now = System.nanoTime() ) == start ) {
        }

        long end = start + factor * 1000000;
        final long start0 = rdtsc0();
        while ( ( now = System.nanoTime() ) < end ) {
        }
        long end0 = rdtsc0();
        end = now;

        RDTSC_FACTOR = ( ( end - start ) << FACTOR_BITS ) / ( end0 - start0 ) - 1;
        CPU_FREQUENCY = ( end0 - start0 + 1 ) * 1000 / ( end - start );
    }

    static long rdtsc0() {
        //todo move in this class
        return NativeAffinity.rdtsc0();
    }
}
