package iss.workshop.gamerecommender.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import iss.workshop.gamerecommender.R;

public class FriendProfileFriendsAdapter extends ArrayAdapter<Object>  {
    private final Context context;
    protected String[] names;
    protected String[] avatars;
    public FriendProfileFriendsAdapter(Context context, String[] avatars,String[] names) {
        super(context, R.layout.friendlistcell);

        this.context=context;
        this.avatars=avatars;
        this.names=names;

        addAll(new Object[names.length]);
    }

    public View getView(int pos, View view, @NonNull ViewGroup parent){
        if(view==null){
            LayoutInflater inflater=
                    (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
            view=inflater.inflate(R.layout.friendlistcell,parent,false);
        }


        TextView textView=view.findViewById(R.id.textview);
        textView.setText(names[pos]);

        ImageView imageView=view.findViewById(R.id.imageview);
        int id=context.getResources().getIdentifier(avatars[pos],
                "drawable",context.getPackageName());
        imageView.setImageResource(id);

        return view;
    }
}
