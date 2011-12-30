package vanilla.java.affinity.impl;

import org.junit.*;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;


/**
 * @author cheremin
 * @since 29.12.11,  20:25
 */
public abstract class AbstractAffinityImplTest {

    protected static final int CORES = Runtime.getRuntime().availableProcessors();

    public abstract IAffinityImpl getImpl();


    @Test
    public void getAffinityCompletesGracefully() throws Exception {
        getImpl().getAffinity();
    }

    @Test
    public void getAffinityReturnsValidValue() throws Exception {
        final long affinity = getImpl().getAffinity();
        assertTrue(
                "Affinity mask " + affinity + " must be >0",
                affinity > 0
        );
        final int allCoresMask = ( 1 << CORES ) - 1;
        assertTrue(
                "Affinity mask " + affinity + " must be <=(2^" + CORES + "-1 = " + allCoresMask + ")",
                affinity <= allCoresMask
        );
    }

    @Test
    public void setAffinityCompletesGracefully() throws Exception {
        getImpl().setAffinity( 1 );
    }

    @Test
    public void getAffinityReturnsValuePreviouslySet() throws Exception {
        final IAffinityImpl impl = getImpl();
        final int cores = CORES;
        for ( int core = 0; core < cores; core++ ) {
            final long mask = ( 1 << core );
            getAffinityReturnsValuePreviouslySet( impl, mask );
        }
    }

    private void getAffinityReturnsValuePreviouslySet( final IAffinityImpl impl,
                                                       final long mask ) throws Exception {

        impl.setAffinity( mask );
        final long _mask = impl.getAffinity();
        assertEquals( mask, _mask );
    }

    @After
    public void tearDown() throws Exception {
        final int anyCore = ( 1 << CORES ) - 1;
        getImpl().setAffinity( anyCore );
    }
}
