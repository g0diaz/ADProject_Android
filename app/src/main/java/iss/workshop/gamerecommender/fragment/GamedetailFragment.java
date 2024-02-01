package iss.workshop.gamerecommender.fragment;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import iss.workshop.gamerecommender.R;
public class GamedetailFragment extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view=inflater.inflate(R.layout.fragment_gamedetail, container, false);


        Bundle args=getArguments();
        if(args!=null){
            String name=args.getString("gamename");
            String image=args.getString("image");

            TextView textView=view.findViewById(R.id.gamename);
            textView.setText(name);

            ImageView imageView=view.findViewById(R.id.gameimage);
            int id=requireContext().getResources().getIdentifier(image,
                    "drawable",requireContext().getPackageName());
            imageView.setImageResource(id);
        }
        return view;
    }
}