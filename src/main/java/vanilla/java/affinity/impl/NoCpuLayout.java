package vanilla.java.affinity.impl;

import vanilla.java.affinity.CpuLayout;

/**
 * This assumes there is one socket with every cpu on a different core.
 *
 * @author peter.lawrey
 */
public class NoCpuLayout implements CpuLayout {
    private final int cpus;

    public NoCpuLayout(int cpus) {
        this.cpus = cpus;
    }


    public int cpus() {
        return cpus;
    }

    @Override
    public int socketId(int cpuId) {
        return 0;
    }

    @Override
    public int coreId(int cpuId) {
        return cpuId;
    }

    @Override
    public int threadId(int cpuId) {
        return 0;
    }
}
