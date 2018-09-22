package com.insomniacgks.newmoviesandshows.models;

import java.io.Serializable;
import java.util.ArrayList;

public class MovieModel implements Serializable {
    private String backdropURL;
    private ArrayList<Integer> genresID;
    private int id;
    private String overview;
    private String posterURL;
    private float rating;
    private String releaseDate;
    private String title;

    public MovieModel(String movieTitle, String releaseDate, String overView, String s, String s1, int id, float rating, ArrayList<Integer> genresID) {
        this.genresID = genresID;
        this.title = movieTitle;
        this.releaseDate = releaseDate;
        this.overview = overView;
        this.rating = rating;
        this.backdropURL = s1;
        this.posterURL = s;
        this.id = id;
    }

    public String getTitle() {
        return this.title;
    }

    public String getReleaseDate() {
        return this.releaseDate;
    }

    public String getOverview() {
        return this.overview;
    }

    public String getPosterURL() {
        return this.posterURL;
    }

    public String getBackdropURL() {
        return this.backdropURL;
    }

    public int getId() {
        return this.id;
    }

    public float getRating() {
        return this.rating;
    }

    public ArrayList<Integer> getGenresID() {
        return this.genresID;
    }
}
