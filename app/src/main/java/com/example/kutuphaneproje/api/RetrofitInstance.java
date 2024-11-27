package com.example.kutuphaneproje.api;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitInstance {
    static Retrofit retrofit = null;
    static String BASE_URL = "https://www.googleapis.com/";

    public static BookApiService getService() {
        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit.create(BookApiService.class);
    }
}
