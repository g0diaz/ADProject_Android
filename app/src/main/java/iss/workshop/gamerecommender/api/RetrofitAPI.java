package iss.workshop.gamerecommender.api;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface RetrofitAPI {
    //Create account
    @POST("api/user/register")
    Call<ResponseBody> createUser(@Body JsonObject userData);

    //Login
    @POST("api/user/login")
    Call<ResponseBody> loginUser(@Body JsonObject loginData);

    //Store user preference
    @POST("api/user/profile/genre")
    Call<ResponseBody> storeGenres(@Body JsonObject genreData);

    @GET("api/user/profile/genreList")
    Call<JsonElement> getGenres();

    //Retrieve Game List
    @GET("api/game/list")
    Call<JsonArray> getAllGames();

    //Retrieve Game Data
    @POST("api/game/detail")
    Call<ResponseBody> getGameDetail(@Body JsonObject gameIdData);

    //Retrieve Game Search Result
    @POST("api/common/search")
    Call<ResponseBody> getSearchResult(@Body JsonObject searchData);

    //Retrieve User Profile
    @POST("api/user/profile/detail")
    Call<ResponseBody> getProfileDetail(@Body JsonObject userIdData);

    //Retrieve User Related Friends
    @POST("api/user/profile/friends")
    Call<ResponseBody> getFriendsList(@Body JsonObject userIdData);

    //Retrieve User Related Developers
    @POST("api/user/profile/developers")
    Call<ResponseBody> getDevelopersList(@Body JsonObject userIdData);

    //Retrieve User Related Games
    @POST("api/user/profile/games")
    Call<ResponseBody> getGamesList(@Body JsonObject userIdData);

    //Update User Profile
    @PUT("api/user/profile/update/{userId}")
    Call<ResponseBody> editProfile(@Path ("userId") int userId,@Body JsonObject userJsonObject);

    //Follow Friend
    @PUT("api/user/profile/user/follow/{friendId}")
    Call<ResponseBody> followFriend(@Path("friendId") int friendId, @Body JsonObject userIdData);

    //Unfollow Friend
    @PUT("api/user/profile/user/unfollow/{friendId}")
    Call<ResponseBody> unfollowFriend(@Path("friendId") int friendId, @Body JsonObject userIdData);

    //Follow Game
    @PUT("api/game/follow/{gameId}")
    Call<ResponseBody> followGame(@Path("gameId") int gameId, @Body JsonObject userIdData);

    //Unfollow Game
    @PUT("api/game/unfollow/{gameId}")
    Call<ResponseBody> unfollowGame(@Path("gameId") int gameId, @Body JsonObject userIdData);

    //Follow Dev
    @PUT("api/user/profile/dev/follow/{devId}")
    Call<ResponseBody> followDev(@Path("devId") int devId, @Body JsonObject userIdData);

    //Unfollow Dev
    @PUT("api/user/profile/dev/unfollow/{devId}")
    Call<ResponseBody> unfollowDev(@Path("devId") int devId, @Body JsonObject userIdData);

    @POST("api/user/activity")
    Call<ResponseBody> getAllActivity(@Body JsonObject userId);

    @POST("api/game/review")
    Call<ResponseBody> reviewGame(@Body JsonObject reviewData);

    @DELETE("api/game/review/delete/{reviewId}")
    Call<ResponseBody> deleteReview(@Path("reviewId") int reviewId);

    @PUT("api/game/review/update/{reviewId}")
    Call<ResponseBody> updateReview(@Path("reviewId") int reviewId, @Body JsonObject reviewData);
}

