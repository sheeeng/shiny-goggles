package com.example.android.movies;

import java.util.ArrayList;
import java.util.List;

public class MovieResults {
    private List<Movie> listMovies;

    public MovieResults() {
        listMovies = new ArrayList<>();
    }

    public List<Movie> getListMovies() {
        return listMovies;
    }

    void setListMovies(List<Movie> listMovies) {
        this.listMovies = listMovies;
    }
}
