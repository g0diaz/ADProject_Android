package iss.workshop.gamerecommender.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import iss.workshop.gamerecommender.R;
import iss.workshop.gamerecommender.databinding.ActivityMainBinding;
import iss.workshop.gamerecommender.fragment.ActivityFeedFragment;
import iss.workshop.gamerecommender.fragment.ProfileDetailFragment;
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
        ImageButton logoutBtn = findViewById(R.id.logout_btn);

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
                SharedPreferences sharedPreferences = getSharedPreferences("loginPrefs", Context.MODE_PRIVATE);
                int myUserId = sharedPreferences.getInt("userId", 0);

                Bundle bundle = new Bundle();
                bundle.putInt("userId", myUserId);

                ProfileDetailFragment profileDetailFragment = new ProfileDetailFragment();
                profileDetailFragment.setArguments(bundle);

                replaceFragment(profileDetailFragment, "Profile");
            }
        });

        logoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences pref = getSharedPreferences("loginPrefs", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = pref.edit();

                editor.remove("username");
                editor.remove("sessionId");
                editor.remove("loggedIn");
                editor.remove("userId");
                editor.commit();

                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);

                finish();
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