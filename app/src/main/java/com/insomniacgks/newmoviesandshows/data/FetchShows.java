package com.insomniacgks.newmoviesandshows.data;

import android.os.AsyncTask;

import com.insomniacgks.newmoviesandshows.models.ShowModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Objects;

public class FetchShows extends AsyncTask<Void, Void, ArrayList<ShowModel>> {

    String link;
    private static int total_pages;

    public interface AsyncResponse {
        void processFinish(ArrayList<ShowModel> output, int total_pages);
    }

    public FetchShows.AsyncResponse delegate = null;

    public FetchShows(String link) {
        this.link = link;
    }

    @Override
    protected ArrayList<ShowModel> doInBackground(Void... voids) {
        ArrayList<ShowModel> shows = null;
        try {
            URL url = new URL(link);
            HttpURLConnection client = (HttpURLConnection) url.openConnection();
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
            shows = getShows(builder.toString());

        } catch (Exception ignored) {
        }
        return shows;
    }

    private ArrayList<ShowModel> getShows(String JSONstr) throws JSONException {
        total_pages=new JSONObject(JSONstr).getInt("total_pages");
        JSONArray resultArray = new JSONObject(JSONstr).getJSONArray("results");
        ArrayList<ShowModel> shows = null;
        if (resultArray.length() != 0) {
            shows = new ArrayList<>();
            for (int i = 0; i < resultArray.length(); i++) {
                JSONObject item = resultArray.getJSONObject(i);
                if (Objects.equals(item.getString("original_language"), "en")) {
                    JSONArray genres = item.getJSONArray("genre_ids");
                    ArrayList<Integer> genresList = new ArrayList<>();
                    for (int j = 0; j < genres.length(); j++) {
                        genresList.add(genres.getInt(j));
                    }
                    shows.add(new ShowModel(
                            item.getInt("id"),
                            item.getString("original_name"),
                            item.getString("first_air_date"),
                            item.getString("overview"),
                            String.valueOf(item.getDouble("vote_average")),
                            Constants.BASE_POSTER_PATH + item.getString("poster_path"),
                            Constants.BASE_BACKDROP_PATH + item.getString("backdrop_path"),
                            genresList));
                }
            }
        }
        return shows;
    }

    @Override
    protected void onPostExecute(ArrayList<ShowModel> showModels) {
        super.onPostExecute(showModels);
        delegate.processFinish(showModels, total_pages);
    }
}