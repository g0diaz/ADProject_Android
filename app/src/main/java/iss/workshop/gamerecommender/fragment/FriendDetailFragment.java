package iss.workshop.gamerecommender.fragment;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.List;

import iss.workshop.gamerecommender.R;
import iss.workshop.gamerecommender.adapter.FriendProfileFriendsAdapter;
import iss.workshop.gamerecommender.adapter.FriendProfileGamesAdapter;
import iss.workshop.gamerecommender.adapter.ImageLoader;

public class FriendDetailFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view=inflater.inflate(R.layout.fragment_friend_detail, container, false);
//        ListView friendlistView = view.findViewById(R.id.friendlist);
//        ListView gamelistView = view.findViewById(R.id.gamelist);

        Bundle args=getArguments();
        if(args!=null){
            String name=args.getString("friendName");
            String url=args.getString("url");

            TextView textView=view.findViewById(R.id.friend_name);
            textView.setText(name);

            ImageView imageView=view.findViewById(R.id.friend_avatar);
            ImageLoader.loadImage(getContext(), url, imageView);
        }

//        if (friendlistView != null) {
//            friendlistView.setAdapter(new FriendProfileFriendsAdapter(requireContext(), url, name));
//        }
//        if (gamelistView != null) {
//            gamelistView.setAdapter(new FriendProfileGamesAdapter(requireContext(), url, name));
//        }
        return view;
    }

}

