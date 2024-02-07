package iss.workshop.gamerecommender.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.Toast;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.List;

import iss.workshop.gamerecommender.R;
import iss.workshop.gamerecommender.adapter.PrefActivityAdapter;
import iss.workshop.gamerecommender.api.RetrofitAPI;
import iss.workshop.gamerecommender.api.RetrofitClient;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class PrefActivity extends AppCompatActivity{
    private List<String> selected=new ArrayList<>();
    private PrefActivityAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pref);

        request();
        Button conBtn=findViewById(R.id.conBtn);
        conBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveSelectedGenres();
            }
        });
    }

    //to get the data from the sever
    private void request(){
        RetrofitClient retrofitClient=new RetrofitClient();
        Call<JsonElement> call=retrofitClient.getAPI().getGenres();
        call.enqueue(new Callback<JsonElement>() {
            @Override
            public void onResponse(Call<JsonElement> call, Response<JsonElement> response) {
                JsonElement result=response.body();
                JsonArray jsonArray = result.getAsJsonArray();
                List<String> genreList=new ArrayList<>();;
                for (JsonElement element : jsonArray) {
                    genreList.add(element.getAsString());
                }
                setGenre(genreList);
            }
            @Override
            public void onFailure(Call<JsonElement> call, Throwable throwable) {
                System.out.println("Error Connect");
            }
        });
    }

    //to set the adapter of the genrelist
    private void setGenre(List<String> genreList){
        String[] genres=genreList.toArray(new String[0]);
        adapter=new PrefActivityAdapter(this,genres);

        ListView listView=findViewById(R.id.listview);
        if(listView!=null){
            listView.setAdapter(adapter);
        }
    }

    //save selectedgenres
    private void saveSelectedGenres() {
        selected=adapter.getSelectedStrings();

        JsonObject genreData=new JsonObject();
        JsonArray jsonArray = new JsonArray();
        for (String element : selected) {
            jsonArray.add(element.toUpperCase());
        }
        genreData.add("genreData",jsonArray);

        SharedPreferences sharedPreferences = getSharedPreferences("loginPrefs", Context.MODE_PRIVATE);
        int userId = sharedPreferences.getInt("userId",0);

        genreData.addProperty("userId",userId);

        RetrofitClient retrofitClient=new RetrofitClient();
        Call<ResponseBody> call=retrofitClient.getAPI().storeGenres(genreData);

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Intent intent = new Intent(PrefActivity.this, MainActivity.class);
                    startActivity(intent);
                }else{
                    System.out.println("error");
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText(PrefActivity.this, "Save failed: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}