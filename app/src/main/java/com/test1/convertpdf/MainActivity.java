package com.test1.convertpdf;

import androidx.activity.ComponentActivity;
import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.ActivityResultRegistry;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;

public class MainActivity extends AppCompatActivity {
    private ImageView camera;
    private ImageView upload;
    private ImageView multipleImage;

    private static final int REQUEST_PERMISSION_CODE = 102;
    private final ArrayList<String> permission = new ArrayList<String>(Arrays.asList(Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.CAMERA));

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        multipleImage = (ImageView) findViewById(R.id.multiple);
        camera = (ImageView) findViewById(R.id.cameraView);
        upload = (ImageView) findViewById(R.id.UploadView);

        camera.setOnClickListener(this::onClickCamera);
        upload.setOnClickListener(this::onClickUpload);



        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            permission.add(Manifest.permission.READ_MEDIA_IMAGES);
            permission.remove(Manifest.permission.READ_EXTERNAL_STORAGE);
            permission.remove(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
        checkPermission(permission, REQUEST_PERMISSION_CODE);
    }


    public void onClickMultiple(View v) {
        //Plan - Launch camera activity as usual but have an integer in the intent
        //Kind of a conditional variable, then based
    }

    public void onClickCamera(View v) {
        //Check camera and storage permissions ==> camera to take pictures, storage to save them
        Intent intent = new Intent(this, cameraActivity.class);
        startActivity(intent);
    }

//replace startActivityForResult(intent), that's deprecated, the onActivityResult is for the result of the intent
ActivityResultLauncher<Intent> launcher = registerForActivityResult(
    new ActivityResultContracts.StartActivityForResult(),
    new ActivityResultCallback<ActivityResult>() {
        @Override
        public void onActivityResult(ActivityResult result) {
            if (result.getResultCode() == Activity.RESULT_OK) {
                Intent data = result.getData();
                Uri uri = data.getData();
                PathFromUri path = new PathFromUri();
                String pickedPath = path.getPathFromUri(uri, getApplicationContext());
                if (!(pickedPath.contains(".jpg") || pickedPath.contains(".jpeg"))) {
                    Toast.makeText(getApplicationContext(), "ImageFile Required!", Toast.LENGTH_LONG).show();
                } else {
                    Log.d("TAG", "" + pickedPath);
                    Log.d("TAG", "uri " + uri);
                    Log.d("TAG", "uri path " + uri.getPath());

                    File file = new File(pickedPath);
                    String fileName = file.getName();
                    Log.d("TAG", ""+fileName);

                    Intent intent = new Intent(getApplicationContext(), uploadActivity.class);
                    intent.putExtra("FileName", fileName);
                    intent.putExtra("Image FilePath", pickedPath);
                    startActivity(intent);
                }
            }
        }
    });

    public void onClickUpload(View v) {
        //For some reason, I can't figure out how to get the actual path from this uri
        //And we get a FileNotFoundException if we try to use like it is, so comment out and look at it later
        //if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        //    i = new Intent(MediaStore.ACTION_PICK_IMAGES);
            //Intent i = new Intent(Intent.ACTION_PICK);
            //i.setType("image/jpg");
            //i.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
            //launcher.launch(i);
        //} else {}
        //Intent i = new Intent(Intent.ACTION_PICK);
        //i.setType("image/jpg");
        //i.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
        //i.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        PickImage result = new PickImage((ComponentActivity) MainActivity.this, getApplicationContext(), getActivityResultRegistry());
        result.pickImage();
    }

    public void checkPermission(ArrayList<String> permission, int requestCode)
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