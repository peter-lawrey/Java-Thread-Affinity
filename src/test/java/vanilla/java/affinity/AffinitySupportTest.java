package vanilla.java.affinity;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * @author peter.lawrey
 */
public class NativeAffinityTest {
    @Test
    public void testGetAffinity() throws Exception {
        long a = NativeAffinity.getAffinity();
        assertFalse(a == 0);
        assertFalse(a == -1);
    }

    @Test
    public void testSetAffinity() throws Exception {
        NativeAffinity.setAffinity(0x1);
        assertEquals(0x1, NativeAffinity.getAffinity());

        NativeAffinity.setAffinity(0x2);
        assertEquals(0x2, NativeAffinity.getAffinity());
    }

    @Test
    public void testRdtsc() throws Exception {
        long l1 = NativeAffinity.rdtsc();
        long l2 = NativeAffinity.rdtsc();
        assertTrue(l2 > l1);
        assertTrue(l2 < l1 + 1000000);
    }

    @Test
    public void dumpLocks() {
        AffinityLock[] locks = {
                new AffinityLock(0, true, false),
                new AffinityLock(1, false, false),
                new AffinityLock(2, false, true),
                new AffinityLock(3, false, true),
                new AffinityLock(4, true, false),
                new AffinityLock(5, false, false),
                new AffinityLock(6, false, true),
                new AffinityLock(7, false, true),
        };
        locks[2].assignedThread = new Thread(new InterrupedThread(), "logger");
        locks[2].assignedThread.start();
        locks[3].assignedThread = new Thread(new InterrupedThread(), "engine");
        locks[3].assignedThread.start();
        locks[6].assignedThread = new Thread(new InterrupedThread(), "main");
        locks[7].assignedThread = new Thread(new InterrupedThread(), "tcp");
        locks[7].assignedThread.start();
        assertEquals("0: General use CPU\n" +
                "1: CPU not available\n" +
                "2: Thread[logger,5,main] alive=true\n" +
                "3: Thread[engine,5,main] alive=true\n" +
                "4: General use CPU\n" +
                "5: CPU not available\n" +
                "6: Thread[main,5,main] alive=false\n" +
                "7: Thread[tcp,5,main] alive=true\n", AffinityLock.dumpLocks0(locks));

        locks[2].assignedThread.interrupt();
        locks[3].assignedThread.interrupt();
        locks[7].assignedThread.interrupt();
    }

    static class InterrupedThread implements Runnable {
        public void run() {
            try {
                Thread.sleep(Integer.MAX_VALUE);
            } catch (InterruptedException e) {
            }
        }
    }

    @Test
    public void testRdtscPerf() {
        final int runs = 10 * 1000 * 1000;
        NativeAffinity.rdtsc();
        long start = System.nanoTime();
        long start0 = NativeAffinity.rdtsc();
        for (int i = 0; i < runs; i++)
            NativeAffinity.rdtsc();
        long time = System.nanoTime() - start;
        final long time0 = NativeAffinity.rdtsc() - start0;
        long time2 = NativeAffinity.tscToNano(time0);
        System.out.printf("Each call took %.1f ns and the ratio was %.5f%n", (double) time / runs, (double) time2 / time);
    }
}
