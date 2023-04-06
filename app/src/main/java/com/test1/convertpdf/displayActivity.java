package com.test1.convertpdf;

import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.ImageCapture;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Context;
import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.util.Objects;

public class displayActivity extends AppCompatActivity {
    private Uri uri;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        uri = Uri.parse(getIntent().getStringExtra("OutputFileResults"));

        ImageView display = (ImageView) findViewById(R.id.displayView);
        ImageView retake = (ImageView) findViewById(R.id.retakeView);
        retake.setOnClickListener(this::onClickRetake);

        display.setImageURI(uri);
        Bitmap bitmap = BitmapFactory.decodeFile(getPathFromUri(uri));
        display.setImageBitmap(bitmap);
    }

    public void onClickRetake(View v) {
        //let's do a delete picture taken if retake is clicked
        String filePath = getPathFromUri(uri);
        File file = new File(filePath);
        boolean state = file.delete();
        if (!state) {
            Toast.makeText(getApplicationContext(), "Deletion failed", Toast.LENGTH_SHORT).show();
        } else Toast.makeText(getApplicationContext(), "Deletion successful", Toast.LENGTH_SHORT).show();
        onBackPressed();
    }

    public String getPathFromUri(Uri uri) {
        assert uri != null;

        String result = null;
        String[] proj = { MediaStore.Images.Media.DATA };
        Cursor cursor = getApplicationContext().getContentResolver( ).query(uri, proj, null, null, null );
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