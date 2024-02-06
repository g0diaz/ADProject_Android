package iss.workshop.gamerecommender.adapter;

import android.content.Context;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

//to avoid duplicate glide code
public class ImageLoader {

    public static void loadImage(Context context, String url, ImageView imageView) {
        Glide.with(context)
                .load(url)
                .into(imageView);
    }
}
