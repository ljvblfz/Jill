/*
 * Copyright (C) 2014 The Android Open Source Project
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


import com.android.jill.compile.androidtree.bouncycastle.BouncycastleCompilationTest;
import com.android.jill.compile.androidtree.core.CoreCompilationTest;
import com.android.jill.compile.androidtree.frameworks.FrameworksBaseCompilationTest;
import com.android.jill.compile.androidtree.services.ServicesCompilationTest;
import com.android.jill.shrob.ShrinkTest;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

/**
 * Test suite containing all tests.
 */
@RunWith(Suite.class)
@SuiteClasses(value = {
    AnnotationTest.class,
    FibonacciThreeAddressTest.class,
    FieldTest.class,
    InnerTest.class,
    JarjarTest.class,
    NoClasspathTest.class,
    BouncycastleCompilationTest.class,
    CoreCompilationTest.class,
    FrameworksBaseCompilationTest.class,
    ServicesCompilationTest.class,
    ShrinkTest.class})
public class AllTests {
}
