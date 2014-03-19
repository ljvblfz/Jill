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

package com.android.jill.compile.androidtree.bouncycastle;

import com.android.jack.JarJarRules;
import com.android.jack.ProguardFlags;
import com.android.jack.category.SlowTests;
import com.android.jill.JillTestTools;

import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.io.File;

public class BouncycastleCompilationTest {

  private static final File[] BOOTCLASSPATH = new File[] {
    JillTestTools.getFromAndroidTree(
        "out/target/common/obj/JAVA_LIBRARIES/core_intermediates/classes.jar")
  };

  private static final File SOURCELIST = JillTestTools.getTargetLibSourcelist("bouncycastle");

  private static final JarJarRules JARJAR_RULES = new JarJarRules(
      JillTestTools.getFromAndroidTree("external/bouncycastle/jarjar-rules.txt"));

  @Test
  @Category(SlowTests.class)
  public void compareBouncycastleStructureWithJill() throws Exception {
    JillTestTools.checkStructureWithJill(
        BOOTCLASSPATH,
        null,
        SOURCELIST,
        false /* compareDebugInfoBinary */,
        JARJAR_RULES,
        (ProguardFlags[]) null);
  }
}
