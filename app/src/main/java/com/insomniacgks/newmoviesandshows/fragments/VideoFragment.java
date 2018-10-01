package com.insomniacgks.newmoviesandshows.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.insomniacgks.newmoviesandshows.R;
import com.insomniacgks.newmoviesandshows.data.GetVideos;

import static com.insomniacgks.newmoviesandshows.activities.MovieDetailActivity.movie;

/**
 * A simple {@link Fragment} subclass.
 */
public class VideoFragment extends Fragment {


    public VideoFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_video, container, false);
        RecyclerView videos_rv = v.findViewById(R.id.movie_videos_rv);
        new GetVideos(getContext(), movie.getId(), videos_rv, "movie", (RelativeLayout) v.findViewById(R.id.movie_videos_rl)).execute();
        return v;
    }

}
