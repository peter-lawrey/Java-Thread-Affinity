package vanilla.java.affinity.impl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Assert;
import org.junit.Test;

import static com.google.common.base.Preconditions.*;

/**
 * fixme: Class AbstractAffinityImplTest is for porn
 *
 * @author cheremin
 * @since 29.12.11,  20:25
 */
public class AbstractAffinityImplTest {
    private static final Log log = LogFactory.getLog( AbstractAffinityImplTest.class );

    @Test
    public void getAffinityCompletesGracefully() throws Exception {
        JNAAffinity.INSTANCE.getAffinity();
    }

    @Test
    public void setAffinityCompletesGracefully() throws Exception {
        JNAAffinity.INSTANCE.setAffinity( 1 );
    }

    @Test
    public void getAffinityReturnsValuePreviouslySet() throws Exception {
        JNAAffinity.INSTANCE.setAffinity( 1 );
        final long affinity = JNAAffinity.INSTANCE.getAffinity();
        Assert.assertEquals( 1, affinity );
    }
}
