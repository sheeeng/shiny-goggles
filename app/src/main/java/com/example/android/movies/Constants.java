package com.example.android.movies;

public class Constants {

    // No need to use "static final" attributes because this is an interface an not a class.
    public interface URLS {
        String TMDB_IMAGE_SIZE_W92 = "http://image.tmdb.org/t/p/w92/";
        String TMDB_IMAGE_SIZE_W154 = "http://image.tmdb.org/t/p/w154/";
        String TMDB_IMAGE_SIZE_W185 = "http://image.tmdb.org/t/p/w185/";
        String TMDB_IMAGE_SIZE_W342 = "http://image.tmdb.org/t/p/w342/";
        String TMDB_IMAGE_SIZE_W500 = "http://image.tmdb.org/t/p/w500/";
        String TMDB_IMAGE_SIZE_W780 = "http://image.tmdb.org/t/p/w780/";
        String TMDB_IMAGE_SIZE_ORIGINAL = "http://image.tmdb.org/t/p/orginal/";
    }

    public interface INTENT {
        String MOVIE_DETAILS = "MOVIE_DETAILS";
    }
}
