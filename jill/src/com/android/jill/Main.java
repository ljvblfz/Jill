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

import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import javax.annotation.Nonnull;

/**
 *  Main class for Jill tool.
 */
public class Main {

  public static void main(@Nonnull String[] args) throws IOException {
    Options options = null;

    try {
      options = getOptions(args);

      if (options.askForHelp()) {
        printUsage(new CmdLineParser(options));
        System.exit(ExitStatus.SUCCESS);
      }

      if (options.askForVersion()) {
        System.out.println("Jill");
        System.out.println("Version: " + getVersion() + '.');
        System.exit(ExitStatus.SUCCESS);
      }

      run(options);

      System.exit(ExitStatus.SUCCESS);
    } catch (CmdLineException e) {
      if (e.getMessage() != null) {
        System.err.println(e.getMessage());
      }
      CmdLineParser parser = e.getParser();
      if (parser != null) {
        printUsage(parser);
      } else {
        System.err.println("Try --help for help");
      }
      System.exit(ExitStatus.FAILURE_USAGE);
    } catch (JillException e) {
      if (options != null) {
        System.err.println("Binary transformation of " + options.getBinaryFile().getName()
            + " failed.");
        if (options.isVerbose()) {
          e.printStackTrace();
        }
      } else {
        System.err.println("Binary transformation failed.");
      }

      System.exit(ExitStatus.FAILURE_INTERNAL);
    }

    System.exit(ExitStatus.FAILURE_UNKNOWN);
  }

  @Nonnull
  public static Options getOptions(@Nonnull String[] args) throws CmdLineException {
    Options options = new Options();

    CmdLineParser parser = new CmdLineParser(options);
    parser.setUsageWidth(100);

    parser.parseArgument(args);
    parser.stopOptionParsing();

    try {
      options.checkValidity();
    } catch (IllegalOptionsException e) {
      throw new CmdLineException(parser, e.getMessage(), e);
    }

    return options;
  }

  public static void run(@Nonnull Options options) throws IOException {
    new Jill(options, Main.getVersion()).process(options.getBinaryFile());
  }

  private static void printUsage(@Nonnull CmdLineParser parser) {
    System.err.print("Main: ");
    parser.printSingleLineUsage(System.err);
    System.err.println();
    parser.printUsage(System.err);
  }

  @Nonnull
  private static String getVersion() throws IOException {
    String version = "Unknown (no ressource file)";

    InputStream is = Main.class.getClassLoader().getResourceAsStream("jill.properties");
    if (is != null) {
      Properties prop = new Properties();
      prop.load(is);
      version = prop.getProperty("jill.version", "Unknown (no jill.version entry)");
    }

    return version;
  }
}
