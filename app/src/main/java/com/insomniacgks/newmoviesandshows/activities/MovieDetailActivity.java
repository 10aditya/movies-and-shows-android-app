package com.insomniacgks.newmoviesandshows.activities;

import android.app.Activity;
import android.app.FragmentTransaction;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.RecyclerView;
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
import com.insomniacgks.newmoviesandshows.data.Constants;
import com.insomniacgks.newmoviesandshows.R;
import com.insomniacgks.newmoviesandshows.data.GetCast;
import com.insomniacgks.newmoviesandshows.data.GetCrew;
import com.insomniacgks.newmoviesandshows.data.GetImages;
import com.insomniacgks.newmoviesandshows.data.GetReviews;
import com.insomniacgks.newmoviesandshows.data.GetVideos;
import com.insomniacgks.newmoviesandshows.fragments.CastFragment;
import com.insomniacgks.newmoviesandshows.fragments.CrewFragment;
import com.insomniacgks.newmoviesandshows.fragments.ImageFragment;
import com.insomniacgks.newmoviesandshows.fragments.ReviewFragment;
import com.insomniacgks.newmoviesandshows.fragments.VideoFragment;
import com.insomniacgks.newmoviesandshows.models.MovieModel;

import java.util.ArrayList;
import java.util.Objects;

import static com.bumptech.glide.load.resource.bitmap.BitmapTransitionOptions.withCrossFade;

public class MovieDetailActivity extends AppCompatActivity {

    private TextView movieTitle, movieRatings, movieOverview, movieReleaseDate, movieGenres;
    private ImageView moviePoster, movieBackdrop;
    private RecyclerView images_rv, cast_rv;
    private RecyclerView crew_rv, reviews_rv, videos_rv;
    private RelativeLayout reviews_rl;
    public static MovieModel movie;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_detail);
        movie = (MovieModel) Objects.requireNonNull(getIntent().getExtras()).getSerializable("movie");
        initializeViews();
        movieTitle.setText(movie.getTitle());
        movieOverview.setText(movie.getOverview());
        movieRatings.setText(String.valueOf(movie.getRating()));
        movieReleaseDate.setText(movie.getReleaseDate());
        ArrayList<Integer> genres = movie.getGenresID();
        StringBuilder genresString = new StringBuilder("");
        int i;
        for (i = 0; i < genres.size() - 1; i++) {
            genresString.append(Constants.genreString(genres.get(i))).append(" | ");
        }
        genresString.append(Constants.genreString(genres.get(i)));
        movieGenres.setText(genresString.toString());

        RequestOptions options = new RequestOptions()
                .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                .priority(Priority.HIGH);

        Glide.with(this)
                .asBitmap()
                .load(movie.getPosterURL())
                .apply(options)
                .apply(new RequestOptions()
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .apply(new RequestOptions()
                                .placeholder(this.getResources().getDrawable(R.drawable.gradient))))
                .transition(withCrossFade())
                .into(moviePoster);

        Glide.with(this)
                .asBitmap()
                .load(movie.getBackdropURL())
                .apply(options)
                .apply(new RequestOptions()
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .apply(new RequestOptions()
                                .placeholder(this.getResources().getDrawable(R.drawable.gradient))))
                .listener(new RequestListener<Bitmap>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Bitmap> target, boolean isFirstResource) {
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Bitmap resource, Object model, Target<Bitmap> target, DataSource dataSource, boolean isFirstResource) {
                        Palette.from(resource).generate(new Palette.PaletteAsyncListener() {
                            @Override
                            public void onGenerated(@NonNull Palette palette) {
                                Palette.Swatch swatch = palette.getVibrantSwatch();
                                if (swatch != null) {
                                    getWindow().setStatusBarColor(swatch.getRgb());
                                }
                            }
                        });

                        return false;
                    }
                })
                .transition(withCrossFade())
                .into(movieBackdrop);

        android.support.v4.app.FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out);
        fragmentTransaction.replace(R.id.movie_images_fl, new ImageFragment());
        fragmentTransaction.replace(R.id.movie_cast_fl, new CastFragment());
        fragmentTransaction.replace(R.id.movie_videos_fl, new VideoFragment());
        fragmentTransaction.replace(R.id.movie_crew_fl, new CrewFragment());
        fragmentTransaction.replace(R.id.movie_reviews_fl, new ReviewFragment());
        fragmentTransaction.commitAllowingStateLoss();

/*
        new GetImages(this, movie.getId(), images_rv, "movie", (RelativeLayout) findViewById(R.id.movie_images_rl)).execute();
        new GetCast(this, movie.getId(), cast_rv, "movie", (RelativeLayout) findViewById(R.id.movie_cast_rl)).execute();
        new GetCrew(this, movie.getId(), crew_rv, "movie",(RelativeLayout)findViewById(R.id.movie_crew_rl)).execute();
        new GetReviews(this, movie.getId(), reviews_rv, reviews_rl, "movie").execute();
        new GetVideos(this, movie.getId(), videos_rv, "movie",(RelativeLayout)findViewById(R.id.movie_videos_rl)).execute();
*/
    }

    private void initializeViews() {
        movieTitle = findViewById(R.id.movie_title);
        movieReleaseDate = findViewById(R.id.movie_release_date);
        movieRatings = findViewById(R.id.movie_ratings);
        movieGenres = findViewById(R.id.movie_genres);
        moviePoster = findViewById(R.id.movie_poster);
        movieBackdrop = findViewById(R.id.movie_backdrop);
        movieOverview = findViewById(R.id.show_overview);/*
        images_rv = findViewById(R.id.movie_images_rv);
        cast_rv = findViewById(R.id.movie_cast_rv);
        crew_rv = findViewById(R.id.movie_crew_rv);
        reviews_rv = findViewById(R.id.movie_reviews_rv);
        reviews_rl = findViewById(R.id.movie_reviews_rl);
        videos_rv = findViewById(R.id.movie_videos_rv);*/
    }

}
