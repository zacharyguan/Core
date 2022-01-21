package com.zachary.ffmpeg;

import android.os.Environment;
import android.util.Log;

import java.io.File;

public class FFmpegUtil {
    private static final String TAG = "FFmpegUtil";

    public static void mixAudio(String srcPath, String mixPath, String mixedPath, final OnFFmpegHandleListener listener) {
        String tempPath = Environment.getExternalStorageDirectory() + File.separator + "AlpcerFactory" + File.separator + "Temp" + File.separator + "mixTemp.wav";
        File file = new File(tempPath);
        if (!file.exists()) {
            if (!file.getParentFile().exists()) {
                file.getParentFile().mkdirs();
            }
        }

        FFmpegShell.execute(FFmpegCmds.mixAudio(srcPath, mixPath, tempPath), new OnFFmpegHandleListener() {
            @Override
            public void onBegin() {
                if (listener != null) {
                    listener.onBegin();
                }
            }

            @Override
            public void onEnd(int resultCode, String resultMsg) {
                if (resultCode == FFmpegShell.RESULT_SUCCESS) {
                    File file = new File(mixedPath);
                    if (!file.getParentFile().exists()) {
                        file.getParentFile().mkdirs();
                    }
                    FFmpegShell.execute(FFmpegCmds.transformAudio(tempPath, mixedPath), new OnFFmpegHandleListener() {
                        @Override
                        public void onBegin() {

                        }

                        @Override
                        public void onEnd(int resultCode, String resultMsg) {
                            if (listener != null) {
                                listener.onEnd(resultCode, resultMsg);
                            }
                        }
                    });
                } else {
                    if (listener != null) {
                        listener.onEnd(resultCode, resultMsg);
                    }
                }

            }
        });
    }

    public static void delayAudio(String srcPath, int delaySeconds, String outPath, final OnFFmpegHandleListener listener) {
        File file = new File(outPath);
        if (!file.exists()) {
            if (!file.getParentFile().exists()) {
                file.getParentFile().mkdirs();
            }
        }

        FFmpegShell.execute(FFmpegCmds.delayAudio(srcPath, delaySeconds, outPath), new OnFFmpegHandleListener() {
            @Override
            public void onBegin() {
                if (listener != null) {
                    listener.onBegin();
                }
            }

            @Override
            public void onEnd(int resultCode, String resultMsg) {
                if (listener != null) {
                    listener.onEnd(resultCode, resultMsg);
                }
            }
        });
    }

    public static void extractAudio(final String srcPath, final String destPath, final OnFFmpegHandleListener listener) {
        String tempPath = Environment.getExternalStorageDirectory() + File.separator + "AlpcerFactory" + File.separator + "Temp" + File.separator + "extractTemp.aac";
        File file = new File(tempPath);
        if (!file.exists()) {
            if (!file.getParentFile().exists()) {
                file.getParentFile().mkdirs();
            }
        } else {
            file.delete();
        }

        FFmpegShell.execute(FFmpegCmds.extractAudio(srcPath, tempPath), new OnFFmpegHandleListener() {
            @Override
            public void onBegin() {
                if (listener != null) {
                    listener.onBegin();
                }
            }

            @Override
            public void onEnd(int resultCode, String resultMsg) {
                if (resultCode == FFmpegShell.RESULT_SUCCESS) {
                    Log.e("FFmpegUtil", "extract success, converting...");
                    File file = new File(destPath);
                    if (!file.getParentFile().exists()) {
                        file.getParentFile().mkdirs();
                    }
                    FFmpegShell.execute(FFmpegCmds.transformAudio(tempPath, destPath), new OnFFmpegHandleListener() {
                        @Override
                        public void onBegin() {

                        }

                        @Override
                        public void onEnd(int resultCode, String resultMsg) {
                            if (resultCode != FFmpegShell.RESULT_SUCCESS) {
                                File destFile = new File(destPath);
                                if (destFile.exists()) {
                                    destFile.delete();
                                }
                            }
                            if (listener != null) {
                                listener.onEnd(resultCode, resultMsg);
                            }
                        }
                    });
                } else {
                    if (listener != null) {
                        listener.onEnd(resultCode, resultMsg);
                    }
                }
            }
        });
    }
}