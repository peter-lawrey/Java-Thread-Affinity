package vanilla.java.affinity;

public enum AffinitySupport {
    ;

    interface IAffinity {
        public long getAffinity();

        public void setAffinity(long affinity);

        public long nanoTime();
    }

    private static final IAffinity affinityImpl;

    static {
        if (NativeAffinity.LOADED)
            affinityImpl = NativeAffinity.INSTANCE;
        else if (JNAAffinity.LOADED)
            affinityImpl = JNAAffinity.INSTANCE;
        else
            affinityImpl = NullAffinity.INSTANCE;
    }

    public static long getAffinity() {
        return affinityImpl.getAffinity();
    }

    public static void setAffinity(long affinity) {
        affinityImpl.setAffinity(affinity);
    }

    public static long nanoTime() {
        return affinityImpl.nanoTime();
    }
}
