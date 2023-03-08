package com.test1.convertpdf;

import androidx.annotation.NonNull;
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
import androidx.core.content.ContextCompat;
import androidx.lifecycle.LifecycleOwner;

import android.content.ContentValues;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.util.Size;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.google.common.util.concurrent.ListenableFuture;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class cameraActivity extends AppCompatActivity {

    private Button captureButton;
    private PreviewView previewView;
    private ImageView display;
    private ImageCapture capture;
    private Executor executor = Executors.newSingleThreadExecutor();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        captureButton = (Button) findViewById(R.id.captureButton);
        previewView = (PreviewView) findViewById(R.id.previewCamera);

        captureButton.setOnClickListener(this::onClickCapture);

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
            control.enableTorch(true);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    public void onClickCapture(View v) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.ENGLISH);
        //File file = new File(getDirectoryName(sdf), sdf.format(new Date()) + ".jpg");
        //String filePath = getDirectoryName(sdf);
        //filePath = filePath.replaceAll(":", "."); //looks like this solved the operation not permitted issue
        //File file = new File(filePath);

        ContentValues values = new ContentValues();
        values.put(MediaStore.MediaColumns.DISPLAY_NAME, sdf.format(System.currentTimeMillis()));
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

                //Store saved picture to gallery only if image is saved
                Log.d("TAG", "Is this even reached");
                Log.d("TAG", Objects.requireNonNull(outputFileResults.getSavedUri()).toString());

                /*2023-03-02 14:39:28.421 7308-8065/com.test1.convertpdf D/TAG: content://media/external/images/media/1000000069                 */
                Log.d("TAG", "Surely not reached right");
                Log.d("TAG", outputFileResults.getSavedUri().getEncodedPath());

                // Display the saved picture
                showDisplay(outputFileResults);
            }
            @Override
            public void onError(@NonNull ImageCaptureException exception) {
                exception.printStackTrace();
            }
        });
        //Log.d("Camera", file.getAbsolutePath());
        //Toast.makeText(cameraActivity.this, "Image saved in " + file.getAbsolutePath() + "successfully", Toast.LENGTH_LONG).show();
    }

    public void showDisplay(ImageCapture.OutputFileResults results) {
        LayoutInflater inflater = getLayoutInflater();
        View view = inflater.inflate(R.layout.activity_display, null);
        ImageView imageView = (ImageView) view.findViewById(R.id.displayView);
        imageView.setImageURI(results.getSavedUri());

//        // Get the dimensions of the View
//        int targetW = imageView.getWidth();
//        int targetH = imageView.getHeight();
//
//        // Get the dimensions of the bitmap
//        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
//        bmOptions.inJustDecodeBounds = true;
//
//        int photoW = bmOptions.outWidth;
//        int photoH = bmOptions.outHeight;
//
//        // Determine how much to scale down the image
//        int scaleFactor = Math.min(photoW/targetW, photoH/targetH);
//
//        // Decode the image file into a Bitmap sized to fill the View
//        bmOptions.inJustDecodeBounds = false;
//        bmOptions.inSampleSize = scaleFactor;
//        bmOptions.inPurgeable = true;

        //Bitmap bitmap = BitmapFactory.decodeFile(Objects.requireNonNull(results.getSavedUri()).getPath());
        //imageView.setImageBitmap(bitmap);
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