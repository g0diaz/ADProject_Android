package iss.workshop.gamerecommender.fragment;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import iss.workshop.gamerecommender.R;
import iss.workshop.gamerecommender.adapter.FeedActivityAdapter;

public class ActivityFeedFragment extends Fragment {

    private final String[] feeds = {
           "GGWP Guys!", "Faze UP", "GGEZ!"
    };

    public ActivityFeedFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View layoutRoot = inflater.inflate(R.layout.fragment_activity_feed, container, false);
        ListView listView = layoutRoot.findViewById(R.id.feedListView);
        if (listView != null) {
            listView.setAdapter(new FeedActivityAdapter(getContext(), feeds));
        }
        return layoutRoot;
    }
}
