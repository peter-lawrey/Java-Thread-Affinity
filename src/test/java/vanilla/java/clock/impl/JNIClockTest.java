package vanilla.java.clock.impl;

import org.junit.*;
import vanilla.java.affinity.NativeAffinity;

import static org.junit.Assert.*;

/**
 * fixme: Class DefaultClockTest is for test
 *
 * @author cheremin
 * @since 29.12.11,  19:12
 */
public class JNIClockTest {
    @BeforeClass
    public static void checkJniLibraryPresent() {
        Assume.assumeTrue( JNIClock.LOADED );
    }

    @Test
    public void testRdtsc() throws Exception {
        long l1 = JNIClock.rdtsc0();
        long l2 = JNIClock.rdtsc0();
        assertTrue( l2 > l1 );
        assertTrue( l2 < l1 + 1000000 );
    }

    @Test
    public void testRdtscPerf() {
        final int runs = 10 * 1000 * 1000;
        NativeAffinity.rdtsc0();
        long start = System.nanoTime();
        long start0 = NativeAffinity.rdtsc0();
        for ( int i = 0; i < runs; i++ ) {
            NativeAffinity.rdtsc0();
        }
        long time = System.nanoTime() - start;
        final long time0 = NativeAffinity.rdtsc0() - start0;
        long time2 = JNIClock.tscToNano( time0 );
        System.out.printf( "Each call took %.1f ns and the ratio was %.5f%n", ( double ) time / runs, ( double ) time2 / time );
    }
}