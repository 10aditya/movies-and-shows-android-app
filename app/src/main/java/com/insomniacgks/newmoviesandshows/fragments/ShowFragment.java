package com.insomniacgks.newmoviesandshows.fragments;


import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

import com.insomniacgks.newmoviesandshows.data.Constants;
import com.insomniacgks.newmoviesandshows.data.FetchShows;
import com.insomniacgks.newmoviesandshows.R;
import com.insomniacgks.newmoviesandshows.adapters.ShowsRecyclerViewAdapter;
import com.insomniacgks.newmoviesandshows.models.ShowModel;

import java.util.ArrayList;
import java.util.Objects;

/**
 * A simple {@link Fragment} subclass.
 */
public class ShowFragment extends Fragment
        implements NavigationView.OnNavigationItemSelectedListener, FetchShows.AsyncResponse {

    RelativeLayout layout;
    FloatingActionButton fab;
    NavigationView tpview;
    private RecyclerView recyclerView;
    boolean firstPageFlag = true;
    String currentURL;
    FrameLayout blurView;
    CoordinatorLayout coordinatorLayout;
    ArrayList<ShowModel> shows;
    FetchShows fetchShows;
    private ShowsRecyclerViewAdapter myAdapter;
    int page_count = 1;
    private int total_pages = 999999;

    public ShowFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_show, container, false);

        fab = view.findViewById(R.id.FAB);
        tpview = view.findViewById(R.id.timepassview);

        tpview.setNavigationItemSelectedListener(this);
        tpview.getMenu().getItem(0).setChecked(true);

        layout = view.findViewById(R.id.timepass);
        recyclerView = view.findViewById(R.id.shows_recycler_view);

        currentURL = "https://api.themoviedb.org/3/tv/on_the_air?api_key=" + Constants.API_KEY + "&language=en-US&page=";
        coordinatorLayout = view.findViewById(R.id.coordinator_layout);

        final Snackbar snackbar = Snackbar.make(coordinatorLayout, "Loading TV Shows...", Snackbar.LENGTH_SHORT);
        snackbar.show();
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (!recyclerView.canScrollVertically(1)) {
                    page_count++;
                    if (page_count <= total_pages) {
                        fetchShows = new FetchShows(currentURL + page_count);
                        fetchShows.delegate = ShowFragment.this;
                        fetchShows.execute();
                        snackbar.show();
                    }
                }
            }
        });

        blurView = view.findViewById(R.id.blur_view);

        shows = new ArrayList<>();

        fetchShows = new FetchShows(currentURL + page_count);
        fetchShows.delegate = ShowFragment.this;
        fetchShows.execute();

        blurView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ObjectAnimator animation = ObjectAnimator.ofFloat(layout, "translationX", -tpview.getWidth());
                animation.setDuration(125);
                animation.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        tpview.setVisibility(View.INVISIBLE);
                        fab.setImageDrawable(ContextCompat.getDrawable(Objects.requireNonNull(getContext()), R.drawable.ic_chevron_right_black_24dp));
                    }
                });
                animation.start();
                blurView.setVisibility(View.INVISIBLE);
            }
        });

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (tpview.getVisibility() == View.INVISIBLE) {
                    tpview.setVisibility(View.VISIBLE);
                    ObjectAnimator animation = ObjectAnimator.ofFloat(layout, "translationX", 0);
                    animation.setDuration(125);
                    animation.start();
                    animation.addListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            super.onAnimationEnd(animation);
                            fab.setImageDrawable(ContextCompat.getDrawable(Objects.requireNonNull(getContext()), R.drawable.ic_chevron_left_black_24dp));
                        }
                    });
                    blurView.setVisibility(View.VISIBLE);
                } else {
                    ObjectAnimator animation = ObjectAnimator.ofFloat(layout, "translationX", -tpview.getWidth());
                    animation.setDuration(125);
                    animation.addListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            super.onAnimationEnd(animation);
                            tpview.setVisibility(View.INVISIBLE);
                            fab.setImageDrawable(ContextCompat.getDrawable(Objects.requireNonNull(getContext()), R.drawable.ic_chevron_right_black_24dp));
                        }
                    });
                    animation.start();
                    blurView.setVisibility(View.INVISIBLE);
                }
                /*Animation fadeAnimation = AnimationUtils.loadAnimation(this, R.anim.text_view_animation);
                passwordField.startAnimation(fadeAnimation);*/
            }
        });

        return view;
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case R.id.shows_on_the_air: {
                if (!item.isChecked()) {
                    page_count = 1;
                    firstPageFlag = true;
                    currentURL = "https://api.themoviedb.org/3/tv/on_the_air?api_key=" + Constants.API_KEY + "&language=en-US&page=";
                    fetchShows = new FetchShows(currentURL + page_count);
                    fetchShows.delegate = ShowFragment.this;
                    fetchShows.execute();
                }
                break;
            }
            case R.id.shows_airing_today: {
                if (!item.isChecked()) {
                    page_count = 1;
                    firstPageFlag = true;
                    currentURL = "https://api.themoviedb.org/3/tv/airing_today?api_key=" + Constants.API_KEY + "&language=en-US&page=";
                    fetchShows = new FetchShows(currentURL + page_count);
                    fetchShows.delegate = ShowFragment.this;
                    fetchShows.execute();
                }
                break;
            }
            case R.id.show_top_rated: {
                if (!item.isChecked()) {
                    page_count = 1;
                    firstPageFlag = true;
                    currentURL = "https://api.themoviedb.org/3/tv/top_rated?api_key=" + Constants.API_KEY + "&language=en-US&page=";
                    fetchShows = new FetchShows(currentURL + page_count);
                    fetchShows.delegate = ShowFragment.this;
                    fetchShows.execute();
                }
                break;
            }
            case R.id.shows_popular: {
                if (!item.isChecked()) {
                    page_count = 1;
                    firstPageFlag = true;
                    currentURL = "https://api.themoviedb.org/3/tv/popular?api_key=" + Constants.API_KEY + "&language=en-US&page=";
                    fetchShows = new FetchShows(currentURL + page_count);
                    fetchShows.delegate = ShowFragment.this;
                    fetchShows.execute();
                }
                break;
            }
            default: {
                if (!item.isChecked()) {
                    page_count = 1;
                    firstPageFlag = true;
                    currentURL = "https://api.themoviedb.org/3/discover/tv?api_key="
                            + Constants.API_KEY
                            + "&language=en-US&sort_by=popularity.desc&with_genres="
                            + Constants.genreID(item.getTitle().toString())
                            + "&with_original_language=en&page=";
                    fetchShows = new FetchShows(currentURL + page_count);
                    fetchShows.delegate = ShowFragment.this;
                    fetchShows.execute();
                }
            }
        }

        ObjectAnimator animation = ObjectAnimator.ofFloat(layout, "translationX", -tpview.getWidth());
        animation.setDuration(125);
        animation.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                tpview.setVisibility(View.INVISIBLE);
                fab.setImageDrawable(ContextCompat.getDrawable(Objects.requireNonNull(getContext()), R.drawable.ic_chevron_right_black_24dp));
            }
        });
        animation.start();
        blurView.setVisibility(View.INVISIBLE);

        return true;
    }


    @Override
    public void processFinish(ArrayList<ShowModel> output, int total_pages) {
        shows = output;
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

    public void changeFabPosition() {
        ObjectAnimator animation = ObjectAnimator.ofFloat(layout, "translationX", -tpview.getWidth());
        animation.setDuration(300);
        animation.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                tpview.setVisibility(View.INVISIBLE);
            }
        });
        animation.start();
        fab.setImageDrawable(ContextCompat.getDrawable(Objects.requireNonNull(getContext()), R.drawable.ic_chevron_right_black_24dp));
    }
}
