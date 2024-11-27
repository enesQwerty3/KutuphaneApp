package com.example.kutuphaneproje.api;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Query;

public interface BookApiService {
    @Headers("key:your_apikey")
    @GET("books/v1/volumes")
    Call<Books> getSearchRequest(@Query(value = "q") String query, @Query("startIndex") int startIndex);
}
