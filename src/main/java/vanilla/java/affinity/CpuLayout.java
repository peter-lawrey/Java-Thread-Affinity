package vanilla.java.affinity;

/**
 * @author peter.lawrey
 */
public interface CpuLayout {
    /**
     * @return the number of cpus.
     */
    public int cpus();

    /**
     * @param cpuId the logical processor number
     * @return which socket id this cpu is on.
     */
    public int socketId(int cpuId);

    /**
     * @param cpuId the logical processor number
     * @return which core on a socket this cpu is on.
     */
    public int coreId(int cpuId);

    /**
     * @param cpuId the logical processor number
     * @return which thread on a core this cpu is on.
     */
    public int threadId(int cpuId);
}
