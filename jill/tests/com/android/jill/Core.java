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

import com.android.jack.test.TestsProperties;
import com.android.jack.test.toolchain.AbstractTestTools;
import com.android.jill.api.JillProvider;
import com.android.jill.api.v01.Api01Config;

import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ServiceLoader;

@Ignore("Tree")
public class Core {

  @BeforeClass
  public static void setUpClass() {
    Core.class.getClassLoader().setDefaultAssertionStatus(true);
  }

  @Test
  public void coreToJayceFromJar() throws Exception {
    Options options = new Options();
    options.setBinaryFile(new File(TestsProperties.getAndroidRootDir().getPath()
        + "/out/target/common/obj/JAVA_LIBRARIES/core-libart_intermediates/classes.jar"));
    options.setVerbose(true);
    options.output = AbstractTestTools.createTempFile("jillTest", ".zip");
    Jill.process(options);
  }

  @Test
  public void coreToJayceFromJarWithJillApi() throws Exception {
    File jillPrebuilt = AbstractTestTools.getPrebuilt("jill");

    ClassLoader classLoader = URLClassLoader.newInstance(new URL[] {jillPrebuilt.toURI().toURL()},
        Core.class.getClassLoader());

    ServiceLoader<JillProvider> serviceLoader = ServiceLoader.load(JillProvider.class, classLoader);
    JillProvider provider = serviceLoader.iterator().next();

    Api01Config config = provider.createConfig(Api01Config.class);

    config.setInputJavaBinaryFile(new File(TestsProperties.getAndroidRootDir().getPath()
        + "/out/target/common/obj/JAVA_LIBRARIES/core-libart_intermediates/classes.jar"));
    config.setVerbose(true);
    config.setOutputJackFile(AbstractTestTools.createTempFile("jillTest", ".jack"));

    config.getTask().run();
  }


  @Test
  public void coreToJayceFromFolder() throws Exception {
    Options options = new Options();
    options.setBinaryFile(new File(TestsProperties.getAndroidRootDir().getPath()
        + "/out/target/common/obj/JAVA_LIBRARIES/core-libart_intermediates/classes/"));
    options.setVerbose(true);
    options.output = AbstractTestTools.createTempFile("jillTest", ".zip");
    Jill.process(options);
  }
}
