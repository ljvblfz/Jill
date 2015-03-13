/*
 * Copyright (C) 2013 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.android.jill;

import com.android.jill.frontend.java.JavaTransformer;
import com.android.jill.utils.FileUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.jar.JarFile;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;

/**
 * Main class of Jill.
 */
public class Jill {

  @Nonnull
  private static final String PROPERTIES_FILE = "jill.properties";
  @CheckForNull
  private static Version version = null;

  public static void process(@Nonnull Options options) {
    File binaryFile = options.getBinaryFile();
    JavaTransformer jt = new JavaTransformer(getVersion().getVersion(), options);
    if (binaryFile.isFile()) {
      if (FileUtils.isJavaBinaryFile(binaryFile)) {
        List<File> javaBinaryFiles = new ArrayList<File>();
        javaBinaryFiles.add(binaryFile);
        jt.transform(javaBinaryFiles);
      } else if (FileUtils.isJarFile(binaryFile)) {
        try {
          jt.transform(new JarFile(binaryFile));
        } catch (IOException e) {
          throw new JillException("Fails to create jar file " + binaryFile.getName(), e);
        }
      } else {
        throw new JillException("Unsupported file type: " + binaryFile.getName());
      }
    } else {
      List<File> javaBinaryFiles = new ArrayList<File>();
      FileUtils.getJavaBinaryFiles(binaryFile, javaBinaryFiles);
      jt.transform(javaBinaryFiles);
    }
  }

  @Nonnull
  public static Version getVersion() {
    if (version == null) {
      InputStream is = Jill.class.getClassLoader().getResourceAsStream(PROPERTIES_FILE);
      if (is != null) {
        version = new Version(is);
      } else {
        System.err.println("Failed to open Jack properties file " + PROPERTIES_FILE);
        throw new AssertionError();
      }
    }

    assert version != null;
    return version;
  }

}
