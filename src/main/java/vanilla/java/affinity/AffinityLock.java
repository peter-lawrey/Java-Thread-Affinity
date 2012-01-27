/*
 * Copyright 2011 Peter Lawrey
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package vanilla.java.affinity;

import vanilla.java.affinity.impl.NoCpuLayout;
import vanilla.java.affinity.impl.VanillaCpuLayout;

import java.io.File;
import java.io.IOException;
import java.util.NavigableMap;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author peter.lawrey
 */
public class AffinityLock {
    // TODO It seems like on virtualized platforms .availableProcessors() value can change at
    // TODO runtime. We should think about how to adopt to such change

    // Static fields and methods.
    public static final String AFFINITY_RESERVED = "affinity.reserved";

    public static final int PROCESSORS = Runtime.getRuntime().availableProcessors();
    public static final long BASE_AFFINITY = AffinitySupport.getAffinity();
    public static final long RESERVED_AFFINITY = getReservedAffinity0();

    private static final Logger LOGGER = Logger.getLogger(AffinityLock.class.getName());

    private static final AffinityLock[] LOCKS = new AffinityLock[PROCESSORS];
    private static NavigableMap<Integer, AffinityLock[]> CORES; // set by cpuLayout()
    private static final AffinityLock NONE = new AffinityLock(-1, false, false);
    private static CpuLayout cpuLayout = new NoCpuLayout(PROCESSORS);

    static {
        for (int i = 0; i < PROCESSORS; i++)
            LOCKS[i] = new AffinityLock(i, ((BASE_AFFINITY >> i) & 1) != 0, ((RESERVED_AFFINITY >> i) & 1) != 0);
        try {
            if (new File("/proc/cpuinfo").exists())
                cpuLayout(VanillaCpuLayout.fromCpuInfo());
        } catch (IOException e) {
            LOGGER.log(Level.WARNING, "Unable to load /proc/cpuinfo", e);
        }
    }

    public static void cpuLayout(CpuLayout cpuLayout) {
        synchronized (AffinityLock.class) {
            AffinityLock.cpuLayout = cpuLayout;
            int cores = cpuLayout.sockets() * cpuLayout.coresPerSocket();
            int threads = cpuLayout.threadsPerCore();
            CORES = new TreeMap<Integer, AffinityLock[]>();
            for (AffinityLock al : LOCKS) {
                final int id = al.id;
                int core = coreForId(id);
                AffinityLock[] als = CORES.get(core);
                if (als == null)
                    CORES.put(core, als = new AffinityLock[threads]);
                als[cpuLayout.threadId(id)] = al;
            }
        }
    }

    private static int coreForId(int id) {
        return cpuLayout.socketId(id) * cpuLayout.coresPerSocket() + cpuLayout.coreId(id);
    }

    public static CpuLayout cpuLayout() {
        return cpuLayout;
    }

    private static long getReservedAffinity0() {
        String reservedAffinity = System.getProperty(AFFINITY_RESERVED);
        if (reservedAffinity == null || reservedAffinity.trim().isEmpty())
            return ((1 << PROCESSORS) - 1) ^ BASE_AFFINITY;
        return Long.parseLong(reservedAffinity, 16);
    }

    public static AffinityLock acquireLock() {
        return acquireLock(true);
    }

    public static AffinityLock acquireCore() {
        return acquireCore(true);
    }

    public static AffinityLock acquireLock(boolean bind) {
        return acquireLock(bind, -1, AffinityStrategies.ANY);
    }

    public static AffinityLock acquireCore(boolean bind) {
        return acquireCore(bind, -1, AffinityStrategies.ANY);
    }

    private static AffinityLock acquireLock(boolean bind, int cpuId, AffinityStrategy... strategies) {
        synchronized (AffinityLock.class) {
            for (AffinityStrategy strategy : strategies) {
                for (int i = PROCESSORS - 1; i > 0; i--) {
                    AffinityLock al = LOCKS[i];
                    if (al.canReserve() && strategy.matches(cpuId, i)) {
                        al.assignCurrentThread(bind, false);
                        return al;
                    }
                }
            }
        }
        if (LOGGER.isLoggable(Level.WARNING))
            LOGGER.warning("No reservable CPU for " + Thread.currentThread());
        return AffinityLock.NONE;
    }

    private static AffinityLock acquireCore(boolean bind, int cpuId, AffinityStrategy... strategies) {
        synchronized (AffinityLock.class) {
            for (AffinityStrategy strategy : strategies) {
                LOOP:
                for (AffinityLock[] als : CORES.descendingMap().values()) {
                    for (AffinityLock al : als) {
                        int core = coreForId(al.id);
                        if (!al.canReserve() || !strategy.matches(cpuId, core))
                            continue LOOP;
                    }
                    final AffinityLock al = als[0];
                    al.assignCurrentThread(bind, true);
                    return al;
                }
            }
        }
        if (LOGGER.isLoggable(Level.WARNING))
            LOGGER.warning("No reservable Core for " + Thread.currentThread());
        return acquireLock(bind, cpuId, strategies);
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

    //// Non static fields and methods.
    private final int id;
    private final boolean base;
    private final boolean reserved;
    boolean bound = false;
    Thread assignedThread;

    AffinityLock(int id, boolean base, boolean reserved) {
        this.id = id;
        this.base = base;
        this.reserved = reserved;
    }

    private void assignCurrentThread(boolean bind, boolean wholeCore) {
        assignedThread = Thread.currentThread();
        if (bind)
            bind(wholeCore);
    }

    public void bind() {
        bind(false);
    }

    public void bind(boolean wholeCore) {
        if (bound && assignedThread != null && assignedThread.isAlive())
            throw new IllegalStateException("cpu " + id + " already bound to " + assignedThread);

        if (wholeCore) {
            int core = coreForId(id);
            for (AffinityLock al : CORES.get(core)) {
                if (bound && al.assignedThread != null && al.assignedThread.isAlive()) {
                    LOGGER.severe("cpu " + al.id + " already bound to " + al.assignedThread);
                } else {
                    al.bound = true;
                    al.assignedThread = Thread.currentThread();
                }
            }
            if (LOGGER.isLoggable(Level.INFO)) {
                StringBuilder sb = new StringBuilder().append("Assigning core ").append(core);
                String sep = ": cpus ";
                for (AffinityLock al : CORES.get(core)) {
                    sb.append(sep).append(al.id);
                    sep = ", ";
                }
                sb.append(" to ").append(assignedThread);
                LOGGER.info(sb.toString());
            }
        } else {
            bound = true;
            assignedThread = Thread.currentThread();
            if (LOGGER.isLoggable(Level.INFO))
                LOGGER.info("Assigning cpu " + id + " to " + assignedThread);
        }
        AffinitySupport.setAffinity(1L << id);
    }

    private boolean canReserve() {
        if (!reserved) return false;
        if (assignedThread != null) {
            if (assignedThread.isAlive()) return false;
            LOGGER.severe("Lock assigned to " + assignedThread + " but this thread is dead.");
        }
        return true;
    }

    public AffinityLock acquireLock(AffinityStrategy... strategies) {
        return acquireLock(false, id, strategies);
    }

    public void release() {
        Thread t = Thread.currentThread();
        synchronized (AffinityLock.class) {
            for (AffinityLock al : LOCKS) {
                Thread at = al.assignedThread;
                if (at == t) {
                    if (LOGGER.isLoggable(Level.INFO))
                        LOGGER.info("Releasing cpu " + al.id + " from " + t);
                    al.assignedThread = null;
                    al.bound = false;
                } else if (at != null && !at.isAlive()) {
                    LOGGER.warning("Releasing cpu " + al.id + " from " + t + " as it is not alive.");
                    al.assignedThread = null;
                    al.bound = false;
                }
            }
        }
        AffinitySupport.setAffinity(BASE_AFFINITY);
    }
}
