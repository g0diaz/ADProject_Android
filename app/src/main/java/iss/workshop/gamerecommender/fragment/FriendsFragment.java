package iss.workshop.gamerecommender.fragment;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import iss.workshop.gamerecommender.R;

public class FriendsFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_friends, container, false);

        TitleBarFragment titleBarFragment = new TitleBarFragment();
        Bundle arguments = new Bundle();
        arguments.putString("title", "Friends");
        titleBarFragment.setArguments(arguments);

        // Add the TitleBarFragment to the placeholder in this fragment's layout
        getChildFragmentManager().beginTransaction()
                .add(R.id.title_bar_placeholder, titleBarFragment)
                .commit();
        return view;
    }
}