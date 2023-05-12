package com.test1.convertpdf;

import static androidx.core.content.ContextCompat.startActivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.widget.Toast;

import androidx.activity.ComponentActivity;
import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.ActivityResultRegistry;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.lifecycle.DefaultLifecycleObserver;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleOwner;

import java.io.File;

public class PickImage implements DefaultLifecycleObserver {
    private ComponentActivity activity;

    private Context context;
    private final ActivityResultRegistry registry;
    private ActivityResultLauncher<Intent> launched;

    public PickImage(ComponentActivity activity, Context context, ActivityResultRegistry registry) {
        this.activity = activity;
        this.context = context;
        this.registry = registry;
    }

    public void pickImage() {
        Intent i = new Intent(Intent.ACTION_PICK);
        i.setType("image/jpg");
        i.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
        i.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);

        launched = registry.register("key", new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
            @Override
            public void onActivityResult(ActivityResult result) {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    Intent data = result.getData();
                    Uri uri = data.getData();
                    PathFromUri path = new PathFromUri();
                    String pickedPath = path.getPathFromUri(uri, context);
                    if (!(pickedPath.contains(".jpg") || pickedPath.contains(".jpeg"))) {
                        Toast.makeText(context, "ImageFile Required!", Toast.LENGTH_LONG).show();
                    } else {
                        Log.d("TAG", "" + pickedPath);
                        Log.d("TAG", "uri " + uri);
                        Log.d("TAG", "uri path " + uri.getPath());

                        File file = new File(pickedPath);
                        String fileName = file.getName();
                        Log.d("TAG", ""+fileName);

                        Intent intent = new Intent(context, uploadActivity.class);
                        intent.putExtra("FileName", fileName);
                        intent.putExtra("Image FilePath", pickedPath);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(context, intent, null);
                    }
                }
            }
        });

        launched.launch(i);
    }
}
