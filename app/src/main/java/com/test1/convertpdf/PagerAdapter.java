package com.test1.convertpdf;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.ArrayList;

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
        return view == ((ConstraintLayout) object);
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
}