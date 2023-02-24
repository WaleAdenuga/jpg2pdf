package com.test1.convertpdf;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.common.util.concurrent.ListenableFuture;

import java.util.ArrayList;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity {
    private ImageView camera;
    private ImageView upload;

    private static final int REQUEST_PERMISSION_CODE = 102;
    private final String[] permission = new String[] {Manifest.permission.WRITE_EXTERNAL_STORAGE,
             Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.CAMERA};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        camera = (ImageView) findViewById(R.id.cameraView);
        upload = (ImageView) findViewById(R.id.UploadView);

        camera.setOnClickListener(this::onClickCamera);
        upload.setOnClickListener(this::onClickUpload);

        checkPermission(permission, REQUEST_PERMISSION_CODE);
    }

    public void onClickCamera(View v) {
        //Check camera and storage permissions ==> camera to take pictures, storage to save them
        Intent intent = new Intent(this, cameraActivity.class);
        startActivity(intent);
    }

    public void onClickUpload(View v) {
        Intent intent = new Intent(this, uploadActivity.class);
        startActivity(intent);
    }

    public void checkPermission(String[] permission, int requestCode)
    {
        // Checking if permission is not granted, request if not
        ArrayList<String> ungrantedPermissions = new ArrayList<>();
        for (String i : permission) {
            if (ContextCompat.checkSelfPermission(MainActivity.this, i) == PackageManager.PERMISSION_DENIED) {
                ungrantedPermissions.add(i);
            }
        }
        //Faster to implement as new String[0] instead of correct length
        if (ungrantedPermissions.size() > 0) {
            ActivityCompat.requestPermissions(MainActivity.this, ungrantedPermissions.toArray(new String[0]), requestCode);
        }
    }

    public boolean allGranted(@NonNull int[] grantResults) {
        for (int i : grantResults) {
            if (i == PackageManager.PERMISSION_DENIED) {
                return false;
            }
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults)
    {
        super.onRequestPermissionsResult(requestCode,
                permissions,
                grantResults);
        if(allGranted(grantResults)) {
            Toast.makeText(this, "All permissions granted", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Application can't function without granting permissions", Toast.LENGTH_LONG).show();
            MainActivity.this.finish();
            System.exit(0);
        }
    }
}