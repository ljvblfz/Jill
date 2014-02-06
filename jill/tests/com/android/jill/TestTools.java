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

import java.io.File;
import java.io.IOException;

import javax.annotation.Nonnull;

public class TestTools {

  @Nonnull
  public static String getAndroidTop() {
    String androidTop = System.getenv("ANDROID_BUILD_TOP");
    if (androidTop == null) {
      throw new AssertionError("Failed to locate environment variable ANDROID_BUILD_TOP.");
    }
    return androidTop;
  }

  @Nonnull
  public static File createTempDir(@Nonnull String prefix, @Nonnull String suffix)
      throws IOException {
    File tmp = File.createTempFile(prefix, suffix);
    if (!tmp.delete()) {
      throw new IOException("Failed to delete file " + tmp.getAbsolutePath());
    }
    if (!tmp.mkdirs()) {
      throw new IOException("Failed to create folder " + tmp.getAbsolutePath());
    }
    return tmp;
  }
}
