package iss.workshop.gamerecommender.fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.List;

import iss.workshop.gamerecommender.R;
import iss.workshop.gamerecommender.adapter.FriendProfileGamesAdapter;
import iss.workshop.gamerecommender.adapter.PrefActivityAdapter;
import iss.workshop.gamerecommender.api.RetrofitClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class HomeFragment extends Fragment {

    private List<String> titles;
    private List<String> urls ;
    private List<Integer> cellIds;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        SharedPreferences sharedPreferences = requireContext().getSharedPreferences("loginPrefs", Context.MODE_PRIVATE);
        int userId = sharedPreferences.getInt("userId", 0);

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
                JsonArray result=response.body();

                titles= new ArrayList<>();
                urls= new ArrayList<>();
                cellIds = new ArrayList<>();

                for (JsonElement element : result) {
                    JsonObject gameObj = element.getAsJsonObject();
                    String title = gameObj.get("title").getAsString();
                    String url = gameObj.get("imageUrl").getAsString();
                    int gameId = gameObj.get("id").getAsInt();

                    titles.add(title);
                    urls.add(url);
                    cellIds.add(gameId);
                }

                setContent(view);
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
                JsonArray result=response.body();

                titles= new ArrayList<>();
                urls= new ArrayList<>();
                cellIds = new ArrayList<>();

                for (JsonElement element : result) {
                    JsonObject gameObj = element.getAsJsonObject();
                    String title = gameObj.get("title").getAsString();
                    String url = gameObj.get("imageUrl").getAsString();
                    int gameId = gameObj.get("id").getAsInt();

                    titles.add(title);
                    urls.add(url);
                    cellIds.add(gameId);
                }

                setContent(view);
            }
            @Override
            public void onFailure(Call<JsonArray> call, Throwable throwable) {
                System.out.println("Error Connect");
            }
        });
    }

    private void displayGameRecommend(View view){
        RetrofitClient retrofitClient=new RetrofitClient();
        Call<JsonArray> call=retrofitClient.getAPI().getAllTopGame();
        call.enqueue(new Callback<JsonArray>() {
            @Override
            public void onResponse(Call<JsonArray> call, Response<JsonArray> response) {
                JsonArray result=response.body();

                titles= new ArrayList<>();
                urls= new ArrayList<>();
                cellIds = new ArrayList<>();

                for (JsonElement element : result) {
                    JsonObject gameObj = element.getAsJsonObject();
                    String title = gameObj.get("title").getAsString();
                    String url = gameObj.get("imageUrl").getAsString();
                    int gameId = gameObj.get("id").getAsInt();

                    titles.add(title);
                    urls.add(url);
                    cellIds.add(gameId);
                }

                setContent(view);
            }
            @Override
            public void onFailure(Call<JsonArray> call, Throwable throwable) {
                System.out.println("Error Connect");
            }
        });
    }

    private void setContent(View view){
        FriendProfileGamesAdapter adapter=new FriendProfileGamesAdapter(requireContext(),urls,titles);

        ListView listView=view.findViewById(R.id.gamelist);
        if(listView!=null){
            listView.setAdapter(adapter);
//            listView.setOnItemClickListener(this);
        }
    }
}