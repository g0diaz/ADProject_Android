package iss.workshop.gamerecommender.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ListView;

import java.util.List;

import iss.workshop.gamerecommender.R;
import iss.workshop.gamerecommender.adapter.NotiActivityAdapter;

public class NotiActivity extends AppCompatActivity {

    private final String[] notifications={"You have 3 recommended games!",
            "Don't share your password to anyone.",
            "You have 3 pending friend requests."};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_noti);

        NotiActivityAdapter adapter = new NotiActivityAdapter(this, notifications);

        ListView listView = findViewById(R.id.noti_list_view);
        if (listView != null){
            listView.setAdapter(adapter);
        }
    }
}