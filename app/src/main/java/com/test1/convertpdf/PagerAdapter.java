package com.test1.convertpdf;

import static androidx.core.content.ContextCompat.startActivity;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

public class PagerAdapter extends androidx.viewpager.widget.PagerAdapter {

    private Context context;
    private ArrayList<String> paths = new ArrayList<String>();
    private LayoutInflater inflater;

    public PagerAdapter(Context context, ArrayList<String> paths) {
        this.context = context;
        this.paths = paths;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return paths.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == (object);
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        ImageView image;
        View view = inflater.inflate(R.layout.swipe_image, container, false);
        image = (ImageView) view.findViewById(R.id.multi_swipe_image);
        Picasso.get().load(new File(paths.get(position))).into(image);
        container.addView(view);
        return view;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((ConstraintLayout) object);
    }

    public void removeItem(int position) {
        if (paths.size() > 0) {
            paths.remove(position);
            notifyDataSetChanged();
        } else {
            Toast.makeText(context, "No images detected", Toast.LENGTH_LONG).show(); //fingers crossed toast works
            Intent intent = new Intent(context, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(context, intent, null);
        }
    }


    public ArrayList<String> getPaths() {
        return paths;
    }

    @Override
    public int getItemPosition(@NonNull Object object) {
        return super.getItemPosition(object);
    }


}
