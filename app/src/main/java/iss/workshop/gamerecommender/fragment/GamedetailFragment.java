package iss.workshop.gamerecommender.fragment;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.IOException;
import java.lang.reflect.Array;

import iss.workshop.gamerecommender.R;
import iss.workshop.gamerecommender.adapter.ImageLoader;
import iss.workshop.gamerecommender.api.RetrofitClient;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class GamedetailFragment extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view=inflater.inflate(R.layout.fragment_gamedetail, container, false);

        Bundle args=getArguments();
        if(args!=null){
            int gameId = args.getInt("gameId", 0);

            fetchGameDetail(gameId);
        }
        return view;
    }

    private void fetchGameDetail(int gameId) {
        JsonObject gameIdData = new JsonObject();
        gameIdData.addProperty("gameId", gameId);

        //Create a call to server using Retrofit for fetching games
        RetrofitClient retrofitClient = new RetrofitClient();
        Call<ResponseBody> call = retrofitClient
                .getAPI()
                .getGameDetail(gameIdData);

        //Enqueue the call
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful() && response.body() != null) {
                    try {
                        String responseBodyString = response.body().string();
                        JsonObject gameDetail = JsonParser.parseString(responseBodyString).getAsJsonObject();
                        JsonObject developer = gameDetail.getAsJsonObject("developer");
                        String developerName = developer.get("displayName").getAsString();
                        String title = gameDetail.get("title").getAsString();
                        String description = gameDetail.get("description").getAsString();
                        String imageUrl = gameDetail.get("imageUrl").getAsString();
                        String webUrl = gameDetail.get("webUrl").getAsString();
                        String releasedDate = gameDetail.get("dateRelease").getAsString();
                        JsonArray platform = gameDetail.get("platforms").getAsJsonArray();

                        Float price = gameDetail.get("price").getAsFloat();
                        Float rating = gameDetail.get("rating").getAsFloat();
                        rating = rating * 100;
                        String ratingPercentage = String.format("%.2f%%", rating);

                        TextView titleTextView = getView().findViewById(R.id.gamename);
                        titleTextView.setText(title);

                        TextView descTextView=getView().findViewById(R.id.description);
                        descTextView.setText(description);

                        ImageView gameImageView=getView().findViewById(R.id.gameimage);
                        ImageLoader.loadImage(getContext(), imageUrl, gameImageView);

                        TextView dateTextView = getView().findViewById(R.id.releasedDateTitle);
                        dateTextView.setText(releasedDate);

                        TextView devTextView = getView().findViewById(R.id.developertitle);
                        devTextView.setText(developerName);

                        TextView priceTextView = getView().findViewById(R.id.priceTitle);
                        priceTextView.setText(String.valueOf(price));

                        TextView ratingTextView = getView().findViewById(R.id.ratingTitle);
                        ratingTextView.setText(ratingPercentage);

                        TextView urlTextView = getView().findViewById(R.id.weburlTitle);
                        urlTextView.setText(String.valueOf(webUrl));

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
}