package com.test1.convertpdf;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ActionBar;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload);

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

        convertedFileTextView.setVisibility(View.INVISIBLE);
        pdfFileNameText.setVisibility(View.INVISIBLE);
        convertView.setVisibility(View.INVISIBLE);
        viewButton.setVisibility(View.INVISIBLE);

        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

    }

    public void onClickSwitch(View v) {
        uploadConvertButton.setVisibility(View.VISIBLE);
        convertedFileTextView.setVisibility(View.INVISIBLE);
        convertView.setVisibility(View.INVISIBLE);
        viewButton.setVisibility(View.INVISIBLE);
        pdfFileNameText.setVisibility(View.INVISIBLE);
    }

    public void onClickView(View v) {

    }

    public void onClickUploadConvert(View v) {
        convertedFileTextView.setVisibility(View.VISIBLE);
        convertView.setVisibility(View.VISIBLE);
        viewButton.setVisibility(View.VISIBLE);
        pdfFileNameText.setVisibility(View.VISIBLE);
        uploadConvertButton.setVisibility(View.INVISIBLE);

    }

    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }

    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        super.finish();
        return super.onOptionsItemSelected(item);

    }
}