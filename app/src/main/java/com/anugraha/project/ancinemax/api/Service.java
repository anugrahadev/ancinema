package com.anugraha.project.ancinemax.api;

import com.anugraha.project.ancinemax.model.MoviesResponse;
import com.anugraha.project.ancinemax.model.TrailerResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface Service {

    @GET("movie/popular")
    Call<MoviesResponse> getPopularMovis(@Query("api_key") String apiKey);

    @GET("movie/top_rated")
    Call<MoviesResponse> getTop_ratedMovis(@Query("api_key") String apiKey);

    @GET("movie/upcoming")
    Call<MoviesResponse> getUpcoming(@Query("api_key") String apiKey);

    @GET("movie/now_playing")
    Call<MoviesResponse> getNow_playing(@Query("api_key") String apiKey);

    @GET("movie/{movie_id}/videos")
    Call<TrailerResponse> getTrailer(@Path("movie_id") int id, @Query("api_key") String apiKey);

}
