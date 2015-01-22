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

import com.android.jack.DexComparator;
import com.android.jack.JarJarRules;
import com.android.jack.ProguardFlags;
import com.android.jack.TestTools;
import com.android.jack.backend.dex.DexFileWriter;
import com.android.jack.test.TestsProperties;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;

public class JillTestTools extends TestTools {

  @Nonnull
  private static final String JILL_UNIT_TESTS_PATH = "jill/tests/";

  @Nonnull
  private static final String JILL_PACKAGE = "com/android/jill/";

  @Nonnull
  private static final File JILL_DIR = new File(TestsProperties.getJackRootDir(), "../jill");

  @Nonnull
  private static final File JILL = new File(JILL_DIR, "dist/jill.jar");

  @Nonnull
  public static File getJillTestFolder(@Nonnull String testName) {
    return new File(JILL_DIR, JILL_UNIT_TESTS_PATH + JILL_PACKAGE + testName);
  }

  public static void runJill(@Nonnull File inputFile, @Nonnull File outputFile) throws Exception {
    String[] args = new String[] {
      "--output",
      outputFile.getAbsolutePath(),
      inputFile.getAbsolutePath()};

    Options options = Main.getOptions(args);
    new Jill(options, "").process(options.getBinaryFile());
  }

  public static void runJillToZip(@Nonnull File inputFile, @Nonnull File outputFile) throws Exception {
    String[] args = new String[] {"--output",
        outputFile.getAbsolutePath(),
        inputFile.getAbsolutePath()};
    Options options = Main.getOptions(args);
    new Jill(options, "").process(options.getBinaryFile());
  }

  public static void checkStructureWithJill(
      @CheckForNull File[] refClasspath,
      @Nonnull File fileOrSourceList,
      boolean withDebugInfo) throws Exception {
    checkStructureWithJill(
        refClasspath,
        fileOrSourceList,
        withDebugInfo,
        null,
        null);
  }

  public static void checkStructureWithJill(
      @CheckForNull File[] classpath,
      @Nonnull File fileOrSourceList,
      boolean withDebugInfo,
      @CheckForNull JarJarRules jarjarRules,
      @CheckForNull ProguardFlags[] proguardFlags) throws Exception {

    com.android.jack.Options options =
        buildCommandLineArgs(classpath, fileOrSourceList);

    boolean useEcjAsRefCompiler = withDebugInfo;

    // Prepare files and directories
    File testDir = createTempDir("jillTest", null);

    if (withDebugInfo) {
      List<String> ecjArguments = new ArrayList<String>(options.getEcjArguments());
      ecjArguments.add(0, "-g");
      options.setEcjArguments(ecjArguments);
    }

    ReferenceCompilerFiles files = createReferenceCompilerFiles(testDir,
        options,
        proguardFlags,
        null,
        null,
        withDebugInfo,
        useEcjAsRefCompiler,
        jarjarRules);

    File refJar = files.jarFile;
    File refDex = files.dexFile;

    // Run Jill on generated class file
    File jackFile = new File(testDir, "test.jayce");
    if (!jackFile.exists() && !jackFile.mkdirs()) {
      throw new IOException("Could not create directory \"" + testDir.getName() + "\"");
    }
    runJill(refJar, jackFile);

    // Run Jack on .jack
    File jackDexFolder = TestTools.createTempDir("jack", "dex");
    compileJackToDex(new com.android.jack.Options(), jackFile, jackDexFolder, false);

    // Compare Jack Dex file to reference
    new DexComparator(withDebugInfo, /* strict */false, /* compareDebugInfoBinary */ false,
        /* compareInstructionNumber */ false, 0f).compare(refDex, new File(jackDexFolder, DexFileWriter.DEX_FILENAME));
  }
}
