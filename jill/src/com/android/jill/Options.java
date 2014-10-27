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

import com.android.jill.utils.FileUtils;

import org.kohsuke.args4j.Argument;
import org.kohsuke.args4j.Option;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;

/**
 * Jill command line options.
 */
public class Options {

  @CheckForNull
  @Argument(
      usage =
      "class files to be transformed contained recursively in directories or in a zip/jar file",
      metaVar = "FILE|DIRECTORY")
  private File binaryFile;

  @Option(name = "--verbose", usage = "enable verbosity (default: false)")
  private  boolean verbose = false;

  @Option(name = "-h", aliases = "--help", usage = "display help")
  protected boolean help;

  @CheckForNull
  @Option(name = "--output", usage = "output file", metaVar = "FILE")
  protected File outputDirOrZip;

  @Option(name = "--version", usage = "display version")
  protected boolean version;

  protected ContainerType container = ContainerType.ZIP;

  @Option(name = "--no-debug", usage = "disable debug info emission")
  protected boolean emitDebugInfo = true;

  public void checkValidity() throws IllegalOptionsException {
    if (askForVersion() || askForHelp()) {
      return;
    }

    if (binaryFile != null) {
      checkBinaryFileValidity();
    } else {
      throw new IllegalOptionsException("Input file not provided");
    }
    if (outputDirOrZip != null) {
      if (container == ContainerType.DIR) {
        checkOutputDir();
      }
    } else {
      throw new IllegalOptionsException("Output directory not provided");
    }
  }

  public void setBinaryFile(@Nonnull File binaryFile) {
    this.binaryFile = binaryFile;
  }

  @Nonnull
  public File getOutputDir() {
    assert outputDirOrZip != null;
    return outputDirOrZip;
  }

  @Nonnull
  public File getBinaryFile() {
    assert binaryFile != null;
    return binaryFile;
  }

  public boolean askForVersion() {
    return version;
  }

  public boolean askForHelp() {
    return help;
  }

  public void setVerbose(boolean verbose) {
    this.verbose = verbose;
  }

  public boolean isVerbose() {
    return verbose;
  }

  public boolean isEmitDebugInfo() {
    return emitDebugInfo;
  }

  @Nonnull
  public ContainerType getContainer() {
    return container;
  }

  private void checkBinaryFileValidity() throws IllegalOptionsException {
    assert binaryFile != null;

    if (!binaryFile.exists()) {
      throw new IllegalOptionsException(binaryFile.getName() + " does not exists.");
    }

    if (binaryFile.isFile() && FileUtils.isJarFile(binaryFile)) {
      return;
    }

    if (binaryFile.isFile() && !FileUtils.isJavaBinaryFile(binaryFile)
        && !FileUtils.isJarFile(binaryFile)) {
      throw new IllegalOptionsException(binaryFile.getName() + " is not a supported binary file.");
    }

    List<File> binaryFiles = new ArrayList<File>();
    FileUtils.getJavaBinaryFiles(binaryFile, binaryFiles);
    if (binaryFiles.isEmpty()) {
      System.err.println("Warning: Folder " + binaryFile.getName()
          + " does not contains class files.");
    }
  }

  private void checkOutputDir() throws IllegalOptionsException {
    assert outputDirOrZip != null;

    if (!outputDirOrZip.exists()) {
      throw new IllegalOptionsException(outputDirOrZip.getName() + " does not exist.");
    }

    if (!outputDirOrZip.canRead() || !outputDirOrZip.canWrite()) {
      throw new IllegalOptionsException("The specified output folder '"
          + outputDirOrZip.getAbsolutePath()
          + "' for jack files cannot be written to or read from.");
    }
  }
}
