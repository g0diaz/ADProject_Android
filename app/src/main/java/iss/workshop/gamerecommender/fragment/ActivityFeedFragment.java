package iss.workshop.gamerecommender.fragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import iss.workshop.gamerecommender.R;
//import iss.workshop.gamerecommender.activity.FeedActivity;
import iss.workshop.gamerecommender.adapter.FeedActivityAdapter;
import iss.workshop.gamerecommender.api.RetrofitClient;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ActivityFeedFragment extends Fragment {
    private List<String> images;
    private List<String> contents;
    private List<String> createTimes;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_activity_feed, container, false);
        ListView listView = view.findViewById(R.id.feedListView);
//        Button postFeedBtn = view.findViewById(R.id.post_Feed);

        SharedPreferences sharedPreferences = requireContext().getSharedPreferences("loginPrefs", Context.MODE_PRIVATE);
        int myUserId = sharedPreferences.getInt("userId", 0);

//        postFeedBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent intent = new Intent(getActivity(), FeedActivity.class);
//                intent.putExtra("userId",myUserId);
//                startActivity(intent);
//            }
//        });

        getAllActivity(myUserId,listView);
        return view;
    }

    private void getAllActivity(int myUserId,ListView listView){
        JsonObject userId=new JsonObject();
        userId.addProperty("userId",myUserId);

        RetrofitClient retrofitClient=new RetrofitClient();
        Call<ResponseBody> call=retrofitClient
                .getAPI().getAllActivity(userId);

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful() && response.body() != null){
                    try {
                        String responseBodyString = response.body().string();
                        JsonArray jsonArray = JsonParser.parseString(responseBodyString).getAsJsonArray();

                        images=new ArrayList<>();
                        contents=new ArrayList<>();
                        createTimes=new ArrayList<>();

                        for(JsonElement e:jsonArray){
                            JsonObject jsonObject=e.getAsJsonObject();
                            int parent_id=jsonObject.get("parentId").getAsInt();

                            String content=jsonObject.get("message").getAsString();
                            String targetName=jsonObject.get("targetName").getAsString();
                            String parentName=jsonObject.get("parentName").getAsString();
                            String finalContent=parentName+" "+content+" "+targetName;

                            String time=jsonObject.get("timeCreated").getAsString();
                            DateTimeFormatter originalFormatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
                            LocalDateTime dateTime = LocalDateTime.parse(time, originalFormatter);
                            DateTimeFormatter targetFormatter = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
                            String formattedTime = dateTime.format(targetFormatter);

                            setImage(parent_id);
                            contents.add(finalContent);
                            createTimes.add(formattedTime);
                        }
                        listView.setAdapter(new FeedActivityAdapter(getContext(), images, contents, createTimes));
                    }
                    catch(IOException e){
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {

            }
        });
    }

    private void setImage(int parent_Id){
        JsonObject userIdData = new JsonObject();
        userIdData.addProperty("userId", parent_Id);

        RetrofitClient retrofitClient = new RetrofitClient();
        Call<ResponseBody> call = retrofitClient
                .getAPI()
                .getProfileDetail(userIdData);

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful() && response.body() != null) {
                    try {
                        String responseBodyString = response.body().string();
                        JsonObject profileDetail = JsonParser.parseString(responseBodyString).getAsJsonObject();

                        String url = profileDetail.get("displayImageUrl").getAsString();
                        images.add(url);
                    }
                    catch (IOException e){
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {

            }
        });
    }
}
