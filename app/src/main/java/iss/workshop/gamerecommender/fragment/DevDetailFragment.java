package iss.workshop.gamerecommender.fragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
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

import androidx.fragment.app.Fragment;

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

public class DevDetailFragment extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view=inflater.inflate(R.layout.fragment_dev_detail, container, false);

        Bundle args=getArguments();
        if(args!=null) {
            int userId = args.getInt("cellId", 0);

            SharedPreferences sharedPreferences = requireContext().getSharedPreferences("loginPrefs", Context.MODE_PRIVATE);
            int myUserId = sharedPreferences.getInt("userId", 0);

            Button followUnfollowButton = view.findViewById(R.id.followUnfollowBtn);

            checkDevAndSetButton(myUserId, userId, followUnfollowButton);

            fetchProfileDetail(userId);
            fetchGameList(userId);
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

                        if (url.isEmpty()){
                            url = "http://10.0.2.2:8080/image/0.png";
                        }

                        TextView bioTextView = getView().findViewById(R.id.bio);
                        bioTextView.setText(bio);

                        TextView textView=getView().findViewById(R.id.dev_name);
                        textView.setText(name);

                        ImageView imageView=getView().findViewById(R.id.dev_avatar);
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

                        ListView gamelistView = getView().findViewById(R.id.dev_gamelist);
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
                                            .addToBackStack("gameFragment")
                                            .commit();
                                }
                            });
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

    private void checkDevAndSetButton(int myUserId, int userId, Button followUnfollowButton) {
        JsonObject userIdData = new JsonObject();
        userIdData.addProperty("userId", myUserId);

        RetrofitClient retrofitClient = new RetrofitClient();
        Call<ResponseBody> call = retrofitClient
                .getAPI()
                .getDevelopersList(userIdData);

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful() && response.body() != null) {
                    try {
                        String responseBodyString = response.body().string();
                        JsonArray devs = JsonParser.parseString(responseBodyString).getAsJsonArray();
                        boolean isFollowed = false;
                        for (JsonElement devElement : devs) {
                            JsonObject devObj = devElement.getAsJsonObject();
                            int followedDevId = devObj.get("id").getAsInt();
                            if (followedDevId == userId) {
                                isFollowed = true;
                                break;
                            }
                        }
                        if (isFollowed){
                            followUnfollowButton.setText("UNFOLLOW");
                        } else {
                            followUnfollowButton.setText("FOLLOW");
                        }
                        devFollowUnfollowButtonSetup(userId, followUnfollowButton, isFollowed);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText(getContext(), "Error checking game follow status: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void devFollowUnfollowButtonSetup(int userId, Button followUnfollowButton, boolean isCurrentlyFollowed) {
        SharedPreferences sharedPreferences = requireContext().getSharedPreferences("loginPrefs", Context.MODE_PRIVATE);
        int myUserId = sharedPreferences.getInt("userId", 0);
        JsonObject userIdData = new JsonObject();
        userIdData.addProperty("userId", myUserId);
        RetrofitClient retrofitClient = new RetrofitClient();

        followUnfollowButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (followUnfollowButton.getText().toString().equalsIgnoreCase("FOLLOW")) {
                    Call<ResponseBody> call = retrofitClient
                            .getAPI()
                            .followDev(userId, userIdData);

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
                    Call<ResponseBody> call = retrofitClient
                            .getAPI()
                            .unfollowDev(userId, userIdData);

                    call.enqueue(new Callback<ResponseBody>() {
                        @Override
                        public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                            if (response.isSuccessful()) {
                                followUnfollowButton.setText(isCurrentlyFollowed ? "FOLLOW" : "UNFOLLOW");
                            } else {
                                Toast.makeText(getContext(), "Failed to update follow status", Toast.LENGTH_SHORT).show();
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
}