package com.insomniacgks.newmoviesandshows.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.insomniacgks.newmoviesandshows.R;
import com.insomniacgks.newmoviesandshows.data.GetCast;
import com.insomniacgks.newmoviesandshows.data.GetCrew;

import static com.insomniacgks.newmoviesandshows.activities.MovieDetailActivity.movie;

/**
 * A simple {@link Fragment} subclass.
 */
public class CrewFragment extends Fragment {


    public CrewFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_crew, container, false);
        new GetCrew(getContext(), movie.getId(), (RecyclerView) v.findViewById(R.id.movie_crew_rv), "movie", (RelativeLayout) v.findViewById(R.id.movie_crew_rl)).execute();
        return v;
    }

}
