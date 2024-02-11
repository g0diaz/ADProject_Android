package iss.workshop.gamerecommender.fragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

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

        Bundle args=getArguments();
        if(args!=null){
            int userId = args.getInt("userId", -1);

            if(userId==-1){
                userId=args.getInt("cellId",-2);
            }

            SharedPreferences sharedPreferences = requireContext().getSharedPreferences("loginPrefs", Context.MODE_PRIVATE);
            int myUserId = sharedPreferences.getInt("userId", 0);

            if (userId == myUserId){
                Button editProfileButton = view.findViewById(R.id.editProfileBtn);
                Button unfollowButton = view.findViewById(R.id.unfollowBtn);
                editProfileButton.setVisibility(View.VISIBLE);
                unfollowButton.setVisibility(View.GONE);

                editProfileButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent=new Intent(getActivity(), EditUserProfileActivity.class);
                        intent.putExtra("userId",myUserId);
                        startActivity(intent);
                    }
                });
            }

            fetchProfileDetail(userId);
            fetchGameList(userId);
            fetchFriendList(userId);
            fetchDevelopersList(userId);
        }
        return view;
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
                        String bio = profileDetail.get("biography").getAsString();
                        String name = profileDetail.get("displayName").getAsString();
                        String url = profileDetail.get("displayImageUrl").getAsString();

                        TextView bioTextView = getView().findViewById(R.id.bio);
                        bioTextView.setText(bio);

                        TextView textView=getView().findViewById(R.id.friend_name);
                        textView.setText(name);

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

                                titles.add(title);
                                urls.add(url);
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
                                        bundle.putInt("gameId", gamesIds.get(pos));

                                        GamedetailFragment gameDetailFragment = new GamedetailFragment();
                                        gameDetailFragment.setArguments(bundle);
                                        getParentFragmentManager().beginTransaction()
                                                .replace(R.id.frame_layout,gameDetailFragment)
                                                .addToBackStack("gameFragment")
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
                            List<Integer> userIds = new ArrayList<>();

                            for (JsonElement friend : friends) {
                                JsonObject friendObj = friend.getAsJsonObject();
                                String name = friendObj.get("displayName").getAsString();
                                String url = friendObj.get("displayImageUrl").getAsString();
                                int userId = friendObj.get("id").getAsInt();

                                names.add(name);
                                urls.add(url);
                                userIds.add(userId);
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
                                        bundle.putInt("userId", userIds.get(pos));

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
                            List<Integer> userIds = new ArrayList<>();

                            for (JsonElement developer : developers) {
                                JsonObject developerObj = developer.getAsJsonObject();
                                String name = developerObj.get("displayName").getAsString();
                                int userId = developerObj.get("id").getAsInt();

                                names.add(name);
                                userIds.add(userId);
                            }

                            FriendProfileDevelopersAdapter adapter = new FriendProfileDevelopersAdapter(requireContext(), names);

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
                                        bundle.putInt("userId", userIds.get(pos));

                                        ProfileDetailFragment profileDetailFragment = new ProfileDetailFragment();
                                        profileDetailFragment.setArguments(bundle);

                                        getParentFragmentManager().beginTransaction()
                                                .replace(R.id.frame_layout, profileDetailFragment)
                                                .addToBackStack("friendsFragment")
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
}