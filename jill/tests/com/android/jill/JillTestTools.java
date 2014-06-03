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
import com.android.jack.Options;
import com.android.jack.ProguardFlags;
import com.android.jack.TestTools;
import com.android.jack.util.ExecuteFile;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;

public class JillTestTools extends TestTools {

  @Nonnull
  private static final File JILL = getFromAndroidTree("out/host/linux-x86/framework/jill.jar");

  public static void runJill(@Nonnull File inputFile, @Nonnull File outputFile) throws Exception {
    String[] args = new String[] {"java",
        "-jar",
        JILL.getAbsolutePath(),
        "-o",
        outputFile.getAbsolutePath(),
        inputFile.getAbsolutePath()};

    ExecuteFile execFile = new ExecuteFile(args);
    if (!execFile.run()) {
      throw new RuntimeException("Call to jill exited with an error");
    }
  }

  public static void runJillToZip(@Nonnull File inputFile, @Nonnull File outputFile) throws Exception {
    String[] args = new String[] {"java",
        "-jar",
        JillTestTools.JILL.getAbsolutePath(),
        "-o",
        outputFile.getAbsolutePath(),
        inputFile.getAbsolutePath(),
        "-v",
        "-c", "zip"};

    ExecuteFile execFile = new ExecuteFile(args);
    execFile.setErr(System.err);
    execFile.setOut(System.out);
    execFile.setVerbose(true);
    if (!execFile.run()) {
      throw new RuntimeException("Call to jill exited with an error");
    }
  }

  public static void checkStructureWithJill(@CheckForNull File[] refBootclasspath,
      @CheckForNull File[] refClasspath,
      @Nonnull File fileOrSourceList,
      boolean withDebugInfo) throws Exception {
    checkStructureWithJill(refBootclasspath,
        refClasspath,
        fileOrSourceList,
        withDebugInfo,
        null,
        null);
  }

  public static void checkStructureWithJill(@CheckForNull File[] bootclasspath,
      @CheckForNull File[] classpath,
      @Nonnull File fileOrSourceList,
      boolean withDebugInfo,
      @CheckForNull JarJarRules jarjarRules,
      @CheckForNull ProguardFlags[] proguardFlags) throws Exception {

    Options options = buildCommandLineArgs(bootclasspath, classpath, fileOrSourceList);

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
    File jackFile = new File(testDir, "test.jack");
    if (!jackFile.exists() && !jackFile.mkdirs()) {
      throw new IOException("Could not create directory \"" + testDir.getName() + "\"");
    }
    runJill(refJar, jackFile);

    // Run Jack on .jack
    File jackDex = new File(testDir, "testjack.dex");
    compileJackToDex(new Options(), jackFile, jackDex, false);

    // Compare Jack Dex file to reference
    new DexComparator().compare(refDex, jackDex, withDebugInfo, /* strict */false,
        /* compareDebugInfoBinary */ false, /* compareInstructionNumber */ false, 0f);
  }
}
