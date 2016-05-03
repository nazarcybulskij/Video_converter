package com.test.trimmer.videoconverter;

import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import com.github.hiteshsondhi88.libffmpeg.FFmpeg;
import com.github.hiteshsondhi88.libffmpeg.FFmpegExecuteResponseHandler;
import com.github.hiteshsondhi88.libffmpeg.FFmpegLoadBinaryResponseHandler;
import com.github.hiteshsondhi88.libffmpeg.exceptions.FFmpegCommandAlreadyRunningException;
import com.github.hiteshsondhi88.libffmpeg.exceptions.FFmpegNotSupportedException;

import java.io.File;
import java.util.Locale;

/**
 * Created by mark on 30.04.16.
 */
public class FfmpegUtils {
    private static final String TAG = FfmpegUtils.class.getSimpleName();
    private static final String OUTPUT = Environment.getExternalStorageDirectory() + "/trim_output.mp4";

    private static void showToast(int text) {
        Toast.makeText(App.getInstance(), text, Toast.LENGTH_LONG).show();
    }

    public static void initFFMPEG() {
        try {
            FFmpeg.getInstance(App.getInstance()).loadBinary(new FFmpegLoadBinaryResponseHandler() {
                @Override
                public void onFailure() {
                    showToast(R.string.load_failed);
                }

                @Override
                public void onSuccess() {
                    Log.d(TAG, "FFMPEG loaded");
                }

                @Override
                public void onStart() {
                }

                @Override
                public void onFinish() {
                }
            });
        } catch (FFmpegNotSupportedException e) {
            showToast(R.string.device_not_supported_message);
        }
    }

    private static String[] createTrimCommand(String videoPath, int start, int end) {
        return String.format(Locale.US, "-i %s -ss %d -t %d -acodec copy -vcodec copy %s", videoPath, start, end - start, OUTPUT).split(" ");
    }

    public static void trimVideo(String videoPath, int start, int end, final Callback callback) {
        try {
            new File(OUTPUT).delete();
            FFmpeg.getInstance(App.getInstance()).killRunningProcesses();
            FFmpeg.getInstance(App.getInstance()).execute(createTrimCommand(videoPath, start, end),
                    new FFmpegExecuteResponseHandler() {
                        @Override
                        public void onSuccess(String message) {
                            Log.d(TAG, "finished trim");
                            callback.finished(OUTPUT);
                        }

                        @Override
                        public void onProgress(String message) {
                            Log.d(TAG, message);
                        }

                        @Override
                        public void onFailure(String message) {
                            Log.d(TAG, "error: " + message);
                            callback.error();
                            showToast(R.string.error);
                        }

                        @Override
                        public void onStart() {
                        }

                        @Override
                        public void onFinish() {
                        }
                    });
        } catch (FFmpegCommandAlreadyRunningException e) {
            showToast(R.string.another_command_is_running);
        }
    }

    public interface Callback {
        void finished(String file);

        void error();
    }
}
