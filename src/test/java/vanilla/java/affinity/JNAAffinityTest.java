package vanilla.java.affinity;

import org.junit.Test;

public class JNAAffinityTest {
    @Test
    public void testNanoTimePerf() {
        final int runs = 10 * 1000 * 1000;
        JNAAffinity.INSTANCE.nanoTime();
        long start = System.nanoTime();
        long start0 = JNAAffinity.INSTANCE.nanoTime();
        for (int i = 0; i < runs; i++)
            JNAAffinity.INSTANCE.nanoTime();
        long time = System.nanoTime() - start;
        final long time0 = JNAAffinity.INSTANCE.nanoTime() - start0;
        long time2 = NativeAffinity.tscToNano(time0);
        System.out.printf("Each call took %.1f ns and the ratio was %.5f%n", (double) time / runs, (double) time2 / time);
    }
}
