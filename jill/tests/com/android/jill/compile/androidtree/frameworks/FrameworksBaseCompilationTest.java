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

package com.android.jill.compile.androidtree.frameworks;

import com.android.jack.category.SlowTests;
import com.android.jill.JillTestTools;

import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.io.File;

public class FrameworksBaseCompilationTest {

  private final static File[] CLASSPATH = new File[] {
    JillTestTools.getFromAndroidTree(
          "out/target/common/obj/JAVA_LIBRARIES/bouncycastle_intermediates/classes.jar"),
          JillTestTools.getFromAndroidTree(
          "out/target/common/obj/JAVA_LIBRARIES/core-junit_intermediates/classes.jar"),
          JillTestTools.getFromAndroidTree(
          "out/target/common/obj/JAVA_LIBRARIES/ext_intermediates/classes.jar")};

  private static final File SOURCELIST = JillTestTools.getTargetLibSourcelist("framework");

  @BeforeClass
  public static void setUpClass() {
    FrameworksBaseCompilationTest.class.getClassLoader().setDefaultAssertionStatus(true);
  }

  @Test
  @Category(SlowTests.class)
  public void compareFrameworksStructureWithJill() throws Exception {
    JillTestTools.checkStructureWithJill(CLASSPATH, SOURCELIST,
        false /*withDebugInfo*/);
  }
}
