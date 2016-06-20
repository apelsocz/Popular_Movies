package com.pelsoczi.popularmovies.rest;

import com.pelsoczi.popularmovies.models.MoviesResponse;
import com.pelsoczi.popularmovies.models.ReviewsResponse;
import com.pelsoczi.popularmovies.models.VideosResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ApiInterface {
    @GET("popular")
    Call<MoviesResponse> getPopularMovies(@Query("api_key") String apiKey);

    @GET("top_rated")
    Call<MoviesResponse> getTopRatedMovies(@Query("api_key") String apiKey);


    @GET("{id}/reviews")
    Call<ReviewsResponse> getReviews(@Path("id") int id, @Query("api_key") String apiKey);

    @GET("{id}/videos")
    Call<VideosResponse> getVideos(@Path("id") int id, @Query("api_key") String apiKey);
}