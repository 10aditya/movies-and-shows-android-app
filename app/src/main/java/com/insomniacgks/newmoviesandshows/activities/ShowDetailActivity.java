package com.insomniacgks.newmoviesandshows.activities;

import android.annotation.SuppressLint;
import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.insomniacgks.newmoviesandshows.R;
import com.insomniacgks.newmoviesandshows.data.Constants;
import com.insomniacgks.newmoviesandshows.data.GetCast;
import com.insomniacgks.newmoviesandshows.data.GetCrew;
import com.insomniacgks.newmoviesandshows.data.GetImages;
import com.insomniacgks.newmoviesandshows.data.GetReviews;
import com.insomniacgks.newmoviesandshows.data.GetVideos;
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

import eightbitlab.com.blurview.BlurView;

import static com.bumptech.glide.load.resource.bitmap.BitmapTransitionOptions.withCrossFade;

public class ShowDetailActivity extends AppCompatActivity {

    ShowModel show;
    TextView name, rating, release_year, homepage, genres, networks;
    ImageView poster, backdrop;
    BlurView blurView;
    private TextView showOverview;
    private RecyclerView seasons_rv, images_rv, cast_rv, crew_rv, reviews_rv, videos_rv;
    private RelativeLayout reviews_rl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_detail);
        show = (ShowModel) Objects.requireNonNull(getIntent().getExtras()).getSerializable("show");
        initializeViews();
        new FetchShowDetails().execute();
        name.setText(show.getName());
        rating.setText(String.valueOf(show.getRating()));
        release_year.setText(show.getFirst_air_date().substring(0, 4));
        StringBuilder genres = new StringBuilder("");
        ArrayList<Integer> genreeIDs = show.getGenres();
        for (int i = 0; i < genreeIDs.size() - 1; i++) {
            genres.append(Constants.genreString(genreeIDs.get(i))).append(" | ");
        }
        if (genreeIDs.size() != 0)
            genres.append(Constants.genreString(genreeIDs.get(genreeIDs.size() - 1)));
        this.genres.setText(genres.toString());
        RequestOptions options = new RequestOptions()
                .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                .priority(Priority.HIGH);

        Glide.with(this)
                .asBitmap()
                .load(show.getPoster_url())
                .apply(options)
                .apply(new RequestOptions()
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .apply(new RequestOptions()
                                .placeholder(this.getResources().getDrawable(R.drawable.gradient))))
                .transition(withCrossFade())
                .into(poster);

        Glide.with(this)
                .asBitmap()
                .load(show.getBackdrop_url())
                .apply(options)
                .apply(new RequestOptions()
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .apply(new RequestOptions()
                                .placeholder(this.getResources().getDrawable(R.drawable.gradient))))
                .transition(withCrossFade())
                .listener(new RequestListener<Bitmap>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Bitmap> target, boolean isFirstResource) {
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Bitmap resource, Object model, Target<Bitmap> target, DataSource dataSource, boolean isFirstResource) {


                        blurView.setupWith(backdrop).blurRadius(8.0f);
                        Palette.from(resource).generate(new Palette.PaletteAsyncListener() {
                            @Override
                            public void onGenerated(@NonNull Palette palette) {
                                Palette.Swatch swatch_d = palette.getDarkMutedSwatch();
                                if (swatch_d != null) {
                                    getWindow().setStatusBarColor((swatch_d).getRgb());
                                }/*
                                if (swatch_d != null) {
                                    name.setTextColor(swatch_d.getRgb());
                                    rating.setTextColor(swatch_d.getRgb());
                                    release_year.setTextColor(swatch_d.getRgb());
                                    networks.setTextColor(swatch_d.getRgb());
                                    ShowDetailActivity.this.genres.setTextColor(swatch_d.getRgb());
                                }*/
                            }
                        });
                        return false;
                    }
                })
                .into(backdrop);
        showOverview.setText(show.getOverview());

        new GetImages(this, show.getId(), images_rv, "tv", (RelativeLayout) findViewById(R.id.show_images_rl)).execute();
        new GetCast(this, show.getId(), cast_rv, "tv", (RelativeLayout) findViewById(R.id.show_cast_rl)).execute();
        new GetCrew(this, show.getId(), crew_rv, "tv", (RelativeLayout) findViewById(R.id.show_crew_rl)).execute();
        new GetReviews(this, show.getId(), reviews_rv, reviews_rl, "tv").execute();
        new GetVideos(this, show.getId(), videos_rv, "tv", (RelativeLayout) findViewById(R.id.show_videos_rl)).execute();


    }


    void initializeViews() {
        name = findViewById(R.id.show_name);
        rating = findViewById(R.id.show_rating);
        release_year = findViewById(R.id.show_runtime);
        homepage = findViewById(R.id.homepage);
        genres = findViewById(R.id.show_genres);
        networks = findViewById(R.id.show_networks);
        poster = findViewById(R.id.tv_show_poster);
        backdrop = findViewById(R.id.show_backdrop);
        blurView = findViewById(R.id.blur_view);
        showOverview = findViewById(R.id.show_overview);
        images_rv = findViewById(R.id.show_images_rv);
        cast_rv = findViewById(R.id.show_cast_rv);
        crew_rv = findViewById(R.id.show_crew_rv);
        reviews_rv = findViewById(R.id.show_reviews_rv);
        reviews_rl = findViewById(R.id.show_reviews_rl);
        videos_rv = findViewById(R.id.show_videos_rv);
        seasons_rv = findViewById(R.id.show_seasons_rv);
    }

    @SuppressLint("StaticFieldLeak")
    class FetchShowDetails extends AsyncTask<Void, Void, JSONObject> {
        FetchShowDetails() {

        }

        @Override
        protected void onPostExecute(JSONObject jsonData) {
            super.onPostExecute(jsonData);

            try {
                homepage.setText(jsonData.getString("homepage"));

                JSONArray array = jsonData.getJSONArray("networks");
                StringBuilder networks = new StringBuilder("");
                for (int i = 0; i < array.length() - 1; i++) {
                    networks.append(array.getJSONObject(i).getString("name")).append(" | ");
                }
                networks.append(array.getJSONObject(array.length() - 1).getString("name"));
                ShowDetailActivity.this.networks.setText(networks);

                JSONArray jsonArray = jsonData.getJSONArray("seasons");
                ArrayList<String[]> seasons = new ArrayList<>();
                JSONObject jsonObject;
                for (int i = 0; i < jsonArray.length(); i++) {
                    jsonObject = jsonArray.getJSONObject(i);
                    if (jsonObject.getInt("season_number") != 0) {
                        seasons.add(new String[]{String.valueOf(jsonObject.getInt("id")),
                                jsonObject.getString("air_date"),
                                jsonObject.getString("poster_path")});
                    }
                }
                RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(ShowDetailActivity.this, LinearLayoutManager.HORIZONTAL, false);
                seasons_rv.setLayoutManager(layoutManager);
                seasons_rv.setAdapter(new SeasonsRecyclerViewAdapter(ShowDetailActivity.this, seasons));

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        protected JSONObject doInBackground(Void... voids) {
            JSONObject jsonData = null;
            try {
                HttpURLConnection client = (HttpURLConnection) new URL(
                        "http://api.themoviedb.org/3/tv/"
                                + String.valueOf(ShowDetailActivity.this.show.getId())
                                + "?api_key="
                                + Constants.API_KEY
                                + "&language=en-US"
                ).openConnection();
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

                jsonData = new JSONObject(builder.toString());
            } catch (Exception ignored) {
            }
            return jsonData;
        }
    }

    class SeasonsRecyclerViewAdapter extends RecyclerView.Adapter<SeasonsRecyclerViewAdapter.ViewHolder> {
        private Context context;
        ArrayList<String[]> seasons;

        SeasonsRecyclerViewAdapter(Context context, ArrayList<String[]> seasons) {
            this.context = context;
            this.seasons = seasons;
        }

        class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
            TextView season_air_date;
            TextView season_number;
            ImageView season_poster;

            ViewHolder(View itemView) {
                super(itemView);
                this.season_air_date = itemView.findViewById(R.id.season_air_date);
                this.season_number = itemView.findViewById(R.id.season_number);
                this.season_poster = itemView.findViewById(R.id.season_poster);
                itemView.setOnClickListener(this);
            }

            public void onClick(View view) {
                Intent intent = new Intent(context, ShowSeasonDetailActivity.class);
                intent.putExtra("seasonNumber", getAdapterPosition() + 1);
                intent.putExtra("tvShowID", show.getId());
                intent.putExtra("posterURL", show.getPoster_url());
                startActivity(intent,
                        ActivityOptions.makeScaleUpAnimation(
                                view, 0, 0, view.getWidth(), view.getHeight()).toBundle());
            }

        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new ViewHolder(LayoutInflater.from(
                    parent.getContext()).inflate(R.layout.season_card_layout, parent, false));
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            holder.season_number.setText(String.format("Season %s", String.valueOf(position + 1)));
            holder.season_air_date.setText(seasons.get(position)[1]);
            RequestOptions options = new RequestOptions()
                    .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                    .priority(Priority.HIGH);

            Glide.with(context)
                    .asBitmap()
                    .load(Constants.BASE_POSTER_PATH + seasons.get(position)[2])
                    .apply(options)
                    .apply(new RequestOptions()
                            .diskCacheStrategy(DiskCacheStrategy.ALL)
                            .apply(new RequestOptions()
                                    .placeholder(this.context.getResources().getDrawable(R.drawable.gradient))))
                    .transition(withCrossFade())
                    .into(holder.season_poster);
        }

        public int getItemCount() {
            return seasons.size();
        }
    }
}

