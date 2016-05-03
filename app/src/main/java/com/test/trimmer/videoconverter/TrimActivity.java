package com.test.trimmer.videoconverter;

import android.app.AlertDialog;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.VideoView;

import org.florescu.android.rangeseekbar.RangeSeekBar;

import java.io.File;

public class TrimActivity extends AppCompatActivity {
    private final String TAG = TrimActivity.class.getSimpleName();
    private final int SELECT_VIDEO_REQUEST_CODE = 18795;

    private View mSelectVideoButton;
    private View mTrimVideoButton;
    private VideoView mVideoView;
    private RangeSeekBar<Float> mTrimBar;
    private View mLoading;

    private String mVideoPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trim);

        mSelectVideoButton = findViewById(R.id.select_video);
        mVideoView = (VideoView) findViewById(R.id.video);
        mTrimBar = (RangeSeekBar<Float>) findViewById(R.id.trim_bar);
        mTrimVideoButton = findViewById(R.id.trim_video);
        mLoading = findViewById(R.id.loading);

        mSelectVideoButton.setOnClickListener(mOnClickListener);
        mTrimVideoButton.setOnClickListener(mOnClickListener);
        mTrimBar.setOnRangeSeekBarChangeListener(createOnRangeSeekBarChangeListener(mTrimBar));

        mTrimBar.setNotifyWhileDragging(true);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mVideoPath != null) {
            mTrimBar.resetSelectedValues();
            mVideoView.seekTo(1);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == SELECT_VIDEO_REQUEST_CODE && resultCode == RESULT_OK) {
            setVideo(data.getData());
        }
    }

    private void setVideo(Uri uri) {
        mVideoPath = parseUri(uri);
        mVideoView.setVideoURI(uri);
        mTrimBar.resetSelectedValues();
        mVideoView.seekTo(1);
        mTrimBar.setVisibility(View.VISIBLE);
        mTrimVideoButton.setVisibility(View.VISIBLE);
    }

    private String parseUri(Uri uri) {
        String result;
        Cursor cursor = getContentResolver().query(uri, null, null, null, null, null);
        if (cursor == null) {
            result = uri.getPath();
        } else {
            cursor.moveToFirst();
            try {
                result = cursor.getString(cursor.getColumnIndex(MediaStore.Video.VideoColumns.DATA));
            } catch (Exception e) {
                result = Utils.findPathByFileName(cursor.getString(cursor.getColumnIndex(MediaStore.Video.VideoColumns.DISPLAY_NAME)));
            }

            cursor.close();
        }
        return result;
    }

    private RangeSeekBar.OnRangeSeekBarChangeListener<Float> createOnRangeSeekBarChangeListener(final RangeSeekBar<Float> seekBar) {
        return new RangeSeekBar.OnRangeSeekBarChangeListener<Float>() {
            private float mLastMinValue = seekBar.getAbsoluteMinValue();
            private float mLastMaxValue = seekBar.getAbsoluteMaxValue();

            @Override
            public void onRangeSeekBarValuesChanged(RangeSeekBar bar, Float minValue, Float maxValue) {
                if (minValue != mLastMinValue) {
                    mVideoView.seekTo((int) (mVideoView.getDuration() * minValue / 100));
                    mLastMinValue = minValue;
                }

                if (maxValue != mLastMaxValue) {
                    mVideoView.seekTo((int) (mVideoView.getDuration() * maxValue / 100));
                    mLastMaxValue = maxValue;
                }
            }
        };
    }

    private final View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.select_video:
                    Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Video.Media.EXTERNAL_CONTENT_URI);
                    intent.setType("video/*");
                    startActivityForResult(Intent.createChooser(intent, getString(R.string.select_video)), SELECT_VIDEO_REQUEST_CODE);
                    break;

                case R.id.trim_video:
                    mLoading.setVisibility(View.VISIBLE);
                    FfmpegUtils.trimVideo(mVideoPath,
                            (int) (mVideoView.getDuration() * mTrimBar.getSelectedMinValue() / 100000),
                            (int) (mVideoView.getDuration() * mTrimBar.getSelectedMaxValue() / 100000),
                            mCallback);
                    break;
            }
        }
    };

    private final FfmpegUtils.Callback mCallback = new FfmpegUtils.Callback() {
        @Override
        public void finished(String file) {
            mLoading.setVisibility(View.GONE);
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setDataAndType(Uri.fromFile(new File(file)), "video/*");
            startActivity(intent);
        }

        @Override
        public void error() {
            mLoading.setVisibility(View.GONE);
            new AlertDialog.Builder(TrimActivity.this)
                    .setTitle(R.string.error)
                    .setMessage(R.string.cant_process_video)
                    .show();
        }
    };
}
