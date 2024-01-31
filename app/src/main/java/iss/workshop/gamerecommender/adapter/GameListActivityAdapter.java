package iss.workshop.gamerecommender.adapter;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import iss.workshop.gamerecommender.R;

public class GameListActivityAdapter extends ArrayAdapter<Object> {
    private final Context context;
    protected String[] texts;
    protected String[] images;
    public GameListActivityAdapter(Context context, String[] images,String[] texts) {
        super(context, R.layout.gamelistcell);

        this.context=context;
        this.images=images;
        this.texts=texts;

        addAll(new Object[texts.length]);
    }

    public View getView(int pos, View view, @NonNull ViewGroup parent){
        if(view==null){
            LayoutInflater inflater=
                    (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
            view=inflater.inflate(R.layout.prefcell,parent,false);
        }


        TextView textView=view.findViewById(R.id.textview);
        textView.setText(texts[pos]);

        ImageView imageView=view.findViewById(R.id.imageview);
        int id=context.getResources().getIdentifier(images[pos],
                "drawable",context.getPackageName());
        imageView.setImageResource(id);

        return view;
    }
}