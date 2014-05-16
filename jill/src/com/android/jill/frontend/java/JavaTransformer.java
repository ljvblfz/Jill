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

package com.android.jill.frontend.java;

import com.android.jill.ContainerType;
import com.android.jill.Jill;
import com.android.jill.JillException;
import com.android.jill.Options;
import com.android.jill.backend.jayce.JayceWriter;
import com.android.jill.utils.FileUtils;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.tree.ClassNode;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;

/**
 * Transforms java binary files into jayce.
 */
public class JavaTransformer {

  @Nonnull
  private final String version;

  private final Options options;

  @Nonnull
  private static final String JAYCE_FILE_EXTENSION = ".jack";
  @Nonnull
  private static final char TYPE_NAME_SEPARATOR = '/';

  public JavaTransformer(@Nonnull String version, @Nonnull Options options) {
    this.version = version;
    this.options = options;
  }

  public void transform(@Nonnull List<File> javaBinaryFiles) {
    ZipOutputStream zos = null;
    try {
      if (options.getContainer() == ContainerType.ZIP) {
        zos = new ZipOutputStream(new FileOutputStream(options.getOutputDir()));
        for (File fileToTransform : javaBinaryFiles) {
          FileInputStream fis = new FileInputStream(fileToTransform);
          try {
            transformToZip(fis, zos, null);
          } catch (DuplicateJackFileException e) {
            System.err.println(e.getMessage());
          } finally {
            fis.close();
          }
        }
      } else {
        for (File fileToTransform : javaBinaryFiles) {
          FileInputStream fis = new FileInputStream(fileToTransform);
          try {
            transformToDir(fis, options.getOutputDir());
          } catch (DuplicateJackFileException e) {
            System.err.println(e.getMessage());
          } finally {
            fis.close();
          }
        }
      }
    } catch (IOException e) {
      throw new JillException("Transformation failure.", e);
    } finally {
      if (zos != null) {
        try {
          zos.close();
        } catch (IOException e) {
          throw new JillException("Error closing zip.", e);
        }
      }
    }
  }

  public void transform(@Nonnull JarFile jarFile) {
    ZipOutputStream zos = null;
    try {
      if (options.getContainer() == ContainerType.ZIP) {
        zos = new ZipOutputStream(new FileOutputStream(options.getOutputDir()));
      }
      copyResources(jarFile, zos);
      transformJavaFiles(jarFile, zos);
    } catch (Exception e) {
      throw new JillException("Failed to transform " + jarFile.getName(), e);
    } finally {
      if (zos != null) {
        try {
          zos.close();
        } catch (IOException e) {
          throw new JillException("Error closing zip.", e);
        }
      }
    }
  }

  private void transformJavaFiles(@Nonnull JarFile jarFile, @CheckForNull ZipOutputStream zos)
      throws IOException {
    final Enumeration<JarEntry> entries = jarFile.entries();
    while (entries.hasMoreElements()) {
      final JarEntry entry = entries.nextElement();
      String name = entry.getName();
      if (FileUtils.isJavaBinaryFile(name)) {
        JarEntry fileEntry = jarFile.getJarEntry(name);
        if (!fileEntry.isDirectory()) {
          InputStream is = jarFile.getInputStream(fileEntry);
          try {
            if (zos != null) {
              assert options.getContainer() == ContainerType.ZIP;
              transformToZip(is, zos, jarFile);
            } else {
              assert options.getContainer() == ContainerType.DIR;
              transformToDir(is, options.getOutputDir());
            }
          } catch (DuplicateJackFileException e) {
            System.err.println(e.getMessage());
          }
        }
      }
    }
  }

  private void copyResources(@Nonnull JarFile jarFile, @CheckForNull ZipOutputStream zos)
      throws IOException {
    final Enumeration<JarEntry> entries = jarFile.entries();
    while (entries.hasMoreElements()) {
      final JarEntry entry = entries.nextElement();
      String name = entry.getName();
      if (!FileUtils.isJavaBinaryFile(name)) {
        JarEntry fileEntry = jarFile.getJarEntry(name);
        if (!fileEntry.isDirectory()) {
          InputStream is = jarFile.getInputStream(fileEntry);
          if (zos != null) {
            assert options.getContainer() == ContainerType.ZIP;
            copyResourceToZip(is, zos, name);
          } else {
            assert options.getContainer() == ContainerType.DIR;
            copyResourceToDir(is, options.getOutputDir(), name);
          }
        }
      }
    }
  }

  private void copyResourceToZip(InputStream is, ZipOutputStream zipOutputStream, String name) {
    try {
      ZipEntry zipEntry = new ZipEntry(name);
      zipOutputStream.putNextEntry(zipEntry);
      copyResource(is, zipOutputStream, name);
    } catch (Exception e) {
      throw new JillException("Error writing resource " + name, e);
    }
  }

  private void copyResourceToDir(InputStream is, File outputDir, String name) {
    OutputStream resourceOS = null;
    try {
      File outputFile = new File(outputDir, name);
      createParentDirectories(outputFile);
      resourceOS = new FileOutputStream(outputFile);
      copyResource(is, resourceOS, name);
    } catch (Exception e) {
      throw new JillException("Error writing resource " + name, e);
    } finally {
      if (resourceOS != null) {
        try {
          resourceOS.close();
        } catch (IOException e) {
          throw new JillException("Error closing output resource " + name, e);
        }
      }
    }
  }

  private void copyResource(InputStream is, OutputStream os, String name) throws IOException {
    OutputStream resourceOS = null;
    byte[] buffer = new byte[4096];
    int bytesRead;
    while ((bytesRead = is.read(buffer)) >= 0) {
      os.write(buffer, 0, bytesRead);
    }
    os.flush();
  }

  private void transformToZip(@Nonnull InputStream is, @Nonnull ZipOutputStream zipOutputStream,
      @CheckForNull JarFile jarFile) throws IOException, DuplicateJackFileException {
    ClassNode cn = getClassNode(is);
    String filePath = getFilePath(cn.name);
    if (jarFile != null && jarFile.getEntry(filePath) != null) {
      throw new DuplicateJackFileException("Jack file '" + filePath
          + "' was already copied as a resource to archive '" + options.getOutputDir()
          + "' and thus won't be retransformed from class file.");
    }
    try {
      ZipEntry entry = new ZipEntry(filePath);
      zipOutputStream.putNextEntry(entry);
      transform(cn, zipOutputStream);
    } catch (IOException e) {
      throw new JillException("Error writing to output zip", e);
    }
  }

  private void transformToDir(@Nonnull InputStream is, @Nonnull File outputDir)
      throws IOException, DuplicateJackFileException {
    ClassNode cn = getClassNode(is);
    String filePath = getFilePath(cn.name);

    File outputFile = new File(outputDir, filePath);
    if (outputFile.exists()) {
      throw new DuplicateJackFileException("Jack file '" + outputFile.getAbsolutePath()
          + "' was already copied as a resource and thus won't be retransformed from class file.");
    }
    FileOutputStream fos = null;
    try {
      createParentDirectories(outputFile);
      fos = new FileOutputStream(outputFile);
      transform(cn, fos);
    } catch (IOException e) {
      throw new JillException("Unable to create output file " + outputFile.getName(), e);
    } finally {
      if (fos != null) {
        try {
          fos.close();
        } catch (IOException e) {
          throw new JillException("Error closing output " + outputFile.getAbsolutePath(), e);
        }
      }
    }
  }

  private void transform(@Nonnull ClassNode cn, @Nonnull OutputStream os) throws IOException {

    JayceWriter writer = createWriter(os);

    ClassNodeWriter asm2jayce =
        new ClassNodeWriter(writer, new SourceInfoWriter(writer));

    asm2jayce.write(cn);

    writer.flush();
  }

  private void createParentDirectories(File outputFile) throws IOException {
    File parentFile = outputFile.getParentFile();
    if (!parentFile.exists() && !parentFile.mkdirs()) {
      throw new IOException("Could not create directory \"" + parentFile.getName() + "\"");
    }
  }

  private JayceWriter createWriter(@Nonnull OutputStream os) throws IOException {
    JayceWriter writer = new JayceWriter(os);

      writer.writeHeader(
          "jayce(2.12 \"" + Jill.FILE_ENCODING + "\" \"Jill version : " + version + "\")");

      return writer;
  }

  @Nonnull
  private static String getFilePath(@Nonnull String typeBinaryName) {
    return typeBinaryName.replace(TYPE_NAME_SEPARATOR, File.separatorChar) + JAYCE_FILE_EXTENSION;
  }

  @Nonnull
  private ClassNode getClassNode(@Nonnull InputStream is) throws IOException {
    ClassReader cr = new ClassReader(is);
    ClassNode cn = new ClassNode();
    cr.accept(cn, ClassReader.SKIP_FRAMES
        | (options.isEmitDebugInfo() ? 0 : ClassReader.SKIP_DEBUG));
    return cn;
  }

}
