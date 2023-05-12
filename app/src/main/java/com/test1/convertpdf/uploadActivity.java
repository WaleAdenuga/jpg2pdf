package com.test1.convertpdf;

import androidx.activity.ComponentActivity;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ActionBar;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.io.File;
import java.util.Objects;

public class uploadActivity extends AppCompatActivity {

    private Button switchButton;
    private Button viewButton;
    private Button uploadConvertButton;
    private View selectView;
    private View convertView;
    private TextView convertedFileTextView;
    private TextView jpgFileNameText;
    private TextView pdfFileNameText;
    private String providedFileName;
    private String imageFilePath;
    private String pdfFilePath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload);

        providedFileName = getIntent().getStringExtra("FileName");
        imageFilePath = getIntent().getStringExtra("Image FilePath");

        switchButton = (Button) findViewById(R.id.switchButton);
        viewButton = (Button) findViewById(R.id.viewButton);
        uploadConvertButton = (Button) findViewById(R.id.uploadConvertButton);
        selectView = (View) findViewById(R.id.selectView);
        convertView = (View) findViewById(R.id.convertedView);
        convertedFileTextView = (TextView) findViewById(R.id.convertedFileTextView);
        jpgFileNameText = (TextView) findViewById(R.id.jpgFileName);
        pdfFileNameText = (TextView) findViewById(R.id.pdfFileName);

        switchButton.setOnClickListener(this::onClickSwitch);
        viewButton.setOnClickListener(this::onClickView);
        uploadConvertButton.setOnClickListener(this::onClickUploadConvert);
        selectView.setOnClickListener(this::onClickSwitch);
        convertView.setOnClickListener(this::onClickView);

        convertedFileTextView.setVisibility(View.INVISIBLE);
        pdfFileNameText.setVisibility(View.INVISIBLE);
        convertView.setVisibility(View.INVISIBLE);
        viewButton.setVisibility(View.INVISIBLE);

        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        jpgFileNameText.setText(providedFileName);
    }

    public void onClickSwitch(View v) {
        uploadConvertButton.setVisibility(View.VISIBLE);
        convertedFileTextView.setVisibility(View.INVISIBLE);
        convertView.setVisibility(View.INVISIBLE);
        viewButton.setVisibility(View.INVISIBLE);
        pdfFileNameText.setVisibility(View.INVISIBLE);

        PickImage pick = new PickImage((ComponentActivity) uploadActivity.this, getApplicationContext(), getActivityResultRegistry());
        pick.pickImage();
    }

    public void onClickView(View v) {
        openPDF open = new openPDF(pdfFilePath);
        open.openPDF(getApplicationContext());
    }

    public void onClickUploadConvert(View v) {
        convertedFileTextView.setVisibility(View.VISIBLE);
        convertView.setVisibility(View.VISIBLE);
        viewButton.setVisibility(View.VISIBLE);
        pdfFileNameText.setVisibility(View.VISIBLE);
        uploadConvertButton.setVisibility(View.INVISIBLE);

        convertToPdf convert = new convertToPdf(getApplicationContext());
        pdfFilePath = convert.jpgToPdf(imageFilePath, providedFileName, -1);
        File file = new File(pdfFilePath);
        pdfFileNameText.setText(file.getName());
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }

    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        super.finish();
        return super.onOptionsItemSelected(item);

    }
}