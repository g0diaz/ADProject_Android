package iss.workshop.gamerecommender.fragment;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import iss.workshop.gamerecommender.R;
import iss.workshop.gamerecommender.activity.NotiActivity;
import iss.workshop.gamerecommender.activity.ProfileActivity;

public class TitleBarFragment extends Fragment {

    private String title;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Retrieve the title from the fragment's arguments
        if (getArguments() != null) {
            title = getArguments().getString("title", "Default Title");
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_title_bar, container, false);
        ImageButton notiBtn = view.findViewById(R.id.noti_btn);
        ImageButton profileBtn = view.findViewById(R.id.profile_btn);

        // Set the title
        TextView titleTextView = view.findViewById(R.id.activity_feed_title);
        titleTextView.setText(title);

        notiBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), NotiActivity.class);
                startActivity(intent);
            }
        });

        profileBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), ProfileActivity.class);
                startActivity(intent);
            }
        });
        return view;
    }
}
