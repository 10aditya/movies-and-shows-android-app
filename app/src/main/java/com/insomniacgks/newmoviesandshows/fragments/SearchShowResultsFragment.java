package com.insomniacgks.newmoviesandshows.fragments;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.insomniacgks.newmoviesandshows.R;
import com.insomniacgks.newmoviesandshows.adapters.MoviesRecyclerViewAdapter;
import com.insomniacgks.newmoviesandshows.adapters.ShowsRecyclerViewAdapter;
import com.insomniacgks.newmoviesandshows.data.Constants;
import com.insomniacgks.newmoviesandshows.data.FetchMovies;
import com.insomniacgks.newmoviesandshows.data.FetchShows;
import com.insomniacgks.newmoviesandshows.models.MovieModel;
import com.insomniacgks.newmoviesandshows.models.ShowModel;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public class SearchShowResultsFragment extends Fragment implements FetchShows.AsyncResponse {


    private String searchQuery;
    RecyclerView recyclerView;
    private ArrayList<ShowModel> shows;
    private boolean firstPageFlag;
    private int total_pages = 9999;
    private ShowsRecyclerViewAdapter myAdapter;
    private FetchShows fetchShows;
    String url;
    int page_count = 1;
    private View view;

    public SearchShowResultsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_show, container, false);
        view.findViewById(R.id.timepass).setVisibility(View.GONE);
        view.findViewById(R.id.blur_view).setVisibility(View.GONE);
        firstPageFlag = true;
        searchQuery = getArguments().getString("searchQuery");

        url = "https://api.themoviedb.org/3/search/tv?api_key=" + Constants.API_KEY + "&language=en-US&query=" + searchQuery + "&page=";
        recyclerView = view.findViewById(R.id.shows_recycler_view);
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (!recyclerView.canScrollVertically(1)) {
                    page_count++;
                    if (page_count <= total_pages) {
                        fetchShows = new FetchShows(url + page_count);
                        fetchShows.delegate = SearchShowResultsFragment.this;
                        fetchShows.execute();
                    }
                }
            }
        });
        fetchShows = new FetchShows(url + page_count);
        fetchShows.delegate = this;
        fetchShows.execute();
        return view;
    }

    @Override
    public void processFinish(ArrayList<ShowModel> output, int total_pages) {
        shows = output;
        if (output == null) {
            view.findViewById(R.id.hehe).setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
            return;
        }

        if (output.size() == 0) {
            view.findViewById(R.id.hehe).setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
            return;
        }

        if (firstPageFlag) {
            int columns;
            columns = 3;
            this.total_pages = total_pages;
            RecyclerView.LayoutManager layoutManager = new GridLayoutManager(getContext(), columns);
            myAdapter = new ShowsRecyclerViewAdapter(shows, getContext());
            this.recyclerView.setLayoutManager(layoutManager);
            this.recyclerView.setAdapter(myAdapter);
            firstPageFlag = false;
        } else {
            myAdapter.addMoreItems(shows);
            myAdapter.notifyDataSetChanged();
        }

    }
}
