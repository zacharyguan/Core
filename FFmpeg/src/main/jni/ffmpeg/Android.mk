LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)
LOCAL_MODULE := ffmpeg
LOCAL_SRC_FILES := $(LOCAL_PATH)/prebuilt/$(TARGET_ARCH_ABI)/libffmpeg.so
include $(PREBUILT_SHARED_LIBRARY)

include $(CLEAR_VARS)
LOCAL_MODULE := mp3lame
LOCAL_SRC_FILES := $(LOCAL_PATH)/prebuilt/$(TARGET_ARCH_ABI)/libmp3lame.so
include $(PREBUILT_SHARED_LIBRARY)

include $(CLEAR_VARS)
LOCAL_MODULE := media-handle
LOCAL_SRC_FILES := ffmpeg/cmdutils.c \
ffmpeg/ffmpeg.c \
ffmpeg/ffmpeg_filter.c \
ffmpeg/ffmpeg_opt.c \
ffmpeg_cmd.c \
ffmpeg/ffprobe.c \
ffmpeg/ffmpeg_hw.c \
audio_player.c \
openSL_audio_player.c \
video_player.c \
ffmpeg_pusher.cpp \
AVpacket_queue.c \
media_player.c \
video_filter.c \
audio_lame.c \
fast_start.c \
ffprobe_cmd.c
LOCAL_C_INCLUDES := $(LOCAL_PATH) $(LOCAL_PATH)/include $(LOCAL_PATH)/include/$(TARGET_ARCH_ABI)
LOCAL_LDLIBS := -llog -lz -ldl -landroid -ljnigraphics -lOpenSLES
LOCAL_SHARED_LIBRARIES := ffmpeg mp3lame
LOCAL_CPPFLAGS += -std=gnu++11
include $(BUILD_SHARED_LIBRARY)