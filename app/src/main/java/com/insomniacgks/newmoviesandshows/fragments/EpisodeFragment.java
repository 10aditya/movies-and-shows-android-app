package com.insomniacgks.newmoviesandshows.fragments;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
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

import java.util.Objects;

import static com.bumptech.glide.load.resource.bitmap.BitmapTransitionOptions.withCrossFade;

public class EpisodeFragment extends Fragment {
    private String[] episode;

    public EpisodeFragment() {

    }


    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_episode, container, false);
        TextView episodeAirDate = v.findViewById(R.id.episode_air_date);
        TextView episodeOverview = v.findViewById(R.id.episode_overview);
        ImageView episodeStill = v.findViewById(R.id.episode_still);
        episode = Objects.requireNonNull(getArguments()).getStringArray("episode");

        ((TextView) v.findViewById(R.id.episode_name)).setText(this.episode[0]);
        episodeOverview.setText(this.episode[1]);
        episodeAirDate.setText(this.episode[2]);
        episodeOverview.setMovementMethod(new ScrollingMovementMethod());

        RequestOptions options = new RequestOptions()
                .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                .priority(Priority.HIGH);

        Glide.with(this)
                .asBitmap()
                .load(Constants.BASE_POSTER_PATH.substring(0, Constants.BASE_POSTER_PATH.length() - 3) + "500" + this.episode[3])
                .apply(options)
                .apply(new RequestOptions()
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .apply(new RequestOptions()
                                .placeholder(this.getResources().getDrawable(R.drawable.gradient))))
                .transition(withCrossFade())
                .into(episodeStill);

        return v;
    }
}
