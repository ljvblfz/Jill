/*
 * Copyright (C) 2012 The Android Open Source Project
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

package com.android.jill.shrob;

import com.android.jack.ProguardFlags;
import com.android.jill.JillTestTools;

import org.junit.BeforeClass;
import org.junit.Test;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import javax.annotation.Nonnull;

public class ShrinkTest {

  @Nonnull
  private static final File[] defaultBootclasspath = JillTestTools.getDefaultBootclasspath();

  private static ProguardFlags dontObfuscateFlagFile =
      new ProguardFlags(JillTestTools.getJackTestFolder("shrob"), "dontobfuscate.flags");

  @BeforeClass
  public static void setUpClass() {
    ShrinkTest.class.getClassLoader().setDefaultAssertionStatus(true);
  }

  protected ProguardFlags generateInjars(@Nonnull File injar) throws IOException {
    File injarFlags = JillTestTools.createTempFile("injars", ".flags");
    BufferedWriter writer = new BufferedWriter(new FileWriter(injarFlags));
    writer.append("-injars ");
    writer.append(injar.getAbsolutePath());
    writer.close();
    return new ProguardFlags(injarFlags);
  }

  @Test
  public void test27_001() throws Exception {
    File libzip = JillTestTools.createTempFile("lib", ".zip");
    JillTestTools.runJillToZip(JillTestTools.getJackTestLibFolder("shrob/test027"), libzip);
    String testName = "shrob/test027";
    File testFolder = JillTestTools.getJackTestFolder(testName);
    File refFolder = new File(testFolder, "refsShrinking");
    ProguardFlags[] proguardflagsFiles = new ProguardFlags[] {
        dontObfuscateFlagFile,
        new ProguardFlags(testFolder, "proguard.flags001"),
        generateInjars(libzip)};
    JillTestTools.checkListing(defaultBootclasspath, null,
        JillTestTools.getJackTestsWithJackFolder(testName), proguardflagsFiles,
        new File(refFolder, "expected-001.txt"));
  }
}
