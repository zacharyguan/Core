package com.zachary.ffmpeg;

public interface OnFFmpegHandleListener {
    void onBegin();
    default void onProgress(int progress, int duration){}
    void onEnd(int resultCode, String resultMsg);
}