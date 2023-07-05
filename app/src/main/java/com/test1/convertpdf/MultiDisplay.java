package com.test1.convertpdf;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

public class MultiDisplay extends AppCompatActivity {
    private int pics_counter = 0;
    private HashMap<String, Uri> displayMap = new HashMap<>();
    private ProgressBar bar;
    private Button convert;
    private Button button;
    private ViewPager pager;
    private PagerAdapter adapter;
    private ArrayList<String>paths = new ArrayList<String>();
    private ArrayList<String>keys = new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_multi_display);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        pics_counter = getIntent().getIntExtra("Pics Counter", 2);
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

        bar = (ProgressBar) findViewById(R.id.multi_bar);
        bar.setVisibility(View.INVISIBLE);

        convert = (Button) findViewById(R.id.multi_convertButton);
        convert.setOnClickListener(this::onClickMultiConvert);

        button = (Button) findViewById(R.id.multi_convertedFilesButton);
        button.setVisibility(View.INVISIBLE);
        button.setOnClickListener(this::onClickMultiConvertedFilesButton);

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

    }

    public void onClickMultiConvertedFilesButton(View v) {

    }

    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }

    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        onBackPressed();
        return super.onOptionsItemSelected(item);
    }
}