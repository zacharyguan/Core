package com.zachary.ffmpeg;

import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.IntDef;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class FFmpegShell {
    static
    {
        System.loadLibrary("media-handle");
    }

    public final static int RESULT_SUCCESS = 0;

    public final static int RESULT_ERROR = 1;//>0

    private static OnFFmpegHandleListener mProgressListener;

    private static final int STATE_INIT = 0;

    private static final int STATE_RUNNING = 1;

    private static final int STATE_FINISH = 2;

    private static final int STATE_ERROR = 3;

    @Documented
    @Retention(RetentionPolicy.SOURCE)
    @IntDef({STATE_INIT, STATE_RUNNING, STATE_FINISH, STATE_ERROR})
    public @interface FFmpegState {}

    private native static int handle(String[] commands);

    private native static void cancelTaskJni(int cancel);

    private native static int fastStart(String inputFile, String outputFile);

    private native static String handleProbe(String[] commands);

    private static ExecutorService executeSingleThreadPool(Runnable runnable) {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.submit(runnable);
        return executor;
    }

    /**
     * Execute FFmpeg command
     * @param commands the String array of command
     * @param onHandleListener the callback for executing command
     */
    public static void execute(final String[] commands, final OnFFmpegHandleListener onHandleListener) {
        mProgressListener = onHandleListener;
        executeSingleThreadPool(new Runnable() {
            @Override
            public void run() {
                if (onHandleListener != null) {
                    onHandleListener.onBegin();
                }
                //call JNI interface to execute FFmpeg cmd
                int result = handle(commands);
                if (onHandleListener != null) {
                    onHandleListener.onEnd(result, null);
                }
                mProgressListener = null;
            }
        });
    }

    public static int executeSync(final String[] commands) {
        return handle(commands);
    }

    /**
     * Execute FFmpeg multi commands
     * @param commands the String array of command
     * @param onHandleListener the callback for executing command
     */
    public static void execute(final List<String[]> commands, final OnFFmpegHandleListener onHandleListener) {
        mProgressListener = onHandleListener;
        executeSingleThreadPool(new Runnable() {
            @Override
            public void run() {
                if (onHandleListener != null) {
                    onHandleListener.onBegin();
                }
                //call JNI interface to execute FFmpeg cmd
                int result = 0;
                int count = 0;
                for (String[] command : commands) {
                    result = handle(command);
                    count ++;
                    Log.i("FFmpegCmd", count + " result=" + result);
                }
                if (onHandleListener != null) {
                    onHandleListener.onEnd(result, null);
                }
                mProgressListener = null;
            }
        });
    }

    public static void cancelTask(boolean cancel) {
        cancelTaskJni(cancel ? 1 : 0);
    }

    /**
     * execute probe cmd internal
     *
     * @param commands commands
     * @param onHandleListener onHandleListener
     */
    public static void executeProbe(final String[] commands, final OnFFmpegHandleListener onHandleListener) {
        executeSingleThreadPool(new Runnable() {
            @Override
            public void run() {
                if (onHandleListener != null) {
                    onHandleListener.onBegin();
                }
                //call JNI interface to execute FFprobe cmd
                String result = handleProbe(commands);
                int resultCode = !TextUtils.isEmpty(result) ? RESULT_SUCCESS : RESULT_ERROR;
                if (onHandleListener != null) {
                    onHandleListener.onEnd(resultCode, result);
                }
            }
        });
    }

    /**
     * execute probe cmd with synchronization
     *
     * @param commands commands
     */
    public static String executeProbeSynchronize(final String[] commands) {
        return handleProbe(commands);
    }

    public static void onProgressCallback(int position, int duration, @FFmpegState int state) {
        Log.e("FFmpegCmd", "onProgress position=" + position
                + "--duration=" + duration + "--state=" + state);
        if (position > duration && duration > 0) {
            return;
        }
        if (mProgressListener != null) {
            if (position > 0 && duration > 0) {
                int progress = position * 100 / duration;
                if (progress < 100 || state == STATE_FINISH || state == STATE_ERROR) {
                    mProgressListener.onProgress(progress, duration);
                }
            } else {
                mProgressListener.onProgress(position, duration);
            }
        }
    }
}
