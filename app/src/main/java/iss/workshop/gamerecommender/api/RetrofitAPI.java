package iss.workshop.gamerecommender.api;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.List;

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
    @POST("api/user/profile/genre")
    Call<ResponseBody> storeGenres(@Body JsonObject genreData);

    @GET("api/user/profile/genreList")
    Call<JsonElement> getGenres();

    //Retrieve Game List
    @GET("api/game/list")
    Call<JsonArray> getAllGames();

    //Retrieve Game Search Result
    @POST("api/common/search")
    Call<ResponseBody> getSearchResult(@Body JsonObject searchData);
}
