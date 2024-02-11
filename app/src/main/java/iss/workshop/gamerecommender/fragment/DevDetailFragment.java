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
        int userId= args.getInt("cellId", -1);

        fetchProfileDetail(userId);
        fetchGameList(userId);

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

                        TextView textView=getView().findViewById(R.id.dev_game);
                        textView.setText("Developed games");
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
                                    bundle.putInt("gameId", gamesIds.get(pos));

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
}