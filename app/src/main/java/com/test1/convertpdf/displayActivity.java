package com.test1.convertpdf;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.ImageCapture;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.FileProvider;

import android.content.ActivityNotFoundException;
import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Image;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfWriter;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Objects;

public class displayActivity extends AppCompatActivity {
    private Uri imageUri;
    private ProgressBar bar;
    private Button convert;
    private String imageFilePath;
    private String imageFileName;
    private String pdfFilePath;
    private Button button;
    private static final String pdf_directory = "/Documents/ConvertPDF";
    private static final String pdf_tag = ".pdf";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        imageUri = Uri.parse(getIntent().getStringExtra("Image OutputFileResults"));
        imageFileName = getIntent().getStringExtra("Image FileName");

        PathFromUri path = new PathFromUri();
        imageFilePath = path.getPathFromUri(imageUri, getApplicationContext());

        ImageView display = (ImageView) findViewById(R.id.displayView);
        ImageView retake = (ImageView) findViewById(R.id.retakeView);
        retake.setOnClickListener(this::onClickRetake);

        Picasso.get().load(new File(imageFilePath)).into(display);
        //display.setImageURI(imageUri);
        //Bitmap bitmap = BitmapFactory.decodeFile(imageFilePath);
        //display.setRotation(90);
        //display.setImageBitmap(bitmap);

        bar = (ProgressBar) findViewById(R.id.bar);
        bar.setVisibility(View.INVISIBLE);

        convert = (Button) findViewById(R.id.convertButton);
        convert.setOnClickListener(this::onClickConvert);

        button = (Button) findViewById(R.id.convertedFilesButton);
        button.setVisibility(View.INVISIBLE);
        button.setOnClickListener(this::onClickConvertedFilesButton);

        if (!checkIfExists()) {
            button.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    public void onClickConvertedFilesButton(View v) {
        openPDF open = new openPDF(pdfFilePath);
        open.openPDF(getApplicationContext());
    }

    public boolean checkIfExists() {
        int count = 0;
        //Going via environment. is better than providing the full path through intent
        //Providing the full path led to a null pointer exception
        //Better to hardcode the final path, the other avenues don't look faster
        File file = new File(Environment.getExternalStorageDirectory() + pdf_directory);
        if (file.isDirectory()) {
            for (File f : Objects.requireNonNull(file.listFiles())) {
                count++;
            }
        }
        return count > 0 || file.exists();
    }

    public void onClickRetake(View v) {
        //let's do a delete picture taken if retake is clicked
        File file = new File(imageFilePath);
        boolean state = file.delete();
        if (!state) {
            Toast.makeText(getApplicationContext(), "Deletion failed", Toast.LENGTH_SHORT).show();
        } else Toast.makeText(getApplicationContext(), "Deletion successful", Toast.LENGTH_SHORT).show();
        onBackPressed();
    }

    public void onClickConvert(View v){
        convert.setVisibility(View.INVISIBLE);
        bar.setVisibility(View.VISIBLE);

        //Added itext jar file because gradle was being annoying
        convertToPdf convert = new convertToPdf(getApplicationContext());
        pdfFilePath = convert.jpgToPdf(imageFilePath, imageFileName,0);

        bar.setVisibility(View.INVISIBLE);
        button.setVisibility(View.VISIBLE);
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }

    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        onBackPressed();
        return super.onOptionsItemSelected(item);

    }

}