package vanilla.java.affinity;

/**
 * @author peter.lawrey
 */
public interface AffinityStrategy {
    /**
     * @param cpuId  to cpudId to compare
     * @param cpuId2 with a second cpuId
     * @return true if it matches the criteria.
     */
    public boolean matches(int cpuId, int cpuId2);
}
