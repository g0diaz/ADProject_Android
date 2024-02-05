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

public class FriendDetailFragment extends Fragment {

    private final String[] names ={"Faker","Brad","Mike","Steve","John"};
    private final String[] avatars ={"avatar","avatar","avatar","avatar","avatar"};

    private final String[] texts={"PalWorld","Conan Exiles","Octopath Traveler","Green Hell","Diablo"};
    private final String[] images={"palworld","conan_exiles","octopath_traveler","green_hell","diablo"};

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view=inflater.inflate(R.layout.fragment_friend_detail, container, false);
        ListView friendlistView = view.findViewById(R.id.friendlist);
        ListView gamelistView = view.findViewById(R.id.gamelist);

        Bundle args=getArguments();
        if(args!=null){
            String name=args.getString("friendName");
            String image=args.getString("avatar");

            TextView textView=view.findViewById(R.id.friend_name);
            textView.setText(name);

            ImageView imageView=view.findViewById(R.id.friend_avatar);
            int id=requireContext().getResources().getIdentifier(image,
                    "drawable",requireContext().getPackageName());
            imageView.setImageResource(id);
        }

        if (friendlistView != null) {
            friendlistView.setAdapter(new FriendProfileFriendsAdapter(requireContext(), avatars, names));
        }
        if (gamelistView != null) {
            gamelistView.setAdapter(new FriendProfileGamesAdapter(requireContext(), images, texts));
        }
        return view;
    }

}

