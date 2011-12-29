package vanilla.java.affinity;

public enum NullAffinity implements AffinitySupport.IAffinity {
    INSTANCE;

    @Override
    public long getAffinity() {
        return -1;
    }

    @Override
    public void setAffinity(long affinity) {

    }

    @Override
    public long nanoTime() {
        return System.nanoTime();
    }
}
