package com.test1.convertpdf;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.Camera;
import androidx.camera.core.CameraControl;
import androidx.camera.core.CameraInfo;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.ImageCaptureException;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.LifecycleOwner;

import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.util.Size;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.common.util.concurrent.ListenableFuture;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class cameraActivity extends AppCompatActivity {

    private PreviewView previewView;
    private ImageView shutter;
    private ImageView smallDisplay;
    private ImageCapture capture;
    private Executor executor = Executors.newSingleThreadExecutor();
    private int type;
    private int no_of_images;
    private int counter = 0;
    public TextView pic_taken;
    public TextView pic_counter; //display for picture counter;
    public HashMap<String, Uri> picsTakenMap= new HashMap<>();
    private ArrayList<String>fileNames = new ArrayList<>();
    private File lastFile;
    private String lastFileName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        shutter = (ImageView) findViewById(R.id.shutterImageView);
        smallDisplay= (ImageView) findViewById(R.id.smallImageDisplay);
        previewView = (PreviewView) findViewById(R.id.previewCamera);
        pic_taken = (TextView) findViewById(R.id.pic_taken);
        pic_counter = (TextView) findViewById(R.id.pic_counter);

        smallDisplay.setOnClickListener(this::onClickSmallDisplay);
        shutter.setOnClickListener(this::onClickShutter);
        type = getIntent().getIntExtra("S/M", -5);
        switch (type) {
            case 0: //Multiple
                pic_taken.setVisibility(View.VISIBLE);
                pic_counter.setVisibility(View.VISIBLE);
                pic_counter.setText("1");
                smallDisplay.setVisibility(View.INVISIBLE);
                break;
            case 1: //Single
            default:
                pic_taken.setVisibility(View.INVISIBLE);
                pic_counter.setVisibility(View.INVISIBLE);
                smallDisplay.setVisibility(View.INVISIBLE);
                break;

        }
        Log.d("TAG", "s/m type: " + type);
        no_of_images = getIntent().getIntExtra("Image Number", 0);
        Log.d("TAG", "s/m no: " + no_of_images);
        startCamera();
    }

    public void startCamera() {
        final ListenableFuture<ProcessCameraProvider> cameraProviderListenableFuture = ProcessCameraProvider.getInstance(getApplicationContext());
        cameraProviderListenableFuture.addListener(new Runnable() {
            @Override
            public void run() {
                try {
                    ProcessCameraProvider cameraProvider = cameraProviderListenableFuture.get();
                    bindPreview(cameraProvider);
                } catch (ExecutionException | InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }, ContextCompat.getMainExecutor(this));
    }

    public void bindPreview(@NonNull ProcessCameraProvider cameraProvider) {
        Preview preview = new Preview.Builder().build();
        CameraSelector cameraSelector = new CameraSelector.Builder()
                .requireLensFacing(CameraSelector.LENS_FACING_BACK)
                .build();
        ImageAnalysis imageAnalysis = new ImageAnalysis.Builder()
                .setTargetResolution(new Size(1280,720))
                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                .build();
        ImageCapture.Builder builder = new ImageCapture.Builder();

        capture = builder
                .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
                .setTargetRotation(this.getWindowManager().getDefaultDisplay().getRotation())
                .build();
        preview.setSurfaceProvider(previewView.getSurfaceProvider());
        Camera camera = cameraProvider.bindToLifecycle((LifecycleOwner) this, cameraSelector, preview, imageAnalysis, capture);
        //For performing operations that affects all outputs
        CameraControl control = camera.getCameraControl();
        //For querying information and states
        CameraInfo cameraInfo = camera.getCameraInfo();
        control.setLinearZoom(cameraInfo.getZoomState().getValue().getLinearZoom());
        if (cameraInfo.hasFlashUnit()) {
            control.enableTorch(false);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    public void onClickSmallDisplay(View v) { //Use this to initiate imageview popup (in alertdialog or something like that)
        //At least a pic should be taken before you can expand this
        //That way at least, you have usable uri
        Log.d("TAG", "lastFileName "+ lastFileName);
        Log.d("TAG", "counter " + counter);
        Log.d("TAG", "fileNames arraylist onclicksmalldisplay " + fileNames);
        PathFromUri path = new PathFromUri();
        if (counter > 0) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Page " + counter);
            builder.setMessage("Proceed or delete to capture after Page " + ((counter==1) ? "1" : counter-1));
            final View view = getLayoutInflater().inflate(R.layout.dialog_popup_image, null);
            builder.setView(view);
            final ImageView imageView = (ImageView) view.findViewById(R.id.image_popup);
            Picasso.get().load(new File(path.getPathFromUri(picsTakenMap.get(lastFileName), this))).into(imageView);

            builder.setPositiveButton("Done", (dialog, which) -> dialog.dismiss());
            //New idea ==> Delete and go back one page, how about that
            //Iterate through the hashmap to get the index of the last taken picture which would be deleted
            //However, to go back multiple fold probably requires one or 2 lists which questions the point of the hashmap in the 1st place
            builder.setNegativeButton("Delete", (dialog, which) -> {
            //change textview, hashmap, remove from from gallery,change smaller imageview(how?)
                pic_counter.setText(String.valueOf(counter)); //reduce current by 1
                File file = new File(path.getPathFromUri(picsTakenMap.get(lastFileName), this));
                boolean state = file.delete();
                if (!state) Toast.makeText(getApplicationContext(), "Deletion failed", Toast.LENGTH_SHORT).show();
                else {
                    //___________After Deletion works________________//
                    counter--; //reduce the no of taken pics
                    picsTakenMap.remove(lastFileName); //filename of the last image saved which should be last picture taken
                    fileNames.remove(lastFileName);
                    Toast.makeText(getApplicationContext(), "Deletion successful", Toast.LENGTH_SHORT).show();
                    if (counter > 0) { //this is reaching weird levels of nested tbh
                        //need to have an idea of the uris to replace the small display and the image popup
                        //Since unlike hashmap, the arraylist is ordered, then we can just get the last element of the array list
                        //That is guaranteed to be the picture before last taken
                        //But that's only because we would have removed the current pic(which we deleted) from the array list
                        lastFileName = fileNames.get(fileNames.size()-1);
                        //replace image views with new pics after deletion
                        Picasso.get().load(new File(path.getPathFromUri(picsTakenMap.get(lastFileName), this))).into(imageView);
                        Picasso.get().load(new File(path.getPathFromUri(picsTakenMap.get(lastFileName), this))).into(smallDisplay);
                    } else {
                        smallDisplay.setVisibility(View.INVISIBLE);
                    }
                    Log.d("TAG", "fileNames arraylist after deletion " + fileNames);
                    Log.d("TAG", "lastFileName after deletion "+ lastFileName);
                    Log.d("TAG", "counter after deletion " + counter);
                    Log.d("TAG", "updated hashmap after deletion: " + picsTakenMap);
                    //Dismiss at the end to sort of guarantee recursiveness
                    dialog.dismiss();
                }
            });
            AlertDialog dialog = builder.create();
            dialog.show();
        }
    }

    public void onClickShutter(View v) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.ENGLISH);
        //File file = new File(getDirectoryName(sdf), sdf.format(new Date()) + ".jpg");
        //String filePath = getDirectoryName(sdf);
        //filePath = filePath.replaceAll(":", "."); //looks like this solved the operation not permitted issue
        //File file = new File(filePath);

        ContentValues values = new ContentValues();
        String fileName = sdf.format(System.currentTimeMillis());
        values.put(MediaStore.MediaColumns.DISPLAY_NAME, fileName);
        values.put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg");
        if(Build.VERSION.SDK_INT > Build.VERSION_CODES.P) {
            values.put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/ConvertPDF");
        }

        ImageCapture.OutputFileOptions outputFileOptions = new ImageCapture.OutputFileOptions
                .Builder(getContentResolver(),
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)
                .build();
        capture.takePicture(outputFileOptions, executor, new ImageCapture.OnImageSavedCallback() {
            @Override
            public void onImageSaved(@NonNull ImageCapture.OutputFileResults outputFileResults) {
                // Display the saved picture
                switch (type) {
                    case 0: //Multi image capture
                        counter++;
                        fileNames.add(fileName); //have an arraylist for file names, to help with the small display deletion stuff
                        picsTakenMap.put(fileName, outputFileResults.getSavedUri());
                        PathFromUri path = new PathFromUri();
                        lastFile = new File(path.getPathFromUri(outputFileResults.getSavedUri(), getApplicationContext()));
                        lastFileName = fileName;

                        Log.d("TAG", "picsTaken hashmap on ImageSaved " + picsTakenMap);
                        Log.d("TAG", "lastFile on ImageSaved " + lastFile);
                        Log.d("TAG", "lastFileName on ImageSaved " + lastFileName);

                        if (counter == no_of_images) { //I think I need another activity for the multi image display, too complicated to use the same one
                            //Also I have no idea how to even use it tbh
                            Bundle bundle = new Bundle();
                            bundle.putSerializable("Uri Map", picsTakenMap);

                            Intent intent = new Intent(cameraActivity.this, MultiDisplay.class);
                            intent.putExtra("Pics Counter", counter);
                            intent.putExtra("Taken Pics Uri", bundle);
                            startActivity(intent);
                        } else {
                            //To solve : Only the original thread that created a view hierarchy can touch its views
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    smallDisplay.setVisibility(View.VISIBLE);
                                    pic_counter.setText(String.valueOf(counter+1));
                                    Picasso.get().load(lastFile).into(smallDisplay);
                                }
                            });
                        }
                        break;
                    case 1: //Single image capture
                    default:
                        Intent intent = new Intent(cameraActivity.this, displayActivity.class);
                        intent.putExtra("Image OutputFileResults", Objects.requireNonNull(outputFileResults.getSavedUri()).toString());
                        intent.putExtra("Image FileName", fileName);
                        startActivity(intent);
                        break;
                }
            }
            @Override
            public void onError(@NonNull ImageCaptureException exception) {
                exception.printStackTrace();
            }
        });
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }

    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        super.finish();
        return super.onOptionsItemSelected(item);

    }

    public String getDirectoryName(SimpleDateFormat sdf) {
        try {
            File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
            File file = File.createTempFile(sdf.format(new Date()), ".jpg", storageDir);
            return file.getAbsolutePath();
        } catch(Exception e) {
            Log.e("Camera", "Exception while creating a file");
            e.printStackTrace();
        }
        return "";
    }

/*    ActivityResultLauncher<Intent> activityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
        @Override
        public void onActivityResult(ActivityResult result) {
            if (result.getResultCode() == Activity.RESULT_OK) {
                Intent data = result.getData();

                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inScaled = false;
                options.inDither = false;
                options.inPreferredConfig = Bitmap.Config.ARGB_8888;
                Bitmap bit = (Bitmap) data.getExtras().get("data");
                display.setImageBitmap(bit);
                if (display == null) {
                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    startActivity(intent);
                }
            }
        }
    });*/
}