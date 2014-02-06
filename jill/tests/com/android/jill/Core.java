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

import org.junit.BeforeClass;
import org.junit.Test;

import java.io.File;


public class Core {

  @BeforeClass
  public static void setUpClass() {
    Main.class.getClassLoader().setDefaultAssertionStatus(true);
  }

  @Test
  public void coreToJayceFromJar() throws Exception {
    Options options = new Options();
    options.setBinaryFile(new File(TestTools.getAndroidTop()
        + "/out/target/common/obj/JAVA_LIBRARIES/core_intermediates/classes.jar"));
    options.setVerbose(true);
    options.container = ContainerType.DIR;
    options.outputDirOrZip = TestTools.createTempDir("core_", "_dir");
    new Jill(options, "0.1").process(options.getBinaryFile());
  }

  @Test
  public void coreToJayceFromFolder() throws Exception {
    Options options = new Options();
    options.setBinaryFile(new File(TestTools.getAndroidTop()
        + "/out/target/common/obj/JAVA_LIBRARIES/core_intermediates/classes/"));
    options.setVerbose(true);
    options.container = ContainerType.DIR;
    options.outputDirOrZip = TestTools.createTempDir("core_", "_dir");
    new Jill(options, "0.1").process(options.getBinaryFile());
  }
}
