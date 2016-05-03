package com.test.trimmer.videoconverter;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

import java.util.Arrays;

/**
 * Created by nazarko on 5/3/16.
 */
public class HomeActivity extends AppCompatActivity {


  CallbackManager callbackManager;

  View.OnClickListener  onTrimListener= new View.OnClickListener() {
    @Override
    public void onClick(View view) {
      startActivity(new Intent(view.getContext(),TrimActivity.class));
    }
  };

  View.OnClickListener  onMergeListener= new View.OnClickListener() {
    @Override
    public void onClick(View view) {
      startActivity(new Intent(view.getContext(),MergeActivity.class));
    }
  };

  View.OnClickListener  onLoginFacebook= new View.OnClickListener() {
    @Override
    public void onClick(View view) {
      LoginManager.getInstance().logInWithReadPermissions(HomeActivity.this, Arrays.asList("public_profile", "user_friends"));
    }
  };


  FacebookCallback<LoginResult>  facebookCallback = new FacebookCallback<LoginResult>() {
    @Override
    public void onSuccess(LoginResult loginResult) {
      startActivity(new Intent(HomeActivity.this,FacebookProfileActivity.class));
    }

    @Override
    public void onCancel() {
      Toast.makeText(HomeActivity.this,"Cancel",Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onError(FacebookException exception) {
      Toast.makeText(HomeActivity.this,"Error"+exception.getMessage(),Toast.LENGTH_SHORT).show();
    }
  };





  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_home);
    initFacebook();
    initListeners();
  }

  private void initFacebook() {
    FacebookSdk.sdkInitialize(getApplicationContext());
    callbackManager = CallbackManager.Factory.create();
    LoginManager.getInstance().registerCallback(callbackManager,facebookCallback);

  }

  private void initListeners() {
    findViewById(R.id.trim).setOnClickListener(onTrimListener);
    findViewById(R.id.facebook_login).setOnClickListener(onLoginFacebook);
    findViewById(R.id.merge).setOnClickListener(onMergeListener);
  }

  @Override
  protected void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    callbackManager.onActivityResult(requestCode, resultCode, data);
  }


}
