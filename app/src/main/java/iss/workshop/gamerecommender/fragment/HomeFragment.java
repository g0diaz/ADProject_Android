package iss.workshop.gamerecommender.fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import iss.workshop.gamerecommender.R;
import iss.workshop.gamerecommender.adapter.FriendProfileGamesAdapter;
import iss.workshop.gamerecommender.api.RetrofitClient;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class HomeFragment extends Fragment {

    private List<String> titles;
    private List<String> urls ;
    private List<Integer> topIds;
    private List<Integer> trendIds;
    private List<Integer> recommendIds;
    private int userId;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        if (getActivity() != null) {
            TextView titleTextView = getActivity().findViewById(R.id.activity_feed_title);
            if (titleTextView != null) {
                titleTextView.setText("Home");
            }
        }

        SharedPreferences sharedPreferences = requireContext().getSharedPreferences("loginPrefs", Context.MODE_PRIVATE);
        userId = sharedPreferences.getInt("userId", 0);

        displayTopGames(view);
        displayTrendingGames(view);
        displayGameRecommend(view);

        return view;
    }

    private void displayTopGames(View view){
        RetrofitClient retrofitClient=new RetrofitClient();
        Call<JsonArray> call=retrofitClient.getAPI().getAllTopGame();
        call.enqueue(new Callback<JsonArray>() {
            @Override
            public void onResponse(Call<JsonArray> call, Response<JsonArray> response) {
                if (isAdded()) {
                    JsonArray result = response.body();

                    titles = new ArrayList<>();
                    urls = new ArrayList<>();
                    topIds = new ArrayList<>();

                    for (JsonElement element : result) {
                        JsonObject gameObj = element.getAsJsonObject();
                        String title = gameObj.get("title").getAsString();
                        String url = gameObj.get("imageUrl").getAsString();
                        int gameId = gameObj.get("id").getAsInt();

                        if (url.isEmpty()) {
                            url = RetrofitClient.BASE_URL + "image/game.png";
                        }

                        urls.add(url);
                        titles.add(title);
                        topIds.add(gameId);

                    }
                    if (topIds.size() == 0) {
                        view.findViewById(R.id.text).setVisibility(View.INVISIBLE);
                        view.findViewById(R.id.topgamelist).setVisibility(View.GONE);
                    } else {
                        setContent(view, "top");
                    }
                }
            }
            @Override
            public void onFailure(Call<JsonArray> call, Throwable throwable) {
                System.out.println("Error Connect");
            }
        });
    }

    private void displayTrendingGames(View view){
        RetrofitClient retrofitClient=new RetrofitClient();
        Call<JsonArray> call=retrofitClient.getAPI().getAllTrendGame();
        call.enqueue(new Callback<JsonArray>() {
            @Override
            public void onResponse(Call<JsonArray> call, Response<JsonArray> response) {
                if (isAdded()) {
                    JsonArray result = response.body();

                    titles = new ArrayList<>();
                    urls = new ArrayList<>();
                    trendIds = new ArrayList<>();

                    for (JsonElement element : result) {
                        JsonObject gameObj = element.getAsJsonObject();
                        String title = gameObj.get("title").getAsString();
                        String url = gameObj.get("imageUrl").getAsString();
                        int gameId = gameObj.get("id").getAsInt();

                        if (url.isEmpty()) {
                            url = RetrofitClient.BASE_URL + "image/game.png";
                        }
                        urls.add(url);
                        titles.add(title);
                        trendIds.add(gameId);

                    }
                    if (trendIds.size() == 0) {
                        view.findViewById(R.id.text2).setVisibility(View.INVISIBLE);
                        view.findViewById(R.id.trendinggamelist).setVisibility(View.GONE);
                    } else {
                        setContent(view, "trend");
                    }
                }
            }
            @Override
            public void onFailure(Call<JsonArray> call, Throwable throwable) {
                System.out.println("Error Connect");
            }
        });
    }

    private void displayGameRecommend(View view){
        JsonObject userIdData=new JsonObject();
        userIdData.addProperty("userId",userId);

        RetrofitClient retrofitClient=new RetrofitClient();
        Call<ResponseBody> call=retrofitClient.getAPI().getAllRecommendGame(userIdData);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if(response.isSuccessful()&&response.body()!=null){
                    if (isAdded()) {
                        try {
                            String responseBodyString = response.body().string();
                            JsonArray result = JsonParser.parseString(responseBodyString).getAsJsonArray();

                            titles = new ArrayList<>();
                            urls = new ArrayList<>();
                            recommendIds = new ArrayList<>();

                            for (JsonElement element : result) {
                                JsonObject gameObj = element.getAsJsonObject();
                                String title = gameObj.get("title").getAsString();
                                String url = gameObj.get("imageUrl").getAsString();
                                int gameId = gameObj.get("id").getAsInt();

                                if (url.isEmpty()) {
                                    url = RetrofitClient.BASE_URL + "image/game.png";
                                }

                                urls.add(url);
                                titles.add(title);
                                recommendIds.add(gameId);
                            }
                            if (recommendIds.size() == 0) {
                                view.findViewById(R.id.text3).setVisibility(View.GONE);
                                view.findViewById(R.id.gamerecommendlist).setVisibility(View.GONE);
                            } else {
                                setContent(view, "recomm");
                            }
                        } catch (IOException e) {
                            System.out.println("Error");
                        }
                    }
                }
            }
            @Override
            public void onFailure(Call<ResponseBody> call, Throwable throwable) {
                System.out.println("Error Connect");
            }
        });
    }

    private void setContent(View view,String name){
        FriendProfileGamesAdapter adapter=new FriendProfileGamesAdapter(requireContext(),urls,titles);
        ListView listView=new ListView(requireContext());
        switch(name){
            case "top":
                listView=view.findViewById(R.id.topgamelist);
                listView.setAdapter(adapter);
                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        Bundle bundle=new Bundle();
                        bundle.putInt("cellId", topIds.get(position));

                        setItemClick(bundle);
                    }
                });
                break;
            case "trend":
                listView=view.findViewById(R.id.trendinggamelist);
                listView.setAdapter(adapter);
                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        Bundle bundle=new Bundle();
                        bundle.putInt("cellId", trendIds.get(position));

                        setItemClick(bundle);
                    }
                });
                break;
            case "recomm":
                listView=view.findViewById(R.id.gamerecommendlist);
                listView.setAdapter(adapter);
                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        Bundle bundle=new Bundle();
                        bundle.putInt("cellId", recommendIds.get(position));

                        setItemClick(bundle);
                    }
                });
                break;
        }
    }

    private void setItemClick(Bundle bundle) {
        GamedetailFragment gamedetailFragment=new GamedetailFragment();
        gamedetailFragment.setArguments(bundle);
        getParentFragmentManager().beginTransaction()
                .replace(R.id.frame_layout,gamedetailFragment)
                .addToBackStack("gameFragment")
                .commit();
    }
}