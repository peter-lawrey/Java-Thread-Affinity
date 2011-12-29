package vanilla.java.affinity;

/**
 * @author peter.lawrey
 */
public class AffinityLockMain {
    public static void main(String... args) throws InterruptedException {
        new Thread(new SleepRunnable(), "engine").start();
        new Thread(new SleepRunnable(), "reader").start();
        new Thread(new SleepRunnable(), "writer").start();
        Thread.sleep(100);
        System.out.println(AffinityLock.dumpLocks());
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
