/*
 * Copyright 2011 Peter Lawrey
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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
