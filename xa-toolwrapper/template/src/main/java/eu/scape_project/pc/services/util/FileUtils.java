/*
 *  Copyright (c) 2011 The SCAPE Project Partners.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *  under the License.
 */

package ${global_package_name}.util;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * These are generic file utilities used by the client and service.
 * Based on PLANETS eu.planets_project.services.utils.FileUtils
 *
 * @author Thomas Kraemer (thomas.kraemer@uni-koeln.de),
 * @author Peter Melms (peter.melms@uni-koeln.de)
 * @author shsdev (shsschlarb-scape@yahoo.de)
 * @version 0.5
 */
public final class FileUtils {

    public static final String JAVA_TMP = System.getProperty("java.io.tmpdir");
    private static final String TMP_DIR = "simplecopy10-tmp-store";
    private static final int BUFF = 32768;
    private static Logger logger = LoggerFactory.getLogger(FileUtils.class.getName());

    /**
     * Empty private constructor avoids instantiation.
     */
    private FileUtils() {
    }

    /**
     * Get system's JAVA temporary directory.
     * @return System's JAVA temporary directory
     */
    public static File getSystemTempFolder() {
        return new File(JAVA_TMP);
    }

    /**
     * Get temporary file
     * @param name File name
     * @param suffix Suffix
     * @return Temp file folder
     */
    public static File getTmpFile(final String name, final String suffix) {
        String suffixToUse = suffix == null ? ".tmp" : suffix;
        if (!suffixToUse.startsWith(".")) {
            suffixToUse = "." + suffix;
        }
        String nameToUse = name == null ? "tmp" : name;
        File input = null;
        try {
            File folder = new File(JAVA_TMP, FileUtils.TMP_DIR);
            if (!folder.exists()) {
                boolean mkdirs = folder.mkdirs();
                checkCreation(folder, mkdirs);
            }
            input = File.createTempFile(nameToUse, suffixToUse, folder);
            input.deleteOnExit();
        } catch (IOException e) {
            logger.error("An error occurred while creating the temporary file: \""
                    + name+suffix+"\"");
        }
        return input;
    }

    /**
     * @param folder The file we tried to create
     * @param mkdirs The result of creating the file
     */
    private static void checkCreation(final File folder, final boolean mkdirs) {
        logger.info(String.format("Created folder '%s': %s", folder, mkdirs));
        if (!folder.exists()) {
            throw new IllegalArgumentException(String.format(
                    "Could not create '%s'", folder));
        }
    }

    /**
     * Read file of URL into file.
     * @param url URL where the input file is located
     * @return Result file
     */
    public static File urlToFile(URL url, String ext) {
        File fOut = null;
        try {
            fOut = getTmpFile("fromurl", "."+ext);

            URLConnection uc = url.openConnection();
            logger.info("ContentType: " + uc.getContentType());
            InputStream in = uc.getInputStream();
            org.apache.commons.io.FileUtils.copyInputStreamToFile(in, fOut);
            logger.info("File of length " + fOut.length() + " created from URL " + url.toString());

            in.close();
        } catch (IOException ex) {
            logger.error("I/O Exception occurred while trying to read from URL " + url.toString());
        }
        return fOut;
    }
   
}
