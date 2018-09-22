package com.insomniacgks.newmoviesandshows.data;

import android.content.Context;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.insomniacgks.newmoviesandshows.R;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class GetReviews extends AsyncTask<String, Void, ArrayList<String[]>> {
    private Context context;
    private int id;
    private RecyclerView recyclerView;
    private RelativeLayout rl;
    private String type;

    public GetReviews(Context context, int id, RecyclerView recyclerView, RelativeLayout rl, String type) {
        this.context = context;
        this.id = id;
        this.recyclerView = recyclerView;
        this.rl = rl;
        this.type = type;
    }

    protected ArrayList<String[]> doInBackground(String... strings) {
        ArrayList<String[]> reviews = null;
        try {
            HttpURLConnection client = (HttpURLConnection) new URL("http://api.themoviedb.org/3/" + type + "/" + id + "/reviews?api_key=" + Constants.API_KEY + "&language=en-US").openConnection();
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
            reviews = new ArrayList<>();
            JSONArray resultArray = new JSONObject(builder.toString()).getJSONArray("results");
            int i = 0;
            while (i < resultArray.length()) {
                JSONObject object = resultArray.getJSONObject(i);
                reviews.add(new String[]{object.getString("author"), object.getString("content")});
                i++;
            }
        } catch (Exception ignored) {
        }
        return reviews;
    }

    protected void onPostExecute(ArrayList<String[]> strings) {
        super.onPostExecute(strings);
        if (strings != null && strings.size() != 0) {
            RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this.context);
            ReviewsRecyclerViewAdapter myAdapter = new ReviewsRecyclerViewAdapter(this.context, strings);
            recyclerView.setLayoutManager(layoutManager);
            recyclerView.setAdapter(myAdapter);
        } else {
            rl.setVisibility(View.GONE);
        }
    }


    class ReviewsRecyclerViewAdapter extends RecyclerView.Adapter<ReviewsRecyclerViewAdapter.ViewHolder> {
        private ArrayList<String[]> reviewss;
        private Context context;

        ReviewsRecyclerViewAdapter(Context context, ArrayList<String[]> Reviewss) {
            this.context = context;
            this.reviewss = Reviewss;
        }


        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
            return new ViewHolder(LayoutInflater.
                    from(parent.getContext()).inflate(R.layout.reviews_card_layout, parent, false));
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            holder.author.setText(String.format("Review By %s:", this.reviewss.get(position)[0]));
            holder.content.setText(this.reviewss.get(position)[1]);
        }

        @Override
        public int getItemCount() {
            return reviewss.size();
        }

        class ViewHolder extends android.support.v7.widget.RecyclerView.ViewHolder {

            TextView content, author;

            ViewHolder(View itemView) {
                super(itemView);
                this.author = itemView.findViewById(R.id.review_author);
                this.content = itemView.findViewById(R.id.review_content);
            }

        }
    }
}