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

import vanilla.java.affinity.CpuLayout;

import java.io.*;
import java.util.*;

import static java.lang.Integer.parseInt;

/**
 * @author peter.lawrey
 */
public class VanillaCpuLayout implements CpuLayout {
    public static final int MAX_CPUS_SUPPORTED = 64;
    private final List<CpuInfo> cpuDetails;

    VanillaCpuLayout(List<CpuInfo> cpuDetails) {
        this.cpuDetails = cpuDetails;
    }

    public static VanillaCpuLayout fromProperties(String fileName) throws IOException {
        return fromProperties(new FileInputStream(fileName));
    }

    public static VanillaCpuLayout fromProperties(InputStream is) throws IOException {
        Properties prop = new Properties();
        prop.load(is);
        return fromProperties(prop);
    }

    public static VanillaCpuLayout fromProperties(Properties prop) {
        List<CpuInfo> cpuDetails = new ArrayList<CpuInfo>();
        for (int i = 0; i < MAX_CPUS_SUPPORTED; i++) {
            String line = prop.getProperty("" + i);
            if (line == null) break;
            String[] word = line.trim().split(" *, *");
            CpuInfo details = new CpuInfo(parseInt(word[0]),
                    parseInt(word[1]), parseInt(word[2]));
            cpuDetails.add(details);
        }
        return new VanillaCpuLayout(cpuDetails);
    }

    public static VanillaCpuLayout fromCpuInfo() throws IOException {
        return fromCpuInfo("/proc/cpuinfo");
    }

    public static VanillaCpuLayout fromCpuInfo(String filename) throws IOException {
        return fromCpuInfo(new FileInputStream(filename));
    }

    public static VanillaCpuLayout fromCpuInfo(InputStream is) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(is, "UTF-8"));
        String line;
        List<CpuInfo> cpuDetails = new ArrayList<CpuInfo>();
        CpuInfo details = new CpuInfo();
        Map<String, Integer> threadCount = new LinkedHashMap<String, Integer>();

        while ((line = br.readLine()) != null) {
            if (line.trim().isEmpty()) {
                String key = details.socketId + "," + details.coreId;
                Integer count = threadCount.get(key);
                if (count == null)
                    threadCount.put(key, count = 1);
                else
                    threadCount.put(key, count += 1);
                details.threadId = count - 1;
                cpuDetails.add(details);
                details = new CpuInfo();
                continue;
            }
            String[] words = line.split("\\s*:\\s*", 2);
            if (words[0].equals("physical id"))
                details.socketId = parseInt(words[1]);
            else if (words[0].equals("core id"))
                details.coreId = parseInt(words[1]);
        }
        return new VanillaCpuLayout(cpuDetails);
    }

    @Override
    public int cpus() {
        return cpuDetails.size();
    }

    @Override
    public int socketId(int cpuId) {
        return cpuDetails.get(cpuId).socketId;
    }

    @Override
    public int coreId(int cpuId) {
        return cpuDetails.get(cpuId).coreId;
    }

    @Override
    public int threadId(int cpuId) {
        return cpuDetails.get(cpuId).threadId;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0, cpuDetailsSize = cpuDetails.size(); i < cpuDetailsSize; i++) {
            CpuInfo cpuDetail = cpuDetails.get(i);
            sb.append(i).append(": ").append(cpuDetail).append('\n');
        }
        return sb.toString();
    }

    static class CpuInfo {
        int socketId, coreId, threadId;

        CpuInfo() {
        }

        CpuInfo(int socketId, int coreId, int threadId) {
            this.socketId = socketId;
            this.coreId = coreId;
            this.threadId = threadId;
        }

        @Override
        public String toString() {
            return "CpuInfo{" +
                    "socketId=" + socketId +
                    ", coreId=" + coreId +
                    ", threadId=" + threadId +
                    '}';
        }
    }
}
