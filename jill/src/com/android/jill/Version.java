/*
 * Copyright (C) 2015 The Android Open Source Project
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

import com.android.jill.api.JillProvider.SubReleaseKind;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;

/**
 * A class describing version, release, build & code.
 */
public class Version {

  @Nonnull
  private String version;
  @Nonnull
  private String releaseName;
  @Nonnegative
  private int    releaseCode;
  @Nonnull
  private SubReleaseKind subReleaseKind;
  @Nonnegative
  private int    subReleaseCode;
  @CheckForNull
  private String buildId;
  @CheckForNull
  private String codeBase;

  public Version(@Nonnull InputStream is) {
    Properties prop = new Properties();
    try {
      prop.load(is);

      version = prop.getProperty("jill.version");
      assert version != null;

      releaseName = prop.getProperty("jill.version.release.name");
      assert releaseName != null;

      releaseCode = Integer.parseInt(prop.getProperty("jill.version.release.code"));
      assert releaseCode >= 1;

      subReleaseCode = Integer.parseInt(prop.getProperty("jill.version.sub-release.code"));
      assert subReleaseCode >= 1;

      subReleaseKind =
          SubReleaseKind.valueOf(SubReleaseKind.class,
              prop.getProperty("jill.version.sub-release.kind"));
      buildId = prop.getProperty("jill.version.buildid");
      codeBase = prop.getProperty("jill.version.sha");

      if (codeBase == null || buildId == null) {
        subReleaseKind = SubReleaseKind.ENGINEERING;
      }
    } catch (IOException e) {
      System.err.println("Failed to read Jill properties");
      throw new AssertionError(e);
    }
  }

  @Nonnull
  public String getVersion() {
    return version;
  }

  @Nonnull
  public String getReleaseName() {
    return releaseName;
  }

  @Nonnegative
  public int getReleaseCode() {
    return releaseCode;
  }

  @Nonnull
  public SubReleaseKind getSubReleaseKind() {
    return subReleaseKind;
  }

  @Nonnegative
  public int getSubReleaseCode() {
    return subReleaseCode;
  }

  @CheckForNull
  public String getBuildId() {
    return buildId;
  }

  @CheckForNull
  public String getCodeBase() {
    return codeBase;
  }

  @Nonnull
  public String getVerboseVersion() {
    return version + " '" + releaseName + "' ("
                   + (buildId != null ? buildId : "engineering")
                   + (codeBase != null ? (' ' + codeBase) : "") + ")";
  }
}