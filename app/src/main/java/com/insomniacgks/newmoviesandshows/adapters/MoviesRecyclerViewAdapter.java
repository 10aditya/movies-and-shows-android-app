package com.insomniacgks.newmoviesandshows.adapters;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.insomniacgks.newmoviesandshows.R;
import com.insomniacgks.newmoviesandshows.activities.MovieDetailActivity;
import com.insomniacgks.newmoviesandshows.models.MovieModel;

import java.util.ArrayList;

import static com.bumptech.glide.load.resource.bitmap.BitmapTransitionOptions.withCrossFade;

public class MoviesRecyclerViewAdapter extends RecyclerView.Adapter<MoviesRecyclerViewAdapter.ViewHolder> {
    private static ArrayList<MovieModel> mMovies;
    private Context context;
//    private InterstitialAd interstitialAd;

    public MoviesRecyclerViewAdapter(ArrayList<MovieModel> movies, Context context) {
        mMovies = movies;
        this.context = context;
        //this.interstitialAd = new InterstitialAd(context);
        //this.interstitialAd.setAdUnitId(Constants.ADMOB_MOVIE_AD_ID);
        //this.interstitialAd.loadAd(new Builder().addTestDevice("872C1F2EAF7C1188C2E85D0A61C9E668").build());
    }

    public void addMoreItems(ArrayList<MovieModel> moreMovies) {
        mMovies.addAll(moreMovies);
    }

    @NonNull
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.movie_show_card_layout, parent, false));
    }

    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        MovieModel movie = mMovies.get(position);
        if (movie.getPosterURL() != null) {
            holder.title.setText(movie.getTitle());
            holder.title.setSelected(true);
            RequestOptions options = new RequestOptions()
                    .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                    .priority(Priority.HIGH);

            Glide.with(this.context)
                    .asBitmap()
                    .load(movie.getPosterURL())
                    .apply(options)
                    .apply(new RequestOptions()
                            .diskCacheStrategy(DiskCacheStrategy.ALL)
                            .apply(new RequestOptions()
                                    .placeholder(this.context.getResources().getDrawable(R.drawable.gradient))))
                    .transition(withCrossFade())
                    .into(holder.moviePoster);
        }
    }

    public int getItemCount() {
        return mMovies == null ? 0 : mMovies.size();
    }

    public class ViewHolder extends android.support.v7.widget.RecyclerView.ViewHolder implements View.OnClickListener {
        ImageView moviePoster;
        TextView title;

        ViewHolder(View itemView) {
            super(itemView);
            this.title = itemView.findViewById(R.id.movie_name);
            this.moviePoster = itemView.findViewById(R.id.poster);
            itemView.setOnClickListener(this);
        }

        public void onClick(View view) {
            MovieModel clickedItem = MoviesRecyclerViewAdapter.mMovies.get(getAdapterPosition());
            final Intent i = new Intent(MoviesRecyclerViewAdapter.this.context, MovieDetailActivity.class);
            //       final ActivityOptions options = ActivityOptions.makeScaleUpAnimation(view, 0, 0, view.getWidth(), view.getHeight());
            ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation((Activity) context,
                    view.findViewById(R.id.poster),
                    "testing");
            Bundle bundle = new Bundle();
            bundle.putSerializable("movie", clickedItem);
            i.putExtras(bundle);

            MoviesRecyclerViewAdapter.this.context.startActivity(i, options.toBundle());
        }

    }
}
