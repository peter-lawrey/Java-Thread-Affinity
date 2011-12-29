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
JVM_SHARED_LIB := $(JAVA_HOME)/jre/lib/amd64/server

INCLUDES := -I $(JAVA_HOME)/include -I $(JAVA_HOME)/include/linux -I $(WORKING_DIR)

# classpath for javah
ifdef CLASSPATH
JAVAH_CLASSPATH = $(JAVA_BUILD_DIR):$(CLASSPATH)
else
JAVAH_CLASSPATH = $(JAVA_BUILD_DIR)
endif

all: $(TARGET)

$(TARGET): vanilla_java_affinity_NativeAffinity.c $(WORKING_DIR)/vanilla_java_affinity_NativeAffinity.h
	gcc -O2 -shared -fPIC -L$(JVM_SHARED_LIB) -ljvm -lrt $(INCLUDES) vanilla_java_affinity_NativeAffinity.c -o $(TARGET)

$(WORKING_DIR)/vanilla_java_affinity_NativeAffinity.h: $(TARGET_DIR)/vanilla/java/affinity/NativeAffinity.class
	mkdir -p $(TARGET_DIR)/jni
	javah -force -classpath $(JAVAH_CLASSPATH) -d $(WORKING_DIR) vanilla.java.affinity.impl.NativeAffinity

