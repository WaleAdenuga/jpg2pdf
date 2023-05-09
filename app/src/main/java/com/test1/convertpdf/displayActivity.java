package com.test1.convertpdf;

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

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Objects;

public class displayActivity extends AppCompatActivity {
    private Uri uri;
    private ProgressBar bar;
    private Button convert;
    private String filePath;
    private String fileName;
    private Button button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        uri = Uri.parse(getIntent().getStringExtra("OutputFileResults"));
        fileName = getIntent().getStringExtra("FileName");
        filePath = getPathFromUri(uri);

        ImageView display = (ImageView) findViewById(R.id.displayView);
        ImageView retake = (ImageView) findViewById(R.id.retakeView);
        retake.setOnClickListener(this::onClickRetake);

        display.setImageURI(uri);
        Bitmap bitmap = BitmapFactory.decodeFile(filePath);
        display.setRotation(90);
        display.setImageBitmap(bitmap);

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

    public void onClickConvertedFilesButton(View v) {
        File file = new File(Environment.getExternalStorageDirectory(), "Documents/ConvertPDF/" + fileName + ".pdf");
        Log.d("TAG", "" + file);
        Log.d("TAG", "" + file.getAbsolutePath());

        Uri uri = FileProvider.getUriForFile(this, getApplicationContext().getPackageName() + ".provider", file);
        Log.d("TAG", "" + uri);

        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.setClipData(ClipData.newRawUri("", uri));
        intent.setDataAndType(uri, "application/pdf");
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);

        try {
            startActivity(intent);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(this, "No Available PDF APP", Toast.LENGTH_LONG).show();
        }
    }

    public boolean checkIfExists() {
        int count = 0;
        //Going via environment. is better than providing the full path through intent
        //Providing the full path led to a null pointer exception
        //Better to hardcode the final path, the other avenues don't look faster
        File file = new File(Environment.getExternalStorageDirectory() + "/Documents/ConvertPDF");
        if (file.isDirectory()) {
            for (File f : Objects.requireNonNull(file.listFiles())) {
                count++;
            }
        }
        return count > 0 || file.exists();
    }

    public void onClickRetake(View v) {
        //let's do a delete picture taken if retake is clicked
        File file = new File(filePath);
        boolean state = file.delete();
        if (!state) {
            Toast.makeText(getApplicationContext(), "Deletion failed", Toast.LENGTH_SHORT).show();
        } else Toast.makeText(getApplicationContext(), "Deletion successful", Toast.LENGTH_SHORT).show();
        onBackPressed();
    }

    public void onClickConvert(View v){
        convert.setVisibility(View.INVISIBLE);
        bar.setVisibility(View.VISIBLE);
        String dirName = "/Documents/ConvertPDF";

        //Added itext jar file because gradle was being annoying
        try {
            File file = new File(Environment.getExternalStorageDirectory()+dirName);
            if (!file.exists()) {
                boolean state = file.mkdirs();
                if (!state) {
                    Toast.makeText(getApplicationContext(), "Making directory failed", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getApplicationContext(), "Making directory success", Toast.LENGTH_SHORT).show();
                }
            }
            Log.d("TAG", "on click convert file 1 " + file.getAbsolutePath());

            File pdfFile = new File(file.getAbsolutePath(), fileName+".pdf");
            Log.d("TAG", "on click convert pdf file " + pdfFile.getAbsolutePath());

            Document document = new Document(PageSize.A4, 0, 0, 0, 0);
            PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(pdfFile));
            document.open();

            Image image = Image.getInstance(filePath);
            image.setRotationDegrees(270); //images are always flipped for some reason
            image.scaleToFit(image.getWidth()+1f, image.getHeight()+1f);
            //image.setAbsolutePosition(0,0);

            //when you call scale to fit, height and width change so you have to explicitly call getScaledHeight and getScaledWeight
            Rectangle size = new Rectangle(image.getScaledWidth()+1f, image.getScaledHeight()+1f);
            document.setPageSize(size);


            document.newPage();
            document.add(image);
            document.close();
            writer.close();

            bar.setVisibility(View.INVISIBLE);
            button.setVisibility(View.VISIBLE);
        } catch (DocumentException | IOException e) {
            e.printStackTrace();
        }
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