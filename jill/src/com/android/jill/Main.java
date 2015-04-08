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

import com.android.sched.util.config.cli.TokenIterator;
import com.android.sched.util.location.NoLocation;

import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.ParserProperties;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;

/**
 *  Main class for Jill tool.
 */
public class Main {

  public static void main(@Nonnull String[] args) {
    Options options = null;

    try {
      options = getOptions(args);

      if (options.askForHelp()) {
        printUsage(new CmdLineParser(options));
        System.exit(ExitStatus.SUCCESS);
      }

      if (options.askForVersion()) {
        System.out.println("Jill");
        System.out.println("Version: " + Jill.getVersion().getVerboseVersion() + '.');
        System.exit(ExitStatus.SUCCESS);
      }

      Jill.process(options);

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
    } catch (IOException e) {
      System.err.println(e.getMessage());

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
  public static Options getOptions(@Nonnull String[] args) throws CmdLineException, IOException {
    Options options = new Options();


    CmdLineParser parser =
        new CmdLineParser(options, ParserProperties.defaults().withUsageWidth(100));

    TokenIterator iterator = new TokenIterator(new NoLocation(), args);
    List<String> list = new ArrayList<String>();
    while (iterator.hasNext()) {
      list.add(iterator.next());
    }
    parser.parseArgument(list);
    parser.stopOptionParsing();

    try {
      options.checkValidity();
    } catch (IllegalOptionsException e) {
      throw new CmdLineException(parser, e.getMessage(), e);
    }

    return options;
  }

  private static void printUsage(@Nonnull CmdLineParser parser) {
    System.err.print("Main: ");
    parser.printSingleLineUsage(System.err);
    System.err.println();
    parser.printUsage(System.err);
  }
}
