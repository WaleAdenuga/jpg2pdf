package com.test1.convertpdf;

import androidx.activity.ComponentActivity;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ActionBar;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.Objects;

public class uploadActivity extends AppCompatActivity {

    private String providedFileName;
    private String imageFilePath;
    private String pdfFilePath;
    private int cond = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.dialog_input_filename);
        //Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        providedFileName = getIntent().getStringExtra("FileName");
        imageFilePath = getIntent().getStringExtra("Image FilePath");

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Name your converted file");
        final View view = getLayoutInflater().inflate(R.layout.dialog_input_filename, null);
        builder.setView(view);
        final EditText edit = (EditText) view.findViewById(R.id.inputfilename);

        builder.setPositiveButton("Done", ((dialog, which) -> {
            providedFileName = edit.getText().toString();
            Log.d("TAG", "fileName is " + providedFileName);
            showPdf(providedFileName);
            cond = 1;
            dialog.dismiss();
        }));

        builder.setNegativeButton("Cancel", (dialog, which) -> {
            dialog.dismiss();
        });

        builder.setNeutralButton("Re-upload", ((dialog, which) -> {
            PickImage pick = new PickImage((ComponentActivity) uploadActivity.this, this, getActivityResultRegistry());
            pick.pickImage();
            dialog.dismiss();
        }));

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public void showPdf(String fileName) {
        convertToPdf convert = new convertToPdf(this);
        pdfFilePath = convert.jpgToPdf(imageFilePath, fileName,-4, 1, null);

        if (pdfFilePath != null) {
            Toast.makeText(this, "Opening Converted PDF", Toast.LENGTH_SHORT).show();

            openPDF pdf = new openPDF(pdfFilePath);
            pdf.openPDF(this);
        } else Toast.makeText(this, "Conversion Failed", Toast.LENGTH_SHORT).show();

    }

    @Override
    protected void onResume() {
        if (cond == 1) onBackPressed();
        super.onResume();
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }


    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }

    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        //super.finish();
        onBackPressed();
        return super.onOptionsItemSelected(item);

    }
}