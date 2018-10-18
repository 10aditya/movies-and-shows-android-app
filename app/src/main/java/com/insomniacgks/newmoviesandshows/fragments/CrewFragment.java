package com.insomniacgks.newmoviesandshows.fragments;


import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
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
import com.insomniacgks.newmoviesandshows.data.GetCrew;

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
        if (PreferenceManager.getDefaultSharedPreferences(this.getContext()).getBoolean("dark_mode", false)) {
            ((TextView) v.findViewById(R.id.movie_crew_tv)).setTextColor(Color.WHITE);
            v.findViewById(R.id.movie_crew_rl).setBackgroundColor(ContextCompat.getColor(this.getContext(), R.color.black_theme_color));
        }

        new GetCrew(getContext(), MovieDetailActivity.Companion.getMovie().getId(), (RecyclerView) v.findViewById(R.id.movie_crew_rv), "movie", (RelativeLayout) v.findViewById(R.id.movie_crew_rl)).execute();
        return v;
    }

}
