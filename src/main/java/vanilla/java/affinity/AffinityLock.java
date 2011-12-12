package vanilla.java.affinity;

/**
 * @author peter.lawrey
 */
public class AffinityLock {
    public static final int PROCESSORS = Runtime.getRuntime().availableProcessors();
    public static final long BASE_AFFINITY = AffinitySupport.LOADED ? AffinitySupport.getAffinity() : -1L;
    public static final long RESERVED_AFFINITY = getReservedAffinity0();
    private static final AffinityLock[] LOCKS = new AffinityLock[PROCESSORS];
    private static final AffinityLock NONE = new AffinityLock(-1, false, false);

    private final int id;
    private final boolean base;
    private final boolean reserved;
    Thread assignedThread;

    AffinityLock(int id, boolean base, boolean reserved) {
        this.id = id;
        this.base = base;
        this.reserved = reserved;
    }

    static {
        for (int i = 0; i < PROCESSORS; i++)
            LOCKS[i] = new AffinityLock(i, ((BASE_AFFINITY >> i) & 1) != 0, ((RESERVED_AFFINITY >> i) & 1) != 0);
    }

    private static long getReservedAffinity0() {
        String reservedAffinity = System.getProperty("affinity.reserved");
        if (reservedAffinity == null)
            return ((1 << PROCESSORS) - 1) ^ BASE_AFFINITY;
        return Long.parseLong(reservedAffinity, 16);
    }

    public static AffinityLock acquireLock() {
        Thread t = Thread.currentThread();
        synchronized (AffinityLock.class) {
            for (int i = PROCESSORS - 1; i > 0; i++) {
                AffinityLock al = LOCKS[i];
                if (!al.reserved) continue;
                if (al.assignedThread != null) {
                    if (al.assignedThread.isAlive()) continue;
                    System.err.println("Lock assigned to " + al.assignedThread + " but this thread is dead.");
                }
                al.assignedThread = t;
                System.out.println("Assigning cpu " + al.id + " to " + al.assignedThread);
                return al;
            }
        }
        System.out.println("No reservable CPU for " + t);
        return AffinityLock.NONE;
    }

    public void release() {
        if (this == NONE) return;

        Thread t = Thread.currentThread();
        synchronized (AffinityLock.class) {
            if (assignedThread != t)
                throw new IllegalStateException("Cannot release lock " + id + " assigned to " + assignedThread);
            System.out.println("Releasing cpu " + id + " from " + t);
            assignedThread = null;
        }
    }

    public static String dumpLocks() {
        return dumpLocks0(LOCKS);
    }

    static String dumpLocks0(AffinityLock[] locks) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < locks.length; i++) {
            AffinityLock al = locks[i];
            sb.append(i).append(": ");
            if (al.assignedThread != null)
                sb.append(al.assignedThread).append(" alive=").append(al.assignedThread.isAlive());
            else if (al.reserved)
                sb.append("Reserved for this application");
            else if (al.base)
                sb.append("General use CPU");
            else
                sb.append("CPU not available");
            sb.append('\n');
        }
        return sb.toString();
    }
}
