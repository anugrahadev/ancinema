package com.anugraha.project.ancinemax.api;

import com.anugraha.project.ancinemax.model.MoviesResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface Service {

    @GET("movie/popular")
    Call<MoviesResponse> getPopularMovis(@Query("api_key") String apiKey);

    @GET("movie/top_rated")
    Call<MoviesResponse> getTop_ratedMovis(@Query("api_key") String apiKey);

}
