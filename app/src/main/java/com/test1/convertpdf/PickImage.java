package com.test1.convertpdf;

import static androidx.core.content.ContextCompat.startActivity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.ComponentActivity;
import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.ActivityResultRegistry;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.DefaultLifecycleObserver;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleOwner;

import java.io.File;
import java.util.ArrayList;

public class PickImage implements DefaultLifecycleObserver {
    private ComponentActivity activity;

    private Context context;
    private final ActivityResultRegistry registry;
    private ActivityResultLauncher<Intent> launched;
    private ArrayList<Uri>imageUris = new ArrayList<>();
    private ArrayList<String>imagePaths = new ArrayList<>();


    public PickImage(ComponentActivity activity, Context context, ActivityResultRegistry registry) {
        this.activity = activity;
        this.context = context;
        this.registry = registry;
    }

    public void pickImage() {
        Intent i = new Intent(Intent.ACTION_PICK);
        i.setType("image/jpg");
        i.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        i.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);

        launched = registry.register("key", new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
            @Override
            public void onActivityResult(ActivityResult result) {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    Intent data = result.getData();
                    if (data != null) {
                        if (data.getClipData() != null) {
                            int count = data.getClipData().getItemCount();
                            Log.d("TAG", "clip data " + data.getClipData());
                            Log.d("TAG", "count " + count);
                            for (int i = 0; i<count; i++) {
                               imageUris.add(result.getData().getClipData().getItemAt(i).getUri());
                            }
                            Log.d("TAG", "image uris " + imageUris);
                            for (Uri u : imageUris) {
                                PathFromUri path = new PathFromUri();
                                String pickedPath = path.getPathFromUri(u, context);
                                if (!(pickedPath.contains(".jpg") || pickedPath.contains(".jpeg"))) {
                                    Toast.makeText(context, "ImageFile Required!", Toast.LENGTH_LONG).show();
                                }
                                imagePaths.add(pickedPath);
                            }
                            Log.d("TAG", "image paths " + imagePaths);
                            if (imagePaths.size() == 1) {
                                String path = imagePaths.get(0);
                                File file = new File(path);
                                String fileName = file.getName();
                                Log.d("TAG", ""+fileName);
                                Intent intent = new Intent(context, uploadActivity.class);
                                intent.putExtra("FileName", fileName);
                                intent.putExtra("Image FilePath", path);
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(context, intent, null);
                            } else {
                                Intent intent = new Intent(context, dialogActivity.class);
                                intent.putExtra("Image FilePath", imagePaths);
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(context, intent, null);
                            }
                        }
                    }
                }
            }
        });

        launched.launch(i);
    }
}
