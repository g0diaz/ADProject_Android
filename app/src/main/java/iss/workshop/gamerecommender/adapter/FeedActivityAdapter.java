package iss.workshop.gamerecommender.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import iss.workshop.gamerecommender.R;
import iss.workshop.gamerecommender.api.RetrofitClient;
import iss.workshop.gamerecommender.fragment.GamedetailFragment;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FeedActivityAdapter extends ArrayAdapter<Object> {
    private final Context context;
    private List<String> images;
    protected List<String> contents;
    protected  List<String> times;

    public FeedActivityAdapter(Context context, List<String> images,List<String> contents,List<String> times) {
        super(context, R.layout.feedcell);
        this.context = context;
        this.images=images;
        this.contents = contents;
        this.times=times;

        addAll(new Object[contents.size()]);
    }

    @androidx.annotation.NonNull
    public View getView(int pos, View view, @NonNull ViewGroup parent) {
        if (view == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(
                    Activity.LAYOUT_INFLATER_SERVICE);

            view = inflater.inflate(R.layout.feedcell, parent, false);
        }

        TextView textView = view.findViewById(R.id.feed_desc);
        textView.setText(contents.get(pos));

        ImageView imageView=view.findViewById(R.id.feed_photo);
        String url = images.get(pos);
        ImageLoader.loadImage(context, url, imageView);

        TextView timeView=view.findViewById(R.id.time);
        timeView.setText(times.get(pos));

        return view;
    }

}