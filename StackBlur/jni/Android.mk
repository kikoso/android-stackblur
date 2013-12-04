LOCAL_PATH := $(call my-dir)
 
# Create BitmapUtils library
 
include $(CLEAR_VARS)
 
LOCAL_LDLIBS    := -llog -ljnigraphics
 
LOCAL_MODULE    := blur
LOCAL_SRC_FILES := blur.c
 
LOCAL_CFLAGS    =  -ffast-math -O3 -funroll-loops
 
include $(BUILD_SHARED_LIBRARY)