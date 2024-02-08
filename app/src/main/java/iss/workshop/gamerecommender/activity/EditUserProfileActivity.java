package iss.workshop.gamerecommender.activity;

import static java.security.AccessController.getContext;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.IOException;

import iss.workshop.gamerecommender.R;
import iss.workshop.gamerecommender.adapter.ImageLoader;
import iss.workshop.gamerecommender.api.RetrofitClient;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EditUserProfileActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_user_profile);

        Intent intent=getIntent();
        int userId=intent.getIntExtra("userId",0);

        fetchProfileDetail(userId);

        Button completBtn=findViewById(R.id.completBtn);

        completBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateUserProfile(userId);
            }
        });
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

                        EditText displayname = findViewById(R.id.displayname);
                        displayname.setText(name);

                        EditText bioText = findViewById(R.id.bio);
                        bioText.setText(bio);

                        ImageView imageView = findViewById(R.id.imageView);
                        ImageLoader.loadImage(EditUserProfileActivity.this, url, imageView);

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText(EditUserProfileActivity.this, "Cannot fetch the details: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void updateUserProfile(int userId){
        EditText displayname=findViewById(R.id.displayname);
        String displayName=displayname.getText().toString();

        EditText imageUrl=findViewById(R.id.imageUrl);
        String imageurl=imageUrl.getText().toString();

        EditText bio=findViewById(R.id.bio);
        String biotext=bio.getText().toString();

        JsonObject userJsonObject=new JsonObject();
        userJsonObject.addProperty("displayname",displayName);
        userJsonObject.addProperty("imageUrl",imageurl);
        userJsonObject.addProperty("bio",biotext);

        RetrofitClient retrofitClient = new RetrofitClient();
        Call<ResponseBody> call = retrofitClient
                .getAPI().editProfile(userId,userJsonObject);

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Intent intent=new Intent(EditUserProfileActivity.this,MainActivity.class);
                    startActivity(intent);
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {

            }
        });
    }
}