package com.test.trimmer.videoconverter;

import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;

/**
 * Created by nazarko on 5/3/16.
 */
public class UtilFile {


  public String parseUri(Uri uri) {
    String result;
    Cursor cursor = App.getInstance().getContentResolver().query(uri, null, null, null, null, null);
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
}
