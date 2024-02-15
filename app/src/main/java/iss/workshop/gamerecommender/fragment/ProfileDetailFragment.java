package iss.workshop.gamerecommender.fragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.fragment.app.Fragment;

import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;


import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import iss.workshop.gamerecommender.R;
import iss.workshop.gamerecommender.activity.EditUserProfileActivity;
import iss.workshop.gamerecommender.activity.MainActivity;
import iss.workshop.gamerecommender.adapter.FriendProfileDevelopersAdapter;
import iss.workshop.gamerecommender.adapter.FriendProfileFriendsAdapter;
import iss.workshop.gamerecommender.adapter.FriendProfileGamesAdapter;
import iss.workshop.gamerecommender.adapter.ImageLoader;
import iss.workshop.gamerecommender.api.RetrofitClient;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfileDetailFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view=inflater.inflate(R.layout.fragment_profile_detail, container, false);

        if (getActivity() != null) {
            TextView titleTextView = getActivity().findViewById(R.id.activity_feed_title);
            if (titleTextView != null) {
                titleTextView.setText("Profile");
            }
        }

        Bundle args=getArguments();
        if(args!=null){
            int viewedUserId = args.getInt("userId", -1);

            if(viewedUserId==-1){
                viewedUserId=args.getInt("cellId",-2);
            }

            SharedPreferences sharedPreferences = requireContext().getSharedPreferences("loginPrefs", Context.MODE_PRIVATE);
            int myUserId = sharedPreferences.getInt("userId", 0);

            if (viewedUserId == myUserId){
                Button editProfileButton = view.findViewById(R.id.editProfileBtn);
                Button followUnfollowButton = view.findViewById(R.id.followUnfollowBtn);
                editProfileButton.setVisibility(View.VISIBLE);
                followUnfollowButton.setVisibility(View.GONE);

                editProfileButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent=new Intent(getActivity(), EditUserProfileActivity.class);
                        intent.putExtra("userId",myUserId);
                        startActivity(intent);
                    }
                });
            }
            else {
                Button followUnfollowButton = view.findViewById(R.id.followUnfollowBtn);
                followUnfollowButton.setVisibility(View.VISIBLE);
                checkFriendAndSetButton(myUserId, viewedUserId, followUnfollowButton);
            }

            fetchProfileDetail(viewedUserId);
            fetchGameList(viewedUserId);
            fetchFriendList(viewedUserId);
            fetchDevelopersList(viewedUserId);

        }
        return view;
    }

    private void friendFollowUnfollowButtonSetup(int viewedUserId, Button followUnfollowButton, int myUserId) {
        followUnfollowButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (followUnfollowButton.getText().toString().equalsIgnoreCase("FOLLOW")) {
                    JsonObject userIdData = new JsonObject();
                    userIdData.addProperty("userId", myUserId);

                    RetrofitClient retrofitClient = new RetrofitClient();
                    Call<ResponseBody> call = retrofitClient
                            .getAPI()
                            .followFriend(viewedUserId, userIdData);

                    call.enqueue(new Callback<ResponseBody>() {
                        @Override
                        public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                            if (response.isSuccessful()) {
                                Toast.makeText(getContext(), "Successfully followed", Toast.LENGTH_SHORT).show();
                                followUnfollowButton.setText("UNFOLLOW");
                            } else {
                                Toast.makeText(getContext(), "Failed to follow", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onFailure(Call<ResponseBody> call, Throwable t) {
                            Toast.makeText(getContext(), "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    JsonObject userIdData = new JsonObject();
                    userIdData.addProperty("userId", myUserId);

                    RetrofitClient retrofitClient = new RetrofitClient();
                    Call<ResponseBody> call = retrofitClient
                            .getAPI()
                            .unfollowFriend(viewedUserId, userIdData);

                    call.enqueue(new Callback<ResponseBody>() {
                        @Override
                        public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                            if (response.isSuccessful()) {
                                Toast.makeText(getContext(), "Successfully unfollowed", Toast.LENGTH_SHORT).show();
                                followUnfollowButton.setText("FOLLOW");
                            } else {
                                Toast.makeText(getContext(), "Failed to unfollow", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onFailure(Call<ResponseBody> call, Throwable t) {
                            Toast.makeText(getContext(), "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });
    }
    private void checkFriendAndSetButton(int myUserId, int viewedUserId, Button followUnfollowButton) {
        JsonObject userIdData = new JsonObject();
        userIdData.addProperty("userId", myUserId);

        RetrofitClient retrofitClient = new RetrofitClient();
        Call<ResponseBody> call = retrofitClient
                .getAPI()
                .getFriendsList(userIdData);

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful() && response.body() != null) {
                    try {
                        String responseBodyString = response.body().string();
                        JsonArray friends = JsonParser.parseString(responseBodyString).getAsJsonArray();
                        boolean isFriend = false;
                        for (JsonElement friend : friends) {
                            JsonObject friendObj = friend.getAsJsonObject();
                            int friendId = friendObj.get("id").getAsInt();
                            if (friendId == viewedUserId) {
                                isFriend = true;
                                break;
                            }
                        }
                        if (isFriend){
                            followUnfollowButton.setText("UNFOLLOW");
                        } else {
                            followUnfollowButton.setText("FOLLOW");
                        }
                        friendFollowUnfollowButtonSetup(viewedUserId, followUnfollowButton, myUserId);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText(getContext(), "Cannot fetch friend list: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void fetchProfileDetail(int userId) {
        JsonObject userIdData = new JsonObject();
        userIdData.addProperty("userId", userId);

        //Create a call to server using Retrofit for fetching games
        RetrofitClient retrofitClient = new RetrofitClient();
        Call<ResponseBody> call = retrofitClient
                .getAPI()
                .getProfileDetail(userIdData);

        //Enqueue the call
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful() && response.body() != null) {
                    try {
                        String responseBodyString = response.body().string();
                        JsonObject profileDetail = JsonParser.parseString(responseBodyString).getAsJsonObject();
                        JsonObject dateDetail = profileDetail.getAsJsonObject("profile");
                        String bio = profileDetail.get("biography").getAsString();
                        String name = profileDetail.get("displayName").getAsString();
                        String url = profileDetail.get("displayImageUrl").getAsString();
                        String date = dateDetail.get("dateCreated").getAsString();

                        if (url.isEmpty()){
                            url = "http://10.0.2.2:8080/image/user.png";
                        }

                        TextView bioTextView = getView().findViewById(R.id.bio);
                        bioTextView.setText(bio);

                        TextView textView = getView().findViewById(R.id.friend_name);
                        textView.setText(name);

                        TextView dateTextView = getView().findViewById(R.id.date);
                        dateTextView.setText(date);

                        ImageView imageView=getView().findViewById(R.id.friend_avatar);
                        ImageLoader.loadImage(getContext(), url, imageView);

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText(getContext(), "Cannot fetch the details: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void fetchGameList(int userId) {
        JsonObject userIdData = new JsonObject();
        userIdData.addProperty("userId", userId);

        //Create a call to server using Retrofit for fetching games
        RetrofitClient retrofitClient = new RetrofitClient();
        Call<ResponseBody> call = retrofitClient
                .getAPI()
                .getGamesList(userIdData);

        //Enqueue the call
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful() && response.body() != null) {
                    try {
                        String responseBodyString = response.body().string();
                        JsonArray games = JsonParser.parseString(responseBodyString).getAsJsonArray();
                        if (games.size() == 0){
                            TextView noGamesTextView = getView().findViewById(R.id.no_games_textview);
                            if (noGamesTextView != null){
                                noGamesTextView.setVisibility(View.VISIBLE);
                            }
                            ListView gamelistView = getView().findViewById(R.id.gamelist);
                            if (gamelistView != null) {
                                gamelistView.setVisibility(View.GONE);
                            }
                        } else {
                            List<String> titles = new ArrayList<>();
                            List<String> urls = new ArrayList<>();
                            List<Integer> gamesIds = new ArrayList<>();
                            for (JsonElement game : games) {
                                JsonObject gameObj = game.getAsJsonObject();
                                String title = gameObj.get("title").getAsString();
                                String url = gameObj.get("imageUrl").getAsString();
                                int gameId = gameObj.get("id").getAsInt();

                                if (url.isEmpty()){
                                    url = "http://10.0.2.2:8080/image/game.png";
                                }

                                urls.add(url);
                                titles.add(title);
                                gamesIds.add(gameId);
                            }

                            FriendProfileGamesAdapter adapter = new FriendProfileGamesAdapter(requireContext(), urls, titles);

                            ListView gamelistView = getView().findViewById(R.id.gamelist);
                            if (gamelistView != null) {
                                gamelistView.setAdapter(adapter);
                                gamelistView.setOnTouchListener(new View.OnTouchListener() {
                                    @Override
                                    public boolean onTouch(View view, MotionEvent motionEvent) {
                                        int action = motionEvent.getAction();
                                        switch (action) {
                                            case MotionEvent.ACTION_DOWN:
                                                // Disallow ScrollView to intercept touch events.
                                                view.getParent().requestDisallowInterceptTouchEvent(true);
                                                break;
                                            case MotionEvent.ACTION_UP:
                                                // Allow ScrollView to intercept touch events.
                                                view.getParent().requestDisallowInterceptTouchEvent(false);
                                                break;
                                        }
                                        view.onTouchEvent(motionEvent);
                                        return true;
                                    }
                                });
                                gamelistView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                    @Override
                                    public void onItemClick(AdapterView<?> av, View view, int pos, long id) {

                                        Bundle bundle=new Bundle();
                                        bundle.putInt("cellId", gamesIds.get(pos));

                                        GamedetailFragment gameDetailFragment = new GamedetailFragment();
                                        gameDetailFragment.setArguments(bundle);
                                        getParentFragmentManager().beginTransaction()
                                                .replace(R.id.frame_layout,gameDetailFragment)
                                                .addToBackStack("gameDetailFragment")
                                                .commit();
                                    }
                                });
                                TextView noGamesTextView = getView().findViewById(R.id.no_games_textview);
                                if (noGamesTextView != null) {
                                    noGamesTextView.setVisibility(View.GONE);
                                }
                            }
                        }
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
            @Override
            public void onFailure (Call < ResponseBody > call, Throwable t){
                Toast.makeText(getContext(), "Cannot fetch game list: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void fetchFriendList(int userId) {
        JsonObject userIdData = new JsonObject();
        userIdData.addProperty("userId", userId);

        //Create a call to server using Retrofit for fetching games
        RetrofitClient retrofitClient = new RetrofitClient();
        Call<ResponseBody> call = retrofitClient
                .getAPI()
                .getFriendsList(userIdData);

        //Enqueue the call
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful() && response.body() != null) {
                    try {
                        String responseBodyString = response.body().string();
                        JsonArray friends = JsonParser.parseString(responseBodyString).getAsJsonArray();
                        if (friends.size() == 0){
                            TextView noFriendsTextView = getView().findViewById(R.id.no_friends_textview);
                            if (noFriendsTextView != null){
                                noFriendsTextView.setVisibility(View.VISIBLE);
                            }
                            ListView friendlistView = getView().findViewById(R.id.friendlist);
                            if (friendlistView != null) {
                                friendlistView.setVisibility(View.GONE);
                            }
                        } else {
                            List<String> names = new ArrayList<>();
                            List<String> urls = new ArrayList<>();
                            List<Integer> friendIds = new ArrayList<>();

                            for (JsonElement friend : friends) {
                                JsonObject friendObj = friend.getAsJsonObject();
                                String name = friendObj.get("displayName").getAsString();
                                String url = friendObj.get("displayImageUrl").getAsString();
                                int friendId = friendObj.get("id").getAsInt();

                                if (url.isEmpty()){
                                    url = "http://10.0.2.2:8080/image/user.png";
                                }

                                urls.add(url);
                                names.add(name);
                                friendIds.add(friendId);
                            }
                            SharedPreferences sharedPreferences = requireContext().getSharedPreferences("loginPrefs", Context.MODE_PRIVATE);
                            int myUserId = sharedPreferences.getInt("userId", 0);

                            boolean isFriend = friendIds.contains(myUserId);
                            if (!isFriend) {
                                Button unfollowButton = getView().findViewById(R.id.followUnfollowBtn);
                                unfollowButton.setText("FOLLOW");
                            }
                            FriendProfileFriendsAdapter adapter = new FriendProfileFriendsAdapter(requireContext(), urls, names);

                            ListView friendlistView = getView().findViewById(R.id.friendlist);
                            if (friendlistView != null) {
                                friendlistView.setAdapter(adapter);
                                friendlistView.setOnTouchListener(new View.OnTouchListener() {
                                    @Override
                                    public boolean onTouch(View view, MotionEvent motionEvent) {
                                        int action = motionEvent.getAction();
                                        switch (action) {
                                            case MotionEvent.ACTION_DOWN:
                                                // Disallow ScrollView to intercept touch events.
                                                view.getParent().requestDisallowInterceptTouchEvent(true);
                                                break;
                                            case MotionEvent.ACTION_UP:
                                                // Allow ScrollView to intercept touch events.
                                                view.getParent().requestDisallowInterceptTouchEvent(false);
                                                break;
                                        }
                                        view.onTouchEvent(motionEvent);
                                        return true;
                                    }
                                });
                                friendlistView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                    @Override
                                    public void onItemClick(AdapterView<?> av, View view, int pos, long id) {

                                        Bundle bundle=new Bundle();
                                        bundle.putInt("userId", friendIds.get(pos));

                                        ProfileDetailFragment profileDetailFragment = new ProfileDetailFragment();
                                        profileDetailFragment.setArguments(bundle);

                                        getParentFragmentManager().beginTransaction()
                                                .replace(R.id.frame_layout, profileDetailFragment)
                                                .addToBackStack("friendsFragment")
                                                .commit();
                                    }
                                });
                            }
                            TextView noFriendsTextView = getView().findViewById(R.id.no_friends_textview);
                            if (noFriendsTextView != null){
                                noFriendsTextView.setVisibility(View.GONE);
                            }
                        }
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
            @Override
            public void onFailure (Call < ResponseBody > call, Throwable t){
                Toast.makeText(getContext(), "Cannot fetch friend list: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void fetchDevelopersList(int userId) {
        JsonObject userIdData = new JsonObject();
        userIdData.addProperty("userId", userId);

        //Create a call to server using Retrofit for fetching games
        RetrofitClient retrofitClient = new RetrofitClient();
        Call<ResponseBody> call = retrofitClient
                .getAPI()
                .getDevelopersList(userIdData);

        //Enqueue the call
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful() && response.body() != null) {
                    try {
                        String responseBodyString = response.body().string();
                        JsonArray developers = JsonParser.parseString(responseBodyString).getAsJsonArray();
                        if (developers.size() == 0){
                            TextView noDevelopersTextView = getView().findViewById(R.id.no_developers_textview);
                            if (noDevelopersTextView != null){
                                noDevelopersTextView.setVisibility(View.VISIBLE);
                            }
                            ListView developerlistView = getView().findViewById(R.id.developerslist);
                            if (developerlistView != null) {
                                developerlistView.setVisibility(View.GONE);
                            }
                        } else {
                            List<String> names = new ArrayList<>();
                            List<Integer> cellIds = new ArrayList<>();
                            List<String> urls = new ArrayList<>();

                            for (JsonElement developer : developers) {
                                JsonObject developerObj = developer.getAsJsonObject();
                                String name = developerObj.get("displayName").getAsString();
                                int userId = developerObj.get("id").getAsInt();
                                String url = developerObj.get("displayImageUrl").getAsString();

                                if (url.isEmpty()){
                                    urls.add("http://10.0.2.2:8080/image/user.png");
                                } else {
                                    urls.add(url);
                                }
                                names.add(name);
                                cellIds.add(userId);

                            }

                            FriendProfileDevelopersAdapter adapter = new FriendProfileDevelopersAdapter(requireContext(), urls, names);

                            ListView developerlistView = getView().findViewById(R.id.developerslist);
                            if (developerlistView != null) {
                                developerlistView.setAdapter(adapter);
                                developerlistView.setOnTouchListener(new View.OnTouchListener() {
                                    @Override
                                    public boolean onTouch(View view, MotionEvent motionEvent) {
                                        int action = motionEvent.getAction();
                                        switch (action) {
                                            case MotionEvent.ACTION_DOWN:
                                                // Disallow ScrollView to intercept touch events.
                                                view.getParent().requestDisallowInterceptTouchEvent(true);
                                                break;
                                            case MotionEvent.ACTION_UP:
                                                // Allow ScrollView to intercept touch events.
                                                view.getParent().requestDisallowInterceptTouchEvent(false);
                                                break;
                                        }
                                        view.onTouchEvent(motionEvent);
                                        return true;
                                    }
                                });
                                developerlistView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                    @Override
                                    public void onItemClick(AdapterView<?> av, View view, int pos, long id) {

                                        Bundle bundle=new Bundle();
                                        bundle.putInt("cellId", cellIds.get(pos));

                                        DevDetailFragment devDetailFragment = new DevDetailFragment();
                                        devDetailFragment.setArguments(bundle);

                                        getParentFragmentManager().beginTransaction()
                                                .replace(R.id.frame_layout, devDetailFragment)
                                                .addToBackStack("devDetailFragment")
                                                .commit();
                                    }
                                });
                            }
                            TextView noDevelopersTextView = getView().findViewById(R.id.no_developers_textview);
                            if (noDevelopersTextView != null){
                                noDevelopersTextView.setVisibility(View.GONE);
                            }
                        }
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
            @Override
            public void onFailure (Call < ResponseBody > call, Throwable t){
                Toast.makeText(getContext(), "Cannot fetch friend list: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        Bundle args = getArguments();
        if (args != null) {
            int viewedUserId = args.getInt("userId", -1);

            if (viewedUserId == -1) {
                viewedUserId = args.getInt("cellId", -2);
            }

            SharedPreferences sharedPreferences = requireContext().getSharedPreferences("loginPrefs", Context.MODE_PRIVATE);
            int myUserId = sharedPreferences.getInt("userId", 0);

            if (viewedUserId == myUserId) {
                Button editProfileButton = getView().findViewById(R.id.editProfileBtn);
                Button followUnfollowButton = getView().findViewById(R.id.followUnfollowBtn);
                editProfileButton.setVisibility(View.VISIBLE);
                followUnfollowButton.setVisibility(View.GONE);

                editProfileButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(getActivity(), EditUserProfileActivity.class);
                        intent.putExtra("userId", myUserId);
                        startActivity(intent);
                    }
                });
            } else {
                Button followUnfollowButton = getView().findViewById(R.id.followUnfollowBtn);
                followUnfollowButton.setVisibility(View.VISIBLE);
                checkFriendAndSetButton(myUserId, viewedUserId, followUnfollowButton);
            }

            fetchProfileDetail(viewedUserId);
            fetchGameList(viewedUserId);
            fetchFriendList(viewedUserId);
            fetchDevelopersList(viewedUserId);
        }
    }


}