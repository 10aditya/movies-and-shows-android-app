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
import com.insomniacgks.newmoviesandshows.activities.MovieDetailActivity;
import com.insomniacgks.newmoviesandshows.data.GetImages;

/**
 * A simple {@link Fragment} subclass.
 */
public class ImageFragment extends Fragment {


    public ImageFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_image, container, false);
        RecyclerView images_rv = v.findViewById(R.id.movie_images_rv);
        new GetImages(getContext(), MovieDetailActivity.Companion.getMovie().getId(), images_rv, "movie", (RelativeLayout) v.findViewById(R.id.movie_images_rl)).execute();
        return v;
    }

}
