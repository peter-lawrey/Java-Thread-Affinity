#!/usr/bin/make -f
#
# Makefile for C code
#

# C sources to compile

TARGET_DIR := ../../../target/classes
JAVA_BUILD_DIR := $(TARGET_DIR)
WORKING_DIR := $(TARGET_DIR)/../jni

SYS := $(shell gcc -dumpmachine)
uname_S := $(shell sh -c 'uname -s 2>/dev/null || echo not')

ifeq ($(uname_S),Linux)
    TARGET := $(TARGET_DIR)/libaffinity.so
    JAVA_HOME := /usr/java/default
    JAVA_LIB := $(JAVA_HOME)/jre/lib
    INCLUDES := -I $(JAVA_HOME)/include -I $(JAVA_HOME)/include/linux -I $(WORKING_DIR)
    JVM_SHARED_LIBS := -L$(JAVA_LIB)/amd64/server -L$(JAVA_LIB)/i386/server -L$(JAVA_LIB)/amd64/jrockit/ -L$(JAVA_LIB)/i386/jrockit/
endif

ifeq ($(uname_S),Darwin)
    TARGET := $(TARGET_DIR)/libaffinity.dylib
    JAVA_HOME := $(shell /usr/libexec/java_home)
    JAVA_LIB := $(JAVA_HOME)/jre/lib
    INCLUDES := -I $(JAVA_HOME)/include -I $(JAVA_HOME)/include/darwin -I $(WORKING_DIR)
    JVM_SHARED_LIBS := -L$(JAVA_LIB) -L$(JAVA_LIB)/server
endif

# classpath for javah
ifdef CLASSPATH
JAVAH_CLASSPATH = $(JAVA_BUILD_DIR):$(CLASSPATH)
else
JAVAH_CLASSPATH = $(JAVA_BUILD_DIR)
endif

all: $(TARGET)

$(TARGET_DIR)/libaffinity.dylib: com_higherfrequencytrading_affinity_impl_NativeAffinity_MacOSX.c $(WORKING_DIR)/com_higherfrequencytrading_affinity_impl_NativeAffinity.h $(WORKING_DIR)/com_higherfrequencytrading_clock_impl_JNIClock.h $(WORKING_DIR)/com_higherfrequencytrading_busywaiting_impl_BusyWaiting.h
	gcc -O2 -shared -fPIC $(JVM_SHARED_LIBS) -ljvm $(INCLUDES) com_higherfrequencytrading_affinity_impl_NativeAffinity_MacOSX.c -o $(TARGET)

$(TARGET_DIR)/libaffinity.so: com_higherfrequencytrading_affinity_impl_NativeAffinity.c $(WORKING_DIR)/com_higherfrequencytrading_affinity_impl_NativeAffinity.h $(WORKING_DIR)/com_higherfrequencytrading_clock_impl_JNIClock.h $(WORKING_DIR)/com_higherfrequencytrading_busywaiting_impl_BusyWaiting.h
	gcc -O2 -shared -fPIC $(JVM_SHARED_LIBS) -ljvm -lrt $(INCLUDES) com_higherfrequencytrading_affinity_impl_NativeAffinity.c -o $(TARGET)

$(WORKING_DIR)/com_higherfrequencytrading_affinity_impl_NativeAffinity.h: $(TARGET_DIR)/com/higherfrequencytrading/affinity/impl/NativeAffinity.class
	mkdir -p $(TARGET_DIR)/jni
	javah -force -classpath $(JAVAH_CLASSPATH) -d $(WORKING_DIR) com.higherfrequencytrading.affinity.impl.NativeAffinity

$(WORKING_DIR)/com_higherfrequencytrading_clock_impl_JNIClock.h: $(TARGET_DIR)/com/higherfrequencytrading/clock/impl/JNIClock.class
	mkdir -p $(TARGET_DIR)/jni
	javah -force -classpath $(JAVAH_CLASSPATH) -d $(WORKING_DIR) com.higherfrequencytrading.clock.impl.JNIClock

$(WORKING_DIR)/com_higherfrequencytrading_busywaiting_impl_BusyWaiting.h: $(TARGET_DIR)/com/higherfrequencytrading/busywaiting/impl/JNIBusyWaiting.class
	mkdir -p $(TARGET_DIR)/jni
	javah -force -classpath $(JAVAH_CLASSPATH) -d $(WORKING_DIR) com.higherfrequencytrading.busywaiting.impl.JNIBusyWaiting
