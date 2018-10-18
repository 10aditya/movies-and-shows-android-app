package com.insomniacgks.newmoviesandshows.data;

import android.annotation.SuppressLint;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.insomniacgks.newmoviesandshows.R;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import static com.bumptech.glide.load.resource.bitmap.BitmapTransitionOptions.withCrossFade;

public class GetVideos extends AsyncTask<String, Void, ArrayList<String>> {
    @SuppressLint("StaticFieldLeak")
    private Context context;
    private int id;
    @SuppressLint("StaticFieldLeak")
    private RecyclerView recyclerView;
    private String type;
    @SuppressLint("StaticFieldLeak")
    private RelativeLayout rl;


    public GetVideos(Context context, int id, RecyclerView recyclerView, String type, RelativeLayout rl) {
        this.context = context;
        this.id = id;
        this.recyclerView = recyclerView;
        this.type = type;
        this.rl = rl;
    }

    protected ArrayList<String> doInBackground(String... strings) {
        ArrayList<String> reviews = null;
        try {
            HttpURLConnection client = (HttpURLConnection) new URL("http://api.themoviedb.org/3/" + type + "/" + id + "/videos?api_key=" + Constants.API_KEY + "&language=en-US").openConnection();
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
                if ("YouTube".equals(object.getString("site"))) {
                    reviews.add(object.getString("key"));
                    i++;
                }
            }
        } catch (Exception ignored) {
        }
        return reviews;
    }

    protected void onPostExecute(ArrayList<String> strings) {
        super.onPostExecute(strings);
        if (strings != null) {
            if (strings.size() != 0) {
                RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this.context, LinearLayoutManager.HORIZONTAL, false);
                VideosRecyclerViewAdapter myAdapter = new VideosRecyclerViewAdapter(this.context, strings);
                recyclerView.setLayoutManager(layoutManager);
                recyclerView.setAdapter(myAdapter);
            } else {
                rl.setVisibility(View.GONE);
            }
        } else {
            rl.setVisibility(View.GONE);
        }
    }


    class VideosRecyclerViewAdapter extends RecyclerView.Adapter<VideosRecyclerViewAdapter.ViewHolder> {
        private ArrayList<String> videoss;
        private Context context;

        VideosRecyclerViewAdapter(Context context, ArrayList<String> Videoss) {
            this.context = context;
            this.videoss = Videoss;
        }


        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
            return new ViewHolder(LayoutInflater.
                    from(parent.getContext()).inflate(R.layout.videos_card_layout, parent, false));
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            RequestOptions options = new RequestOptions()
                    .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                    .priority(Priority.HIGH);

            if(PreferenceManager.getDefaultSharedPreferences(this.context).getBoolean("dark_mode", false)){
                holder.layoutBack.setBackgroundColor(ContextCompat.getColor(this.context, R.color.black_theme_color));
            }
            Glide.with(context)
                    .asBitmap()
                    .load(String.format(Constants.YOUTUBE_THUMBNAIL_URL, videoss.get(position)))
                    .apply(options)
                    .apply(new RequestOptions()
                            .diskCacheStrategy(DiskCacheStrategy.ALL)
                            .apply(new RequestOptions()
                                    .placeholder(this.context.getResources().getDrawable(R.drawable.gradient))))
                    .transition(withCrossFade())
                    .into(holder.video_thumbnail);

        }

        @Override
        public int getItemCount() {
            return videoss.size();
        }

        class ViewHolder extends android.support.v7.widget.RecyclerView.ViewHolder implements View.OnClickListener {

            ImageView video_thumbnail;
            RelativeLayout layoutBack;

            ViewHolder(View itemView) {
                super(itemView);
                layoutBack = itemView.findViewById(R.id.layout_back);
                this.video_thumbnail = itemView.findViewById(R.id.trailer_thumbnail);
                itemView.setOnClickListener(this);
            }

            @Override
            public void onClick(View v) {
                Intent appIntent = new Intent("android.intent.action.VIEW", Uri.parse("vnd.youtube:" + videoss.get(getAdapterPosition())));
                Intent webIntent = new Intent("android.intent.action.VIEW", Uri.parse("http://www.youtube.com/watch?v=" + videoss.get(getAdapterPosition())));
                try {
                    context.startActivity(appIntent);
                } catch (ActivityNotFoundException e) {
                    context.startActivity(webIntent);
                }
            }
        }
    }
}