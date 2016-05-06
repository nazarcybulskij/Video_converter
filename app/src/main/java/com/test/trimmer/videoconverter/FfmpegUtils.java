package com.test.trimmer.videoconverter;

import android.content.Context;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import com.github.hiteshsondhi88.libffmpeg.FFmpeg;
import com.github.hiteshsondhi88.libffmpeg.FFmpegExecuteResponseHandler;
import com.github.hiteshsondhi88.libffmpeg.FFmpegLoadBinaryResponseHandler;
import com.github.hiteshsondhi88.libffmpeg.exceptions.FFmpegCommandAlreadyRunningException;
import com.github.hiteshsondhi88.libffmpeg.exceptions.FFmpegNotSupportedException;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.Locale;

/**
 * Created by mark on 30.04.16.
 */
public class FfmpegUtils {
    private static final String TAG = FfmpegUtils.class.getSimpleName();
    private static final String OUTPUT_TRIM = Environment.getExternalStorageDirectory() + "/trim_output.mp4";

    private static final String OUTPUT_CONCAT = Environment.getExternalStorageDirectory() + "/concat_output.mp4";

    private static  String CONCAT_TS1 ;// Environment.getExternalStorageDirectory() +"/"+"intermediate1.ts";
    private static  String CONCAT_TS2 ;//= Environment.getExternalStorageDirectory() +"/"+"intermediate2.ts";

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
        return String.format(Locale.US, "-i %s -ss %d -t %d -acodec copy -vcodec copy %s", videoPath, start, end - start, OUTPUT_TRIM).split(" ");
    }

    private static String[] createConcatPartCommand(String videoPath,String temp) {
       return String.format(Locale.US, "-i %s -c copy -bsf:v h264_mp4toannexb -f mpegts %s", videoPath,temp ).split(" ");
     }

  private static String[] createConcatCommand() {
    String list = generateList(new String[] {"intermediate1.ts","intermediate2.ts"});
//    return new String[] {
//        "ffmpeg",
//        "-f",
//        "concat",
//        "-i",
//        list,
//        "-c",
//        "copy",
//        OUTPUT_CONCAT
//    };
    return String.format(Locale.US, "-f concat -i '%s -c copy -bsf:a aac_adtstoasc %s", list,OUTPUT_CONCAT ).split(" ");
  }



  public static void concatVideo(Context context , String videoPath1, final String videoPath2, final Callback callback) {
    try {
      new File(OUTPUT_CONCAT).delete();
      CONCAT_TS1=context.getFilesDir().getAbsolutePath() + File.separator+"intermediate1.ts";
      CONCAT_TS2=context.getFilesDir().getAbsolutePath() + File.separator+"intermediate2.ts";
      new File(CONCAT_TS1).delete();
      new File(CONCAT_TS2).delete();


      FFmpeg.getInstance(App.getInstance()).killRunningProcesses();
      FFmpeg.getInstance(App.getInstance()).execute(createConcatPartCommand(videoPath1,CONCAT_TS1),
          new FFmpegExecuteResponseHandler() {
            @Override
            public void onSuccess(String message) {
              Log.d(TAG, "finished concat");
              concat(videoPath2,callback);
            }

            @Override
            public void onProgress(String message) {
              Log.d(TAG, message);
            }

            @Override
            public void onFailure(String message) {
              Log.d(TAG, "error: " + message);
              //callback.error();
              //showToast(R.string.error);
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

  public static void concat(String videoPath2,final Callback callback) {
    try {
      FFmpeg.getInstance(App.getInstance()).killRunningProcesses();
      FFmpeg.getInstance(App.getInstance()).execute(createConcatPartCommand(videoPath2,CONCAT_TS2),
          new FFmpegExecuteResponseHandler() {
            @Override
            public void onSuccess(String message) {
              Log.d(TAG, "finished concat");
              finalConcat(callback);
            }

            @Override
            public void onProgress(String message) {
              Log.d(TAG, message);
            }

            @Override
            public void onFailure(String message) {
              Log.d(TAG, "error: " + message);
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

  public static  final void finalConcat(final Callback callback){

    try {
      FFmpeg.getInstance(App.getInstance()).killRunningProcesses();
      FFmpeg.getInstance(App.getInstance()).execute(createConcatCommand(),
          new FFmpegExecuteResponseHandler() {
            @Override
            public void onSuccess(String message) {
              Log.d(TAG, "finished concat");
              callback.finished(OUTPUT_CONCAT);
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


  private static String generateList(String[] inputs) {
    File list;
    Writer writer = null;
    try {
      list = new File(App.getInstance().getFilesDir().getAbsolutePath(),"ffmpeg.txt");
      list.createNewFile();
      writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(list)));
      for (String input: inputs) {
        writer.write("file '" + input + "'\n");
        Log.d(TAG, "Writing to list file: file '" + input + "'");
      }
    } catch (IOException e) {
      e.printStackTrace();
      return "/";
    } finally {
      try {
        if (writer != null)
          writer.close();
      } catch (IOException ex) {
        ex.printStackTrace();
      }
    }

    Log.d(TAG, "Wrote list file to " + list.getAbsolutePath());
    return list.getAbsolutePath();
  }










    public static void trimVideo(String videoPath, int start, int end, final Callback callback) {
        try {
            new File(OUTPUT_TRIM).delete();
            FFmpeg.getInstance(App.getInstance()).killRunningProcesses();
            FFmpeg.getInstance(App.getInstance()).execute(createTrimCommand(videoPath, start, end),
                    new FFmpegExecuteResponseHandler() {
                        @Override
                        public void onSuccess(String message) {
                            Log.d(TAG, "finished trim");
                            callback.finished(OUTPUT_TRIM);
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
