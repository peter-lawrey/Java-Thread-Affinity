package vanilla.java.affinity.impl;

import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;

import static junit.framework.Assert.assertEquals;

/**
 * @author peter.lawrey
 */
public class VanillaCpuLayoutTest {

    public static final String EXPECTED = "0: CpuInfo{socketId=0, coreId=0, threadId=0}\n" +
            "1: CpuInfo{socketId=0, coreId=1, threadId=0}\n" +
            "2: CpuInfo{socketId=0, coreId=2, threadId=0}\n" +
            "3: CpuInfo{socketId=0, coreId=3, threadId=0}\n" +
            "4: CpuInfo{socketId=0, coreId=0, threadId=1}\n" +
            "5: CpuInfo{socketId=0, coreId=1, threadId=1}\n" +
            "6: CpuInfo{socketId=0, coreId=2, threadId=1}\n" +
            "7: CpuInfo{socketId=0, coreId=3, threadId=1}\n";

    @Test
    public void testFromCpuInfo() throws IOException {
        final InputStream i7 = getClass().getClassLoader().getResourceAsStream("cpuinfo.i7");
        VanillaCpuLayout vcl = VanillaCpuLayout.fromCpuInfo(i7);
        assertEquals(EXPECTED, vcl.toString());
    }

    @Test
    public void testFromProperties() throws IOException {
        final InputStream i7 = getClass().getClassLoader().getResourceAsStream("i7.properties");
        VanillaCpuLayout vcl = VanillaCpuLayout.fromProperties(i7);
        assertEquals(EXPECTED, vcl.toString());
    }
}
