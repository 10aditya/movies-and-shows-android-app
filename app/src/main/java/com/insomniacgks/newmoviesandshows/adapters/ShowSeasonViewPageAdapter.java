package com.insomniacgks.newmoviesandshows.adapters;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.insomniacgks.newmoviesandshows.fragments.EpisodeFragment;

import java.util.ArrayList;
import java.util.Locale;

public class ShowSeasonViewPageAdapter extends FragmentPagerAdapter {
    private ArrayList<EpisodeFragment> episodeFragments = new ArrayList<>();
    private int seasonNumber;
    private final int tabs;

    public ShowSeasonViewPageAdapter(FragmentManager fm, ArrayList<String[]> episodes, int tabs, int seasonNumber) {
        super(fm);
        this.tabs = tabs;
        this.seasonNumber = seasonNumber;
        for (String[] episode : episodes) {
            Bundle bundle = new Bundle();
            bundle.putStringArray("episode", episode);
            EpisodeFragment episodeFragment = new EpisodeFragment();
            episodeFragment.setArguments(bundle);
            this.episodeFragments.add(episodeFragment);
        }
    }

    @Nullable
    public CharSequence getPageTitle(int position) {
        return String.format(Locale.ENGLISH, "S%02dE%02d", this.seasonNumber, position + 1);
    }

    public Fragment getItem(int position) {
        return this.episodeFragments.get(position);
    }

    public int getCount() {
        return this.tabs;
    }
}
