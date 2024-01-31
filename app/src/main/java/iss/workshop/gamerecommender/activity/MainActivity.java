package iss.workshop.gamerecommender.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;

import iss.workshop.gamerecommender.R;
import iss.workshop.gamerecommender.databinding.ActivityMainBinding;
import iss.workshop.gamerecommender.fragment.ActivityFeedFragment;
import iss.workshop.gamerecommender.fragment.FriendsFragment;
import iss.workshop.gamerecommender.fragment.GameFragment;

public class MainActivity extends AppCompatActivity {

    ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        replaceFragment(new GameFragment());

        binding.bottomNavigationView.setOnItemSelectedListener(item -> {

            if (item.getItemId() == R.id.game) {
                replaceFragment(new GameFragment());
            } else if (item.getItemId() == R.id.activity_feed) {
                replaceFragment(new ActivityFeedFragment());
            } else if (item.getItemId() == R.id.friends) {
                replaceFragment(new FriendsFragment());
            }
            return true;
        });
    }

    private void replaceFragment(Fragment fragment){

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame_layout, fragment);
        fragmentTransaction.commit();
    }
}