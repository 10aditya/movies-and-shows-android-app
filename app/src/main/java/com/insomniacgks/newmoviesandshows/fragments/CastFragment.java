package com.insomniacgks.newmoviesandshows.fragments;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.insomniacgks.newmoviesandshows.R;
import com.insomniacgks.newmoviesandshows.data.GetCast;
import com.insomniacgks.newmoviesandshows.data.GetVideos;

import static com.insomniacgks.newmoviesandshows.activities.MovieDetailActivity.movie;

/**
 * A simple {@link Fragment} subclass.
 */
public class CastFragment extends Fragment {


    public CastFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_cast, container, false);
        new GetCast(getContext(), movie.getId(), (RecyclerView) v.findViewById(R.id.movie_cast_rv), "movie", (RelativeLayout) v.findViewById(R.id.movie_cast_rl)).execute();
        return v;
    }

}
