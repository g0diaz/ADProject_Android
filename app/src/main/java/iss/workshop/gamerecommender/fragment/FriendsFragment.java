package iss.workshop.gamerecommender.fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
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
import iss.workshop.gamerecommender.adapter.FriendProfileFriendsAdapter;
import iss.workshop.gamerecommender.api.RetrofitClient;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FriendsFragment extends Fragment implements AdapterView.OnItemClickListener {

    private List<String> names;
    private List<String> urls;
    private List<Integer> userIds;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        if (getActivity() != null) {
            TextView titleTextView = getActivity().findViewById(R.id.activity_feed_title);
            if (titleTextView != null) {
                titleTextView.setText("Friend");
            }
        }

        // Inflate the layout for this fragment
        View view=inflater.inflate(R.layout.fragment_friends, container, false);
        names = new ArrayList<>();
        urls = new ArrayList<>();
        userIds = new ArrayList<>();

        displayFriends(view);

        return view;
    }

    public void displayFriends(View view){

        SharedPreferences sharedPreferences = requireContext().getSharedPreferences("loginPrefs", Context.MODE_PRIVATE);
        int userId = sharedPreferences.getInt("userId", 0);

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
                        //Using gson library to parse the response
                        String responseBodyString = response.body().string();
                        JsonArray friends = JsonParser.parseString(responseBodyString).getAsJsonArray();

                        for (JsonElement friend : friends) {
                            JsonObject friendObj = friend.getAsJsonObject();
                            String name = friendObj.get("displayName").getAsString();
                            String url = friendObj.get("displayImageUrl").getAsString();
                            int userId = friendObj.get("id").getAsInt();

                            if (url.isEmpty()){
                                urls.add("http://10.0.2.2:8080/image/avatar.jpg");
                            } else {
                                urls.add(url);
                            }
                            names.add(name);
                            userIds.add(userId);
                        }

                        setContent(view);
                    } catch (IOException e) {
                        Log.e("API Error", "Error reading response body", e);
                    }
                } else {
                }

            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText(getContext(), "Cannot fetch the friends: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void setContent(View view){
        FriendProfileFriendsAdapter adapter=new FriendProfileFriendsAdapter(requireContext(), urls, names);

        ListView listView=view.findViewById(R.id.friendlist);
        if(listView!=null){
            listView.setAdapter(adapter);
            listView.setOnItemClickListener(this);
        }
    }

    @Override
    public void onItemClick(AdapterView<?> av, View v, int pos, long id){

        Log.d("ItemClick", "Item clicked: " + pos);

        System.out.println(urls);
        Bundle bundle=new Bundle();
        bundle.putInt("userId", userIds.get(pos));

        ProfileDetailFragment profileDetailFragment = new ProfileDetailFragment();
        profileDetailFragment.setArguments(bundle);

        getParentFragmentManager().beginTransaction()
                .replace(R.id.frame_layout, profileDetailFragment)
                .addToBackStack("friendsFragment")
                .commit();
    }
}