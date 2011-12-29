package vanilla.java.affinity;

class InterrupedThread implements Runnable {
    public void run() {
        try {
            Thread.sleep(Integer.MAX_VALUE);
        } catch (InterruptedException e) {
        }
    }
}
