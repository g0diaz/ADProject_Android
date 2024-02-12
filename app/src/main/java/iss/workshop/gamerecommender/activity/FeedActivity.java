//package iss.workshop.gamerecommender.activity;
//
//import androidx.appcompat.app.AppCompatActivity;
//
//import android.content.Intent;
//import android.os.Bundle;
//import android.view.View;
//import android.widget.Button;
//import android.widget.EditText;
//
//import com.google.gson.JsonObject;
//
//import iss.workshop.gamerecommender.R;
//import iss.workshop.gamerecommender.api.RetrofitClient;
//import okhttp3.ResponseBody;
//import retrofit2.Call;
//import retrofit2.Callback;
//import retrofit2.Response;
//
//public class FeedActivity extends AppCompatActivity {
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_feed);
//
//        EditText editText=findViewById(R.id.feed_text);
//
//        int userId=getIntent().getIntExtra("userId",0);
//        Button btn=findViewById(R.id.post);
//        btn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                String text=editText.getText().toString();
//                storeNewActivity(text,userId);
//                finish();
//            }
//        });
//    }
//    private void storeNewActivity(String text,int userId){
//        JsonObject object=new JsonObject();
//        object.addProperty("text",text);
//        object.addProperty("userId",userId);
//
//        RetrofitClient retrofitClient=new RetrofitClient();
//        Call<ResponseBody> call=retrofitClient
//                .getAPI().storeNewActivity(object);
//
//        call.enqueue(new Callback<ResponseBody>() {
//            @Override
//            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
//
//            }
//
//            @Override
//            public void onFailure(Call<ResponseBody> call, Throwable t) {
//
//            }
//        });
//    }
//}