package com.insomniacgks.newmoviesandshows.fragments;


import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.insomniacgks.newmoviesandshows.R;
import com.insomniacgks.newmoviesandshows.activities.MovieDetailActivity;
import com.insomniacgks.newmoviesandshows.data.GetVideos;

import java.util.Objects;

/**
 * A simple {@link Fragment} subclass.
 */
public class VideoFragment extends Fragment {


    public VideoFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_video, container, false);
        if (PreferenceManager.getDefaultSharedPreferences(this.getContext()).getBoolean("dark_mode", false)) {
            v.findViewById(R.id.movie_videos_rl).setBackgroundColor(ContextCompat.getColor(Objects.requireNonNull(this.getContext()), R.color.black_theme_color));
            ((TextView) v.findViewById(R.id.movie_videos_tv)).setTextColor(Color.WHITE);
        }
        RecyclerView videos_rv = v.findViewById(R.id.movie_videos_rv);
        new GetVideos(getContext(), MovieDetailActivity.Companion.getMovie().getId(), videos_rv, "movie", (RelativeLayout) v.findViewById(R.id.movie_videos_rl)).execute();
        return v;
    }

}
