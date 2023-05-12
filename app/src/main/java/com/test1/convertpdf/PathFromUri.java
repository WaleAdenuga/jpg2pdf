package com.test1.convertpdf;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;

public class PathFromUri {
    public PathFromUri() {
    }

    public String getPathFromUri(Uri uri, Context context) {
//        assert uri != null;

        String result = null;
        String[] proj = { MediaStore.Images.Media.DATA };
        Cursor cursor = context.getContentResolver( ).query(uri, proj, null, null, null );
        if(cursor != null){
            if (cursor.moveToFirst()) {
                int column_index = cursor.getColumnIndexOrThrow(proj[0]);
                result = cursor.getString(column_index);
            }
            cursor.close();
        }
        if(result == null) {
            result = "Not found";
        }
        Log.d("TAG", "result is " + result);
        return result;
    }
}
