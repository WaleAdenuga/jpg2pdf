package com.test1.convertpdf;

import androidx.activity.ComponentActivity;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import java.util.ArrayList;

public class dialogActivity extends AppCompatActivity {

    private String pdfFilePath;
    private String fileName;
    private ArrayList<String> imagePaths = new ArrayList<>();
    private int cond = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.dialog_input_filename);

        imagePaths = getIntent().getStringArrayListExtra("Image FilePath");

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Name your converted file");
        final View view = getLayoutInflater().inflate(R.layout.dialog_input_filename, null);
        builder.setView(view);
        final EditText edit = (EditText) view.findViewById(R.id.inputfilename);

        builder.setPositiveButton("Done", ((dialog, which) -> {
            fileName = edit.getText().toString();
            Log.d("TAG", "fileName is " + fileName);
            showPdf(fileName);
            cond = 1;
            dialog.dismiss();
        }));

        builder.setNegativeButton("Cancel", (dialog, which) -> {
            dialog.dismiss();
        });

        builder.setNeutralButton("Re-upload", ((dialog, which) -> {
            PickImage pick = new PickImage((ComponentActivity) dialogActivity.this, this, getActivityResultRegistry());
            pick.pickImage();
            dialog.dismiss();
        }));

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public void showPdf(String fileName) {
        convertToPdf convert = new convertToPdf(this);
        pdfFilePath = convert.jpgToPdf(null, fileName,-4, 0, imagePaths);

        if (fileName != null && pdfFilePath != null) {
            Toast.makeText(this, "Opening Converted PDF", Toast.LENGTH_SHORT).show();

            openPDF pdf = new openPDF(pdfFilePath);
            pdf.openPDF(this);
        } else Toast.makeText(this, "Conversion Failed", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onResume() {
        if (cond == 1) onBackPressed();
        super.onResume();
    }
}