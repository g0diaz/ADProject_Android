package iss.workshop.gamerecommender.api;

import com.google.gson.JsonObject;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface RetrofitAPI {
    //Create account
    @POST("api/users/register")
    Call<ResponseBody> createUser(@Body JsonObject userData);

    //Login
    @POST("api/users/login")
    Call<ResponseBody> loginUser(@Body JsonObject loginData);

    //Store user preference
    @POST("api/users/genre")
    Call<JsonObject> storeGenres();

    //Retrieve Game List
    @GET("api/games/list")
    Call<JsonObject> getGames();


}
