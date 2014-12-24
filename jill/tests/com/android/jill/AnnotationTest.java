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

package com.android.jill;

import com.android.jack.Main;

import org.junit.BeforeClass;
import org.junit.Test;

import java.io.File;

/**
 * JUnit test for compilation of annotation.
 */
public class AnnotationTest {

  private static final File ANNOTATION001_PATH =
      JillTestTools.getJackTestsWithJackFolder("annotation/test001");

  @BeforeClass
  public static void setUpClass() {
    Main.class.getClassLoader().setDefaultAssertionStatus(true);
  }

  @Test
  public void test001_3WithJill() throws Exception {
    JillTestTools.checkStructureWithJill(null,
        new File(ANNOTATION001_PATH, "Annotation2.java"), false /*withDebugInfo*/);
  }

}
