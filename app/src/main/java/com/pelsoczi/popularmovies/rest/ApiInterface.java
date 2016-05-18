package com.pelsoczi.popularmovies.rest;

import com.pelsoczi.popularmovies.models.MoviesResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface ApiInterface {
    @GET("popular")
    Call<MoviesResponse> getPopularMovies(@Query("api_key") String apiKey);

    @GET("top_rated")
    Call<MoviesResponse> getTopRatedMovies(@Query("api_key") String apiKey);
}