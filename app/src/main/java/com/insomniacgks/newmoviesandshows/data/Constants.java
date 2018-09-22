package com.insomniacgks.newmoviesandshows.data;

import android.util.SparseArray;

import java.util.HashMap;

public class Constants {
    public static final String BASE_POSTER_PATH = "https://image.tmdb.org/t/p/w342";
    public static final String BASE_BACKDROP_PATH = "https://image.tmdb.org/t/p/w780";
    public static final String BASE_PROFILE_IMAGE_PATH = "https://image.tmdb.org/t/p/w154";
    public static final String API_KEY = "<TMDB API KEY>";
    public static final String YOUTUBE_VIDEO_URL = "http://www.youtube.com/watch?v=%1$s";
    public static final String YOUTUBE_THUMBNAIL_URL = "http://img.youtube.com/vi/%1$s/0.jpg";
    private static final HashMap<String, Integer> GENRES = new HashMap<>();
    private static final SparseArray<String> GENRES2 = new SparseArray<>();

    public static void initializeGenres() {
        GENRES.put("Action", 28);
        GENRES.put("Adventure", 12);
        GENRES.put("Animation", 16);
        GENRES.put("Comedy", 35);
        GENRES.put("Crime", 80);
        GENRES.put("Documentary", 99);
        GENRES.put("Drama", 18);
        GENRES.put("Family", 10751);
        GENRES.put("Fantasy", 14);
        GENRES.put("History", 36);
        GENRES.put("Horror", 27);
        GENRES.put("Music", 10402);
        GENRES.put("Mystery", 9648);
        GENRES.put("Romance", 10749);
        GENRES.put("Sci-Fi", 878);
        GENRES.put("TV Movie", 10770);
        GENRES.put("Thriller", 53);
        GENRES.put("War", 10752);
        GENRES.put("Western", 37);
        GENRES.put("Action & Adventure", 10759);
        GENRES.put("Kids", 10762);
        GENRES.put("News", 10763);
        GENRES.put("Reality", 10764);
        GENRES.put("Sci-Fi & Fantasy", 10765);
        GENRES.put("Soap", 10766);
        GENRES.put("Talk", 10767);
        GENRES.put("War & Politics", 10768);
    }

    public static void initializeGenresR() {
        GENRES2.put(28, "Action");
        GENRES2.put(12, "Adventure");
        GENRES2.put(16, "Animation");
        GENRES2.put(35, "Comedy");
        GENRES2.put(80, "Crime");
        GENRES2.put(99, "Documentary");
        GENRES2.put(18, "Drama");
        GENRES2.put(10751, "Family");
        GENRES2.put(14, "Fantasy");
        GENRES2.put(36, "History");
        GENRES2.put(27, "Horror");
        GENRES2.put(10402, "Music");
        GENRES2.put(9648, "Mystery");
        GENRES2.put(10749, "Romance");
        GENRES2.put(878, "Science Fiction");
        GENRES2.put(10770, "TV Movie");
        GENRES2.put(53, "Thriller");
        GENRES2.put(10752, "War");
        GENRES2.put(37, "Western");
        GENRES2.put(10759, "Action & Adventure");
        GENRES2.put(10762, "Kids");
        GENRES2.put(10763, "News");
        GENRES2.put(10764, "Reality");
        GENRES2.put(10765, "Sci-Fi & Fantasy");
        GENRES2.put(10766, "Soap");
        GENRES2.put(10767, "Talk");
        GENRES2.put(10768, "War & Politics");
    }

    public static int genreID(String genre) {
        return GENRES.get(genre);
    }

    public static String genreString(int id) {
        return GENRES2.get(id);
    }
}
