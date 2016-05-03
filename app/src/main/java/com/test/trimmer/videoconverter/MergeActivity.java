package com.test.trimmer.videoconverter;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import org.florescu.android.rangeseekbar.RangeSeekBar;

/**
 * Created by nazarko on 5/3/16.
 */
public class MergeActivity extends AppCompatActivity {


  private final int[] SELECT_VIDEO_REQUEST_CODES = {18795,18796};


  View.OnClickListener onMergeListener = new View.OnClickListener() {
    @Override
    public void onClick(View view) {
      Toast.makeText(MergeActivity.this,"Merge",Toast.LENGTH_SHORT).show();
    }
  };

  View.OnClickListener onSelectFileListener = new View.OnClickListener() {
    @Override
    public void onClick(View view) {
      switch (view.getId()){
        case R.id.button_one:
          startSelectFileActivity(SELECT_VIDEO_REQUEST_CODES[0]);
          break;
        case R.id.button_two:
          startSelectFileActivity(SELECT_VIDEO_REQUEST_CODES[1]);
          break;

      }
    }
  };


  VideoView []   videoFileViews    = new VideoView[2];
  TextView  []   textFileNameViews = new TextView[2];
  String    []   nameFiles         = new String[2];

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_merge);
    initViews();
    initListener();

  }

  private void initViews() {
    videoFileViews[0]=(VideoView) findViewById(R.id.video_one);
    videoFileViews[1]=(VideoView) findViewById(R.id.video_two);
    textFileNameViews[0] =(TextView) findViewById(R.id.name_file_one);
    textFileNameViews[1] =(TextView) findViewById(R.id.name_file_two);
  }

  private void initListener() {
    findViewById(R.id.merge).setOnClickListener(onMergeListener);
    findViewById(R.id.button_one).setOnClickListener(onSelectFileListener);
    findViewById(R.id.button_two).setOnClickListener(onSelectFileListener);
  }

  @Override
  protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    if (resultCode == RESULT_OK) {
      if (requestCode==SELECT_VIDEO_REQUEST_CODES[0]){
        nameFiles[0]=new UtilFile().parseUri(data.getData());
        textFileNameViews[0].setText(nameFiles[0]);
        setVideo(data.getData(),videoFileViews[0]);
      }

      if (requestCode==SELECT_VIDEO_REQUEST_CODES[1]) {
        nameFiles[1]=new UtilFile().parseUri(data.getData());
        textFileNameViews[1].setText(nameFiles[1]);
        setVideo(data.getData(), videoFileViews[1]);
      }


    }
  }



  private  void startSelectFileActivity(int id){
    Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Video.Media.EXTERNAL_CONTENT_URI);
    intent.setType("video/*");
    startActivityForResult(Intent.createChooser(intent, getString(R.string.select_video)), id);
  }


  private  void setVideo(Uri uri,VideoView videoView) {
      videoView.setVideoURI(uri);
      videoView.seekTo(1);


  }




}
