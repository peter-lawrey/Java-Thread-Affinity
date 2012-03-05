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

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import vanilla.java.affinity.IAffinity;

/**
 * @author peter.lawrey
 */
public enum NativeAffinity implements IAffinity {
    INSTANCE;

    public static final boolean LOADED;
    private static final Logger LOGGER = Logger.getLogger(NativeAffinity.class
            .getName());

    static {
        LOADED = loadAffinityNativeLibrary();
    }

    private native static long getAffinity0();

    private native static void setAffinity0(long affinity);

    @Override
    public long getAffinity() {
        return getAffinity0();
    }

    @Override
    public void setAffinity(long affinity) {
        setAffinity0(affinity);
    }

    private static boolean initialize() {
        return loadAffinityNativeLibrary();
    }

    /**
     * Computes the MD5 value of the input stream
     * 
     * @param input
     * @return
     * @throws IOException
     * @throws NoSuchAlgorithmException
     */
    private static String md5sum(InputStream input) throws IOException {
        BufferedInputStream in = new BufferedInputStream(input);

        try {
            MessageDigest digest = java.security.MessageDigest
                    .getInstance("MD5");
            DigestInputStream digestInputStream = new DigestInputStream(in,
                    digest);
            for (; digestInputStream.read() >= 0;) {

            }
            ByteArrayOutputStream md5out = new ByteArrayOutputStream();
            md5out.write(digest.digest());
            return md5out.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("MD5 algorithm is not available: "
                    + e);
        } finally {
            in.close();
        }
    }

    /**
     * Extract the specified library file to the target folder
     * 
     * @param libFolderForCurrentOS
     * @param libraryFileName
     * @param targetFolder
     * @return
     */
    private static boolean extractAndLoadLibraryFile(
            String libFolderForCurrentOS, String libraryFileName,
            String targetFolder) {
        String nativeLibraryFilePath = libFolderForCurrentOS + "/"
                + libraryFileName;
        final String prefix = "javaaffinity-" + getVersion() + "-";

        String extractedLibFileName = prefix + libraryFileName;
        File extractedLibFile = new File(targetFolder, extractedLibFileName);

        try {
            if (extractedLibFile.exists()) {
                // test md5sum value
                String md5sum1 = md5sum(NativeAffinity.class
                        .getResourceAsStream(nativeLibraryFilePath));
                String md5sum2 = md5sum(new FileInputStream(extractedLibFile));

                if (md5sum1.equals(md5sum2)) {
                    return loadNativeLibrary(targetFolder, extractedLibFileName);
                } else {
                    // remove old native library file
                    boolean deletionSucceeded = extractedLibFile.delete();
                    if (!deletionSucceeded) {
                        throw new IOException(
                                "failed to remove existing native library file: "
                                        + extractedLibFile.getAbsolutePath());
                    }
                }
            }

            // extract file into the current directory
            InputStream reader = NativeAffinity.class
                    .getResourceAsStream(nativeLibraryFilePath);
            FileOutputStream writer = new FileOutputStream(extractedLibFile);
            byte[] buffer = new byte[1024];
            int bytesRead = 0;
            while ((bytesRead = reader.read(buffer)) != -1) {
                writer.write(buffer, 0, bytesRead);
            }

            writer.close();
            reader.close();

            if (!System.getProperty("os.name").contains("Windows")) {
                try {
                    Runtime.getRuntime()
                            .exec(new String[] { "chmod", "755",
                                    extractedLibFile.getAbsolutePath() })
                            .waitFor();
                } catch (Throwable e) {
                }
            }

        } catch (IOException e) {
            // TODO something with exception - don't know what to do with it
            // using JUL
            if (LOGGER.isLoggable(Level.INFO)) {
                LOGGER.info("Unable to extract libaffinity in " + targetFolder);
            }
            return false;
        }
        return loadNativeLibrary(targetFolder, extractedLibFileName);

    }

    private static synchronized boolean loadNativeLibrary(String path,
            String name) {
        File libPath = new File(path, name);
        if (libPath.exists()) {

            try {
                System.load(libPath.getAbsolutePath());
                return true;
            } catch (UnsatisfiedLinkError e) {
                // TODO something with e
                if (LOGGER.isLoggable(Level.INFO)) {
                    LOGGER.info("Unable to find libaffinity in " + path);
                }
                return false;
            }
        } else {
            return false;
        }
    }

    private static boolean loadAffinityNativeLibrary() {
        if (LOADED) {
            return LOADED;
        }
        String affinityNativeLibraryName = System.mapLibraryName("affinity");

        // Load the os-dependent library from a jar file
        String affinityNativeLibraryPath = "/vanilla/java/affinity/native/"
                + getNativeLibFolderPathForCurrentOS();

        if (NativeAffinity.class.getResource(affinityNativeLibraryPath
                + File.separator + affinityNativeLibraryName) == null) {
            return false;
        }

        // temporary library folder
        String tempFolder = new File(System.getProperty("java.io.tmpdir"))
                .getAbsolutePath();
        // Try extracting the library from jar
        return extractAndLoadLibraryFile(affinityNativeLibraryPath,
                affinityNativeLibraryName, tempFolder);
    }

    private static String getVersion() {

        InputStream versionFile = NativeAffinity.class
                .getResourceAsStream("/META-INF/maven/vanilla.java/affinity/pom.properties");

        String version = "unknown";
        try {
            if (versionFile != null) {
                Properties versionData = new Properties();
                versionData.load(versionFile);
                version = versionData.getProperty("version", version);
                version = version.trim().replaceAll("[^0-9\\.]", "");
            }
        } catch (IOException e) {
            if (LOGGER.isLoggable(Level.INFO)) {
                LOGGER.info("Unable to find libaffinity version from maven metadata");
            }
        }
        return version;
    }

    private static String getNativeLibFolderPathForCurrentOS() {
        return getOSName() + "/" + getArchName();
    }

    private static String getOSName() {
        return translateOSNameToFolderName(System.getProperty("os.name"));
    }

    private static String getArchName() {
        return translateArchNameToFolderName(System.getProperty("os.arch"));
    }

    private static String translateOSNameToFolderName(String osName) {
        if (osName.contains("Windows")) {
            return "Windows";
        } else if (osName.contains("Mac")) {
            return "Mac";
        } else if (osName.contains("Linux")) {
            return "Linux";
        } else {
            return osName.replaceAll("\\W", "");
        }
    }

    private static String translateArchNameToFolderName(String archName) {
        return archName.replaceAll("\\W", "");
    }

}
