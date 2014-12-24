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
 * JUnit test for compilation of Fibonacci (three-address style).
 */
public class FibonacciThreeAddressTest {

  private static final String CLASS_BINARY_NAME = "com/android/jack/fibonacci/jack/FibonacciThreeAddress";
  private static final File JAVA_FILEPATH = JillTestTools.getJackTestFromBinaryName(CLASS_BINARY_NAME);

  @BeforeClass
  public static void setUpClass() {
    // Enable assertions
    Main.class.getClassLoader().setDefaultAssertionStatus(true);
  }

  @Test
  public void testCompareFiboDexFileWithJill() throws Exception {
    JillTestTools.checkStructureWithJill(null, JAVA_FILEPATH, false /*withDebugInfo*/);
  }

}
