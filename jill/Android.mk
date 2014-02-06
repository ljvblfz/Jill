# Copyright (C) 2013 The Android Open Source Project
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
#      http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.

LOCAL_PATH:= $(call my-dir)

include $(CLEAR_VARS)

JILL_BASE_VERSION_NAME := 0.1
JILL_BASE_VERSION_CODE := 001

LOCAL_MODULE := jill
LOCAL_MODULE_TAGS := optional
LOCAL_MODULE_CLASS := EXECUTABLES

LOCAL_SRC_FILES := $(call all-java-files-under, src)

LOCAL_JAR_MANIFEST := etc/manifest.txt

LOCAL_STATIC_JAVA_LIBRARIES := \
  asm-all-4.1-jill \
  guava-jack \
  jsr305lib-jack \
  args4j-jack

ifneq "" "$(filter eng.%,$(BUILD_NUMBER))"
  JILL_VERSION_NAME_TAG := eng.$(USER)
else
  JILL_VERSION_NAME_TAG := $(BUILD_NUMBER)
endif

JILL_VERSION_NAME := "$(JILL_BASE_VERSION_NAME).$(JILL_BASE_VERSION_CODE).$(JILL_VERSION_NAME_TAG)"

intermediates := $(call local-intermediates-dir,COMMON)
$(intermediates)/rsc/jill.properties:
	$(hide) mkdir -p $(dir $@)
	$(hide) echo "jill.version=$(JILL_VERSION_NAME)" > $@

LOCAL_JAVA_RESOURCE_FILES := $(intermediates)/rsc/jill.properties

include $(BUILD_HOST_JAVA_LIBRARY)

# Include this library in the build server's output directory
$(call dist-for-goals, dist_files, $(LOCAL_BUILT_MODULE):jill.jar)



include $(CLEAR_VARS)

LOCAL_MODULE := jill-jarjar-asm
LOCAL_MODULE_TAGS := optional
LOCAL_MODULE_CLASS := EXECUTABLES

LOCAL_SRC_FILES := $(call all-java-files-under, src)

LOCAL_JAR_MANIFEST := etc/manifest.txt

LOCAL_STATIC_JAVA_LIBRARIES := \
  asm-all-4.1-jill \
  guava-jack \
  jsr305lib-jack \
  args4j-jack

intermediates := $(call local-intermediates-dir,COMMON)
$(intermediates)/rsc/jill.properties:
	$(hide) mkdir -p $(dir $@)
	$(hide) echo "jill.version=$(JILL_VERSION_NAME)" > $@

LOCAL_JAVA_RESOURCE_FILES := $(intermediates)/rsc/jill.properties
LOCAL_JARJAR_RULES := $(LOCAL_PATH)/jarjar-rules.txt

include $(BUILD_HOST_JAVA_LIBRARY)

# Include this library in the build server's output directory
$(call dist-for-goals, dist_files, $(LOCAL_BUILT_MODULE):jill-jarjar-asm.jar)
