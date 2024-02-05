package iss.workshop.gamerecommender.api;

import com.google.gson.JsonObject;

import iss.workshop.gamerecommender.dto_models.CreateRequest;
import iss.workshop.gamerecommender.dto_models.LoginRequest;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface RetrofitAPI {
    //Create account
    @POST("api/users/register")
    Call<ResponseBody> createUser(@Body CreateRequest createRequest);

    //Login
    @POST("api/users/login")
    Call<ResponseBody> loginUser(@Body LoginRequest loginRequest);

    //Retrieve Game List
    @GET("api/games/list")
    Call<JsonObject> getGames();
}
