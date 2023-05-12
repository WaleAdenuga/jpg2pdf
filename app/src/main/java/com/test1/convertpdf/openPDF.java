package com.test1.convertpdf;

import static androidx.core.content.ContextCompat.startActivity;

import android.content.ActivityNotFoundException;
import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.widget.Toast;

import androidx.core.content.FileProvider;

import java.io.File;

public class openPDF {
    private String filePath;

    public openPDF(String filePath) {
        this.filePath = filePath;
    }

    public void openPDF(Context context) {
        File file = new File(filePath);
        Log.d("TAG", "" + file);

        Uri uri = FileProvider.getUriForFile(context, context.getPackageName() + ".provider", file);
        Log.d("TAG", "" + uri);

        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.setClipData(ClipData.newRawUri("", uri));
        intent.setDataAndType(uri, "application/pdf");
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        try {
            startActivity(context, intent, null);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(context, "No Available PDF APP", Toast.LENGTH_LONG).show();
        }
    }
}
