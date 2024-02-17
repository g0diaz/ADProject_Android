package iss.workshop.gamerecommender.api;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {
//    private static final String BASE_URL = "http://10.0.2.2:8080/";
    public static final String BASE_URL = "http://146.190.82.15:8080/";
    private Retrofit retrofit;
    public RetrofitClient () {
        retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }

    public RetrofitAPI getAPI () {
        return retrofit.create(RetrofitAPI.class);
    }
}
