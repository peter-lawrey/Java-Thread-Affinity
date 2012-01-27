#!/usr/bin/make -f
#
# Makefile for C code
#

# C sources to compile

TARGET_DIR := ../../../target/classes
TARGET := $(TARGET_DIR)/libaffinity.so

WORKING_DIR := $(TARGET_DIR)/../jni

JAVA_BUILD_DIR := $(TARGET_DIR)

JAVA_HOME ?= /usr/java/default
JVM_SHARED_LIBS := -L$(JAVA_HOME)/jre/lib/amd64/server -L$(JAVA_HOME)/jre/lib/i386/server

INCLUDES := -I $(JAVA_HOME)/include -I $(JAVA_HOME)/include/linux -I $(WORKING_DIR)

# classpath for javah
ifdef CLASSPATH
JAVAH_CLASSPATH = $(JAVA_BUILD_DIR):$(CLASSPATH)
else
JAVAH_CLASSPATH = $(JAVA_BUILD_DIR)
endif

all: $(TARGET)

$(TARGET): vanilla_java_affinity_impl_NativeAffinity.c $(WORKING_DIR)/vanilla_java_affinity_impl_NativeAffinity.h $(WORKING_DIR)/vanilla_java_clock_impl_JNIClock.h $(WORKING_DIR)/vanilla_java_busywaiting_impl_BusyWaiting.h
	gcc -O2 -shared -fPIC $(JVM_SHARED_LIBS) -ljvm -lrt $(INCLUDES) vanilla_java_affinity_impl_NativeAffinity.c -o $(TARGET)

$(WORKING_DIR)/vanilla_java_affinity_impl_NativeAffinity.h: $(TARGET_DIR)/vanilla/java/affinity/impl/NativeAffinity.class
	mkdir -p $(TARGET_DIR)/jni
	javah -force -classpath $(JAVAH_CLASSPATH) -d $(WORKING_DIR) vanilla.java.affinity.impl.NativeAffinity

$(WORKING_DIR)/vanilla_java_clock_impl_JNIClock.h: $(TARGET_DIR)/vanilla/java/clock/impl/JNIClock.class
	mkdir -p $(TARGET_DIR)/jni
	javah -force -classpath $(JAVAH_CLASSPATH) -d $(WORKING_DIR) vanilla.java.clock.impl.JNIClock

$(WORKING_DIR)/vanilla_java_busywaiting_impl_BusyWaiting.h: $(TARGET_DIR)/vanilla/java/busywaiting/impl/JNIBusyWaiting.class
	mkdir -p $(TARGET_DIR)/jni
	javah -force -classpath $(JAVAH_CLASSPATH) -d $(WORKING_DIR) vanilla.java.busywaiting.impl.JNIBusyWaiting
