package iss.workshop.gamerecommender.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import iss.workshop.gamerecommender.R;
import iss.workshop.gamerecommender.databinding.ActivityMainBinding;
import iss.workshop.gamerecommender.fragment.ActivityFeedFragment;
import iss.workshop.gamerecommender.fragment.FriendDetailFragment;
import iss.workshop.gamerecommender.fragment.FriendsFragment;
import iss.workshop.gamerecommender.fragment.GameFragment;

public class MainActivity extends AppCompatActivity {

    ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        replaceFragment(new GameFragment(), "Game");

        binding.bottomNavigationView.setOnItemSelectedListener(item -> {

            if (item.getItemId() == R.id.game) {
                replaceFragment(new GameFragment(), "Game");
            } else if (item.getItemId() == R.id.activity_feed) {
                replaceFragment(new ActivityFeedFragment(), "Activity Feed");
            } else if (item.getItemId() == R.id.friends) {
                replaceFragment(new FriendsFragment(), "Friends");
            }
            return true;
        });

        ImageButton notiBtn = findViewById(R.id.noti_btn);
        ImageButton profileBtn = findViewById(R.id.profile_btn);

        notiBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, NotiActivity.class);
                startActivity(intent);
            }
        });

        profileBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                replaceFragment(new FriendDetailFragment(), "Profile");

            }
        });
    }

    private void replaceFragment(Fragment fragment, String title) {
        updateTitle(title);
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame_layout, fragment);
        fragmentTransaction.commit();
    }

    private void updateTitle(String title) {
        TextView titleTextView = findViewById(R.id.activity_feed_title);
        titleTextView.setText(title);
    }
}