package vanilla.java.affinity.impl;

/**
 * Implementation interface
 *
 * @author cheremin
 * @since 29.12.11,  20:14
 */
public interface IAffinityImpl {
    /**
     * @return returns affinity mask for current thread
     */
    public long getAffinity();

    /**
     * @param affinity sets affinity mask of current thread to specified value
     */
    public void setAffinity( final long affinity );
}
