package com.insomniacgks.newmoviesandshows.data;

import android.os.AsyncTask;

import com.insomniacgks.newmoviesandshows.models.MovieModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class FetchMovies extends AsyncTask<Void, Void, ArrayList<MovieModel>> {
    private String url;
    private static int total_pages;

    public interface AsyncResponse {
        void processFinish(ArrayList<MovieModel> output, int total_pages);
    }

    public AsyncResponse delegate = null;

    public FetchMovies(String url) {
        this.url = url;
    }

    private static ArrayList<MovieModel> getJSON(String data) throws JSONException {
        if (data == null) {
            return null;
        }
        total_pages = new JSONObject(data).getInt("total_pages");
        ArrayList<MovieModel> movieItems = new ArrayList<>();
        JSONArray resultArray = new JSONObject(data).getJSONArray("results");
        for (int i = 0; i < resultArray.length(); i++) {
            String movieTitle;
            String releaseDate;
            String overView;
            JSONObject movieItem = resultArray.getJSONObject(i);
            if (movieItem.get("title") == null) {
                movieTitle = null;
            } else {
                movieTitle = movieItem.getString("title");
            }
            if (movieItem.get("release_date") == null) {
                releaseDate = null;
            } else {
                releaseDate = movieItem.getString("release_date");
            }
            if (movieItem.get("overview") == null) {
                overView = null;
            } else {
                overView = movieItem.getString("overview");
            }
            double rating = movieItem.getDouble("vote_average");
            JSONArray geners = movieItem.getJSONArray("genre_ids");
            ArrayList<Integer> genresList = new ArrayList<>();
            for (int j = 0; j < geners.length(); j++) {
                genresList.add(geners.getInt(j));
            }
            String posterURL = null;
            String backdropURL = null;
            try {
                posterURL = movieItem.getString("poster_path");
                backdropURL = movieItem.getString("backdrop_path");
            } catch (Exception ignored) {
            }
            int id = movieItem.getInt("id");
            movieItems.add(new MovieModel(
                    movieTitle,
                    releaseDate,
                    overView,
                    Constants.BASE_POSTER_PATH + posterURL,
                    Constants.BASE_BACKDROP_PATH + backdropURL,
                    id,
                    (float) rating,
                    genresList));
        }
        return movieItems;
    }

    private static String getJSONstr(String link) {
        String JSONstr = null;
        try {

            HttpURLConnection client = (HttpURLConnection) new URL(link).openConnection();
            
            client.setRequestMethod("GET");
            client.connect();
            BufferedReader reader = new BufferedReader(new InputStreamReader(client.getInputStream()));
            StringBuilder builder = new StringBuilder();
            while (true) {
                String line = reader.readLine();
                if (line == null) {
                    break;
                }
                builder.append(line);
            }
            JSONstr = builder.toString();
            System.out.println(JSONstr);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return JSONstr;
    }

    @Override
    protected ArrayList<MovieModel> doInBackground(Void... voids) {
        try {
            return getJSON(getJSONstr(url));
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    protected void onPostExecute(ArrayList<MovieModel> movieModels) {
        super.onPostExecute(movieModels);
        delegate.processFinish(movieModels, total_pages);
    }
}
