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
import java.util.ArrayList;
import java.util.List;
import java.util.jar.JarFile;

import javax.annotation.Nonnull;

/**
 * Main class of Jill.
 */
public class Jill {

  @Nonnull
  private final Options options;

  @Nonnull
  private final String version;

  @Nonnull
  public static final String FILE_ENCODING = "UTF-8";

  public Jill(@Nonnull Options options, @Nonnull String version) {
    this.options = options;
    this.version = version;
  }

  public void process(@Nonnull File binaryFile) {
    if (binaryFile.isFile()) {
      if (FileUtils.isJavaBinaryFile(binaryFile)) {
          processJavaBinary(binaryFile);
      } else if (FileUtils.isJarFile(binaryFile)) {
          try {
            processJarFile(new JarFile(binaryFile));
          } catch (IOException e) {
            throw new JillException("Fails to create jar file " + binaryFile.getName(), e);
          }
      } else {
        throw new JillException("Unsupported file type: " + binaryFile.getName());
      }
    } else {
      processFolder(binaryFile);
    }
  }

  private void processJavaBinary(@Nonnull File javaBinaryFile) {
    assert javaBinaryFile.isFile();
    List<File> javaBinaryFiles = new ArrayList<File>();
    javaBinaryFiles.add(javaBinaryFile);
    new JavaTransformer(version, options).transform(javaBinaryFiles);
  }

  private void processJarFile(@Nonnull JarFile jarFile) {
    new JavaTransformer(version, options).transform(jarFile);
  }

  private void processFolder(@Nonnull File folder) {
    assert folder.isDirectory();
    List<File> javaBinaryFiles = new ArrayList<File>();
    FileUtils.getJavaBinaryFiles(folder, javaBinaryFiles);
    new JavaTransformer(version, options).transform(javaBinaryFiles);
  }

}
