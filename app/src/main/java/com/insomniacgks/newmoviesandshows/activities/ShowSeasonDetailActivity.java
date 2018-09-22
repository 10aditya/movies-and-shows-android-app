package com.insomniacgks.newmoviesandshows.activities;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.design.widget.TabLayout.OnTabSelectedListener;
import android.support.design.widget.TabLayout.Tab;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.graphics.Palette;
import android.support.v7.graphics.Palette.PaletteAsyncListener;
import android.support.v7.graphics.Palette.Swatch;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.insomniacgks.newmoviesandshows.R;
import com.insomniacgks.newmoviesandshows.adapters.ShowSeasonViewPageAdapter;
import com.insomniacgks.newmoviesandshows.data.Constants;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Objects;

import static com.bumptech.glide.load.resource.bitmap.BitmapTransitionOptions.withCrossFade;

public class ShowSeasonDetailActivity extends AppCompatActivity implements OnTabSelectedListener {
    ImageView poster;
    String posterURL;
    int seasonNumber;
    TabLayout tabLayout;
    int tvShowID;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_season_detail);
        this.seasonNumber = getIntent().getIntExtra("seasonNumber", 0);
        this.tvShowID = getIntent().getIntExtra("tvShowID", 0);
        this.posterURL = getIntent().getStringExtra("posterURL");
        getSupportActionBar().setTitle("Season " + this.seasonNumber);
        this.viewPager = findViewById(R.id.seasonDetail_VP);
        this.tabLayout = findViewById(R.id.seasonDetail_TL);
        this.poster = findViewById(R.id.seasonBackground);

        RequestOptions options = new RequestOptions()
                .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                .priority(Priority.HIGH);

        Glide.with(this)
                .asBitmap()
                .load(posterURL)
                .apply(options)
                .apply(new RequestOptions()
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .apply(new RequestOptions()
                                .placeholder(getResources().getDrawable(R.drawable.gradient))))
                .listener(new RequestListener<Bitmap>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Bitmap> target, boolean isFirstResource) {
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Bitmap resource, Object model, Target<Bitmap> target, DataSource dataSource, boolean isFirstResource) {
                        Palette.from(resource).generate(new PaletteAsyncListener() {
                            public void onGenerated(Palette p) {
                                // Use generated instance
                                Swatch ps = p.getVibrantSwatch();
                                Swatch psw = p.getDarkVibrantSwatch();
                                if (ps != null) {
                                    ShowSeasonDetailActivity.this.getSupportActionBar().setBackgroundDrawable(new ColorDrawable(ps.getRgb()));
                                }
                                if (psw != null) {
                                    ShowSeasonDetailActivity.this.getWindow().setStatusBarColor(psw.getRgb());
                                }
                            }
                        });
                        return false;
                    }
                })
                .transition(withCrossFade())
                .into(poster);

        new FetchEpisodes().execute();
        this.tabLayout.addOnTabSelectedListener(this);
    }

    ViewPager viewPager;

    @SuppressLint({"StaticFieldLeak"})
    class FetchEpisodes extends AsyncTask<Void, Void, ArrayList<String[]>> {

        FetchEpisodes() {
        }

        protected ArrayList<String[]> doInBackground(Void... voids) {
            Exception e;
            ArrayList<String[]> episodes = new ArrayList<>();
            try {
                HttpURLConnection client = (HttpURLConnection) new URL(
                        "http://api.themoviedb.org/3/tv/"
                                + String.valueOf(ShowSeasonDetailActivity.this.tvShowID)
                                + "/season/"
                                + ShowSeasonDetailActivity.this.seasonNumber
                                + "?api_key="
                                + Constants.API_KEY
                                + "&language=en-US")
                        .openConnection();
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
                JSONArray jsonArray = new JSONObject(builder.toString()).getJSONArray("episodes");
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject object = jsonArray.getJSONObject(i);
                    episodes.add(new String[]{object.getString("name"), object.getString("overview"), object.getString("air_date"), object.getString("still_path")});
                }
            } catch (Exception ignored) {
            }

            return episodes;
        }

        protected void onPostExecute(ArrayList<String[]> episodes) {
            super.onPostExecute(episodes);
            for (int i = 1; i <= episodes.size(); i++) {
                tabLayout.addTab(tabLayout.newTab().setText(String.format(Locale.ENGLISH, "S%2dE%2d", seasonNumber, i)));
            }
            viewPager.setAdapter(new ShowSeasonViewPageAdapter(getSupportFragmentManager(), episodes, episodes.size(), ShowSeasonDetailActivity.this.seasonNumber));
        }

    }

    public void onTabSelected(Tab tab) {
        this.viewPager.setCurrentItem(tab.getPosition());
    }

    public void onTabUnselected(Tab tab) {
    }

    public void onTabReselected(Tab tab) {
    }
}
