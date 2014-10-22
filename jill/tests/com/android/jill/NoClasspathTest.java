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

import com.android.jack.category.SlowTests;

import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.io.File;

@Category(SlowTests.class)
public class NoClasspathTest {

  protected static final File FRAMEWORK_JAR = JillTestTools
      .getFromAndroidTree("/out/target/common/obj/JAVA_LIBRARIES/framework_intermediates/"
          + "classes.jar");

  @Test
  public void frameworkFromJill() throws Exception {
    File frameworkJackZip = JillTestTools.createTempFile("framework", ".zip");
    JillTestTools.runJillToZip(FRAMEWORK_JAR, frameworkJackZip);

    File frameworkDex = JillTestTools.createTempDir("framework", "dex");
    JillTestTools.compileJackToDex(new com.android.jack.Options(), frameworkJackZip, frameworkDex,
        false);
  }
}
