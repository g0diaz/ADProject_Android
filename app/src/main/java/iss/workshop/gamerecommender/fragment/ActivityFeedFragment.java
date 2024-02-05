package iss.workshop.gamerecommender.fragment;

import android.content.Intent;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;

import iss.workshop.gamerecommender.R;
import iss.workshop.gamerecommender.activity.FeedActivity;
import iss.workshop.gamerecommender.adapter.FeedActivityAdapter;

public class ActivityFeedFragment extends Fragment {
    private final String[] feeds = {
           "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Curabitur iaculis congue odio, " +
                   "sed scelerisque mauris mattis sed. Aliquam elementum tempus orci, id dignissim " +
                   "sem volutpat vitae. Etiam sed justo lorem. Pellentesque dignissim augue dapibus, " +
                   "cursus nisi vel, varius mi. Integer ullamcorper, ipsum sagittis ornare gravida, " +
                   "est nulla consequat nunc, at efficitur nunc neque molestie arcu. In enim libero, " +
                   "sagittis sit amet commodo in, blandit nec lectus. Aenean varius dui in ipsum " +
                   "accumsan tristique. Nam et dui quis ipsum varius rutrum id vel justo. Nam in nisi " +
                   "tincidunt, maximus enim eu, varius sem. Proin et aliquam tellus. Mauris porta, " +
                   "mauris id tempus fermentum, tortor lectus blandit nisi, ac condimentum neque elit " +
                   "in augue. Sed vel elementum enim, non sagittis lorem. Etiam vehicula orci in accumsan " +
                   "convallis. Nulla vehicula eu tortor nec volutpat. Sed interdum ullamcorper augue et " +
                   "vehicula. Etiam tempus arcu leo, accumsan sodales magna auctor quis.", "Faze UP", "GGEZ!"
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_activity_feed, container, false);
        ListView listView = view.findViewById(R.id.feedListView);
        Button postFeedBtn = view.findViewById(R.id.post_Feed);

        postFeedBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), FeedActivity.class);
                startActivity(intent);
            }
        });

        if (listView != null) {
            listView.setAdapter(new FeedActivityAdapter(getContext(), feeds));
        }
        return view;
    }
}
