package com.insomniacgks.newmoviesandshows.data;

import android.content.Context;
import android.os.AsyncTask;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.RelativeLayout;

import com.insomniacgks.newmoviesandshows.adapters.ImagesRecyclerViewAdapter;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class GetImages extends AsyncTask<Void, Void, ArrayList<String>> {

    private final int id;
    private final RecyclerView recyclerview;
    private String type;
    private RelativeLayout rl;
    private Context context;


    public GetImages(Context context, int id, RecyclerView recyclerView, String type, RelativeLayout rl) {
        this.id = id;
        this.context = context;
        this.recyclerview = recyclerView;
        this.type = type;
        this.rl = rl;
    }

    @Override
    protected ArrayList<String> doInBackground(Void... voids) {
        ArrayList<String> images = null;
        try {
            URL url = new URL("https://api.themoviedb.org/3/" + type + "/" + id + "/images?api_key=" + Constants.API_KEY);
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
            JSONObject object = new JSONObject(builder.toString());
            JSONArray backdrops = object.getJSONArray("backdrops");

            images = new ArrayList<>();
            for (int i = 0; i < backdrops.length(); i++) {
                images.add(Constants.BASE_BACKDROP_PATH + backdrops.getJSONObject(i).getString("file_path"));
            }
            return images;

        } catch (Exception ignored) {
        }
        return images;
    }

    @Override
    protected void onPostExecute(ArrayList<String> strings) {
        super.onPostExecute(strings);
        if (strings != null) {
            if (strings.size() != 0) {
                RecyclerView.LayoutManager linearLayoutManager = new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false);
                recyclerview.setLayoutManager(linearLayoutManager);
                recyclerview.setAdapter(new ImagesRecyclerViewAdapter(context, strings));
            } else {
                rl.setVisibility(View.GONE);
            }
        } else {
            rl.setVisibility(View.GONE);
        }
    }
}

