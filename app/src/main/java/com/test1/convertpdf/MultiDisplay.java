package com.test1.convertpdf;

import androidx.activity.ComponentActivity;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Locale;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

public class MultiDisplay extends AppCompatActivity {
    private String fileName;
    private HashMap<String, Uri> displayMap = new HashMap<>();
    private ProgressBar bar;
    private Button convert;
    private Button button;
    private String pdfFilePath;
    private ViewPager pager;
    private PagerAdapter adapter;
    private ArrayList<String>paths = new ArrayList<String>();
    private ArrayList<String>keys = new ArrayList<String>();
    private ImageView delete;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_multi_display);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        Bundle bundle = getIntent().getBundleExtra("Taken Pics Uri");

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            displayMap = bundle.getSerializable("Uri Map", HashMap.class);
        } else displayMap = (HashMap) bundle.getSerializable("Uri Map");

        Log.d("TAG", "Uri map from multi display "+ displayMap);
        displayImages();
        pager = (ViewPager) findViewById(R.id.multi_viewpager);
        Log.d("TAG", "paths before setting adapter " + paths);
        adapter = new PagerAdapter(getApplicationContext(), paths);
        pager.setAdapter(adapter);

        delete = (ImageView) findViewById(R.id.delete_view);
        delete.setOnClickListener(this::onClickDelete);

        bar = (ProgressBar) findViewById(R.id.multi_bar);
        bar.setVisibility(View.INVISIBLE);

        convert = (Button) findViewById(R.id.multi_convertButton);
        convert.setOnClickListener(this::onClickMultiConvert);

        button = (Button) findViewById(R.id.multi_convertedFilesButton);
        button.setVisibility(View.INVISIBLE);
        button.setOnClickListener(this::onClickMultiConvertedFilesButton);

    }

    public void onClickDelete(View v) {
        //another gimmicky thing that'll just give me headaches
        Log.d("TAG", "paths before deletion " + paths);
        int position = pager.getCurrentItem();
        File file = new File(paths.get(position));
        boolean state = file.delete();
        if (!state) Toast.makeText(getApplicationContext(), "Deletion failed", Toast.LENGTH_SHORT).show();
        else {
            Toast.makeText(getApplicationContext(), "Deletion successful", Toast.LENGTH_SHORT).show();
            Log.d("TAG", "position of pager " + position);
            adapter.removeItem(position);
            pager.setAdapter(adapter);
            paths = adapter.getPaths();
            //sort of allow re-conversion
            if (convert.getVisibility() == View.INVISIBLE) {
                convert.setVisibility(View.VISIBLE);
                button.setVisibility(View.INVISIBLE);
                bar.setVisibility(View.INVISIBLE);
            }
            if (paths.size() <= 0) {
                Toast.makeText(this, "No images detected", Toast.LENGTH_LONG).show(); //fingers crossed toast works
                Intent i = new Intent(this, MainActivity.class);
                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(i);
            }
            Log.d("TAG", "adapter paths after deletion " + adapter.getPaths());
            Log.d("TAG", "adapter paths size after deletion " + adapter.getPaths().size());

            Log.d("TAG", "paths after deletion " + paths);
            Log.d("TAG", "paths size after deletion " + paths.size());
        }
    }

    public void displayImages() {
        //Hashmaps are cool and all but goodness me why aren't they ordered
        //To sort, get the keys into a list and sort, then get from hashmap
        Set<String> keyset =  displayMap.keySet();
        keys.addAll(keyset);
        Collections.sort(keys); //this is why java is superior to C
        Log.d("TAG", "ordered list from keyset " + keys);

        PathFromUri fromUri = new PathFromUri();
        for (String s: keys) {
            paths.add(fromUri.getPathFromUri(displayMap.get(s),this));
        }
        Log.d("TAG", "paths from multi display " + paths);
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    public void onClickMultiConvert(View v) {
        convert.setVisibility(View.INVISIBLE);
        bar.setVisibility(View.VISIBLE);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Name your converted file");
        final View view = getLayoutInflater().inflate(R.layout.dialog_input_filename, null);
        builder.setView(view);
        final EditText edit = (EditText) view.findViewById(R.id.inputfilename);

        builder.setPositiveButton("Done", ((dialog, which) -> {
            fileName = edit.getText().toString();
            dialog.dismiss();
            if (fileName != null) {
                convertToPdf convert = new convertToPdf(getApplicationContext());
                pdfFilePath = convert.jpgToPdf(null, fileName,0, 0, paths);

                bar.setVisibility(View.INVISIBLE);
                button.setVisibility(View.VISIBLE);
            } else Toast.makeText(this, "Conversion Failed", Toast.LENGTH_SHORT).show();

        }));

        builder.setNegativeButton("Cancel", (dialog, which) -> {
            dialog.dismiss();
        });

        AlertDialog dialog = builder.create();
        dialog.show();

        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.ENGLISH);

        //Added itext jar file because gradle was being annoying
    }

    public void onClickMultiConvertedFilesButton(View v) {
        openPDF open = new openPDF(pdfFilePath);
        open.openPDF(getApplicationContext());
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }

    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        onBackPressed();
        return super.onOptionsItemSelected(item);
    }
}