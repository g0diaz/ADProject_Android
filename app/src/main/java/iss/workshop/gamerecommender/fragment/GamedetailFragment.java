package iss.workshop.gamerecommender.fragment;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import iss.workshop.gamerecommender.R;
import iss.workshop.gamerecommender.adapter.ImageLoader;

public class GamedetailFragment extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view=inflater.inflate(R.layout.fragment_gamedetail, container, false);
        Bundle args=getArguments();
        if(args!=null){
            String name=args.getString("title");
            String url=args.getString("url");

            TextView textView=view.findViewById(R.id.gamename);
            textView.setText(name);

            ImageView imageView=view.findViewById(R.id.gameimage);
            ImageLoader.loadImage(getContext(), url, imageView );
        }
        return view;
    }
}