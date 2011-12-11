package vanilla.java.affinity;

public class AffinitySupport {
    public native static long getAffinity();

    public native static void setAffinity(long affinity);

    public native static long rdtsc();
}
