package vanilla.java.affinity;

public class AffinitySupportTest {
    @org.junit.Test
    public void testGetAffinity() throws Exception {
        AffinitySupport.getAffinity();
    }

    @org.junit.Test
    public void testSetAffinity() throws Exception {
        AffinitySupport.setAffinity(~0);

    }

    @org.junit.Test()
    public void testRdtsc() throws Exception {
        AffinitySupport.rdtsc();
    }
}
