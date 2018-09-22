package com.insomniacgks.newmoviesandshows.models;

import java.io.Serializable;
import java.util.ArrayList;

public class ShowModel implements Serializable {
    private String backdrop_url;
    private String first_air_date;
    private ArrayList<Integer> genres;
    private int id;
    private String name;
    private String overview;
    private String poster_url;
    private String rating;

    public ShowModel(int id, String name, String first_air_date, String overview, String rating, String poster_url, String backdrop_url, ArrayList<Integer> genres) {
        this.id = id;
        this.name = name;
        this.overview = overview;
        this.rating = rating;
        this.poster_url = poster_url;
        this.backdrop_url = backdrop_url;
        this.genres = genres;
        this.first_air_date = first_air_date;
    }

    public int getId() {
        return this.id;
    }

    public String getName() {
        return this.name;
    }

    public String getOverview() {
        return this.overview;
    }

    public String getRating() {
        return this.rating;
    }

    public String getPoster_url() {
        return this.poster_url;
    }

    public String getBackdrop_url() {
        return this.backdrop_url;
    }

    public ArrayList<Integer> getGenres() {
        return this.genres;
    }

    public String getFirst_air_date() {
        return this.first_air_date;
    }
}
