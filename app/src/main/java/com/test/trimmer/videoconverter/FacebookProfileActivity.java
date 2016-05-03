package com.test.trimmer.videoconverter;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.facebook.Profile;
import com.facebook.login.widget.ProfilePictureView;

/**
 * Created by nazarko on 5/3/16.
 */
public class FacebookProfileActivity  extends AppCompatActivity {

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_profie);
    getProfileAvatar();
  }

  private void getProfileAvatar() {
    ProfilePictureView profilePictureView;
    profilePictureView = (ProfilePictureView) findViewById(R.id.profile_avatar);
    profilePictureView.setProfileId(Profile.getCurrentProfile().getId());
  }

}
