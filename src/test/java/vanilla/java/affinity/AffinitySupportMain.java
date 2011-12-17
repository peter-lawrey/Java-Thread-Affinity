package vanilla.java.affinity;

/**
 * @author peter.lawrey
 */
public class AffinitySupportMain {
    public static void main(String... args) {
        AffinityLock al = AffinityLock.acquireLock();
        try {
            new Thread(new SleepRunnable(), "reader").start();
            new Thread(new SleepRunnable(), "writer").start();
            new Thread(new SleepRunnable(), "engine").start();
        } finally {
            al.release();
        }
    }

    private static class SleepRunnable implements Runnable {
        public void run() {
            AffinityLock al = AffinityLock.acquireLock();
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
            } finally {
                al.release();
            }
        }
    }
}
