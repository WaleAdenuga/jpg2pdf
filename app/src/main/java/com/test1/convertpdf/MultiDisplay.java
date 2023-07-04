package com.test1.convertpdf;

import androidx.appcompat.app.AppCompatActivity;

import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;

public class MultiDisplay extends AppCompatActivity {
    private int pics_counter = 0;
    private HashMap<String, Uri> displayMap = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_multi_display);

        pics_counter = getIntent().getIntExtra("Pics Counter", 1);
        Bundle bundle = getIntent().getBundleExtra("Taken Pics Uri");

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            displayMap = bundle.getSerializable("Uri Map", HashMap.class);
        } else displayMap = (HashMap) bundle.getSerializable("Uri Map");

        Log.d("TAG", "Uri map from multi display "+ displayMap);
    }
}