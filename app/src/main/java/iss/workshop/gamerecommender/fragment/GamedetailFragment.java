package iss.workshop.gamerecommender.fragment;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
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
import iss.workshop.gamerecommender.adapter.DevBlogAdapter;
import iss.workshop.gamerecommender.adapter.ImageLoader;
import iss.workshop.gamerecommender.adapter.ReviewPostAdapter;
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
            int gameId = args.getInt("cellId", 0);

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

                        //for game detail
                        JsonObject developer = gameDetail.getAsJsonObject("developer");
                        String developerName = developer.get("displayName").getAsString();
                        int developerId = developer.get("id").getAsInt();
                        String title = gameDetail.get("title").getAsString();
                        String description = gameDetail.get("description").getAsString();
                        String imageUrl = gameDetail.get("imageUrl").getAsString();
                        String webUrl = gameDetail.get("webUrl").getAsString();
                        String releasedDate = gameDetail.get("dateRelease").getAsString();
                        JsonArray platformArray = gameDetail.get("platforms").getAsJsonArray();
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

                        devTextView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Bundle bundle = new Bundle();
                                bundle.putInt("cellId", developerId);

                                DevDetailFragment devDetailFragment =new DevDetailFragment();
                                devDetailFragment.setArguments(bundle);
                                getParentFragmentManager().beginTransaction()
                                        .replace(R.id.frame_layout, devDetailFragment)
                                        .addToBackStack("devDetailFragment")
                                        .commit();
                            }
                        });

                        TextView priceTextView = getView().findViewById(R.id.priceTitle);
                        if (price == 0){
                            priceTextView.setText("Free to Play");
                        } else {
                            priceTextView.setText(String.valueOf(price));
                        }

                        TextView ratingTextView = getView().findViewById(R.id.ratingTitle);
                        ratingTextView.setText(ratingPercentage);

                        TextView urlTextView = getView().findViewById(R.id.weburlTitle);
                        urlTextView.setText(String.valueOf(webUrl));

                        StringBuilder platformStringBuilder = new StringBuilder();
                        for (int i = 0; i < platformArray.size(); i++){
                            String platform = platformArray.get(i).getAsString();
                            platformStringBuilder.append(platform);
                            if (i < platformArray.size() - 1) {
                                platformStringBuilder.append(", ");
                            }
                        }

                        String platformString = platformStringBuilder.toString();
                        TextView platformTextView = getView().findViewById(R.id.platformTitle);
                        platformTextView.setText(platformString);

                        JsonArray genreArray = gameDetail.get("genres").getAsJsonArray();
                        StringBuilder genreStringBuilder = new StringBuilder();
                        for (int i = 0; i < genreArray.size(); i++){
                            String genre = genreArray.get(i).getAsString();
                            genreStringBuilder.append(genre);
                            if (i < genreArray.size() - 1) {
                                genreStringBuilder.append(", ");
                            }
                        }
                        String genreString = genreStringBuilder.toString();
                        TextView genreTextView = getView().findViewById(R.id.genretitle);
                        genreTextView.setText(genreString);

                        //for dev blog posts
                        JsonObject profile = gameDetail.getAsJsonObject("profile");
                        JsonArray gameUpdatePosts = profile.get("gameUpdatePosts").getAsJsonArray();
                        List<String> devTitles = new ArrayList<>();
                        List<String> devMessages = new ArrayList<>();
                        List<String> devDates = new ArrayList<>();
                        for (JsonElement gameUpdatePost : gameUpdatePosts){
                            JsonObject blogObj = gameUpdatePost.getAsJsonObject();
                            String blogTitle = blogObj.get("title").getAsString();
                            String blogMessage = blogObj.get("message").getAsString();
                            String blogDate = blogObj.get("datePosted").getAsString();

                            devTitles.add(blogTitle);
                            devMessages.add(blogMessage);
                            devDates.add(blogDate);
                        }

                        DevBlogAdapter devBlogAdapterdapter = new DevBlogAdapter(requireContext(), devTitles, devMessages, devDates);
                        ListView blogListView = getView().findViewById(R.id.post_list);

                        if (blogListView != null) {
                            blogListView.setAdapter(devBlogAdapterdapter);

                            //to make list view scroll work https://stackoverflow.com/questions/6546108/mapview-inside-a-scrollview/6883831#6883831
                            blogListView.setOnTouchListener(new View.OnTouchListener() {
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
                        }

                        //for review posts
                        JsonArray reviewPosts = profile.get("gameReviewPosts").getAsJsonArray();
                        List<String> reviewTitles = new ArrayList<>();
                        List<String> reviewMessages = new ArrayList<>();
                        List<String> reviewDates = new ArrayList<>();
                        List<Boolean> reviewResults = new ArrayList<>();
                        List<Integer> reviewUserIds = new ArrayList<>();
                        for (JsonElement reviewPost : reviewPosts){
                            JsonObject reviewObj = reviewPost.getAsJsonObject();
                            String reviewTitle = reviewObj.get("title").getAsString();
                            String reviewMessage = reviewObj.get("message").getAsString();
                            String reviewDate = reviewObj.get("datePosted").getAsString();
                            Boolean reviewResult = reviewObj.get("isRecommend").getAsBoolean();
                            int reviewUserId = reviewObj.get("userProfileId").getAsInt();

                            reviewTitles.add(reviewTitle);
                            reviewMessages.add(reviewMessage);
                            reviewDates.add(reviewDate);
                            reviewResults.add(reviewResult);
                            reviewUserIds.add(reviewUserId);
                        }

                        ReviewPostAdapter reviewPostAdapter = new ReviewPostAdapter(requireContext(), reviewTitles, reviewMessages, reviewDates, reviewResults, reviewUserIds);
                        ListView reviewListView = getView().findViewById(R.id.review_list);

                        if (reviewListView != null) {
                            reviewListView.setAdapter(reviewPostAdapter);

                            //to make list view scroll work https://stackoverflow.com/questions/6546108/mapview-inside-a-scrollview/6883831#6883831
                            reviewListView.setOnTouchListener(new View.OnTouchListener() {
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
                        }
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