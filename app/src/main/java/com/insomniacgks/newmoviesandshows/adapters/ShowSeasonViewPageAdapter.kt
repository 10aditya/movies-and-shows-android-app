package com.insomniacgks.newmoviesandshows.adapters

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter

import com.insomniacgks.newmoviesandshows.fragments.EpisodeFragment

import java.util.ArrayList
import java.util.Locale

class ShowSeasonViewPageAdapter(fm: FragmentManager, episodes: ArrayList<Array<String>>, private val tabs: Int, private val seasonNumber: Int) : FragmentPagerAdapter(fm) {
    private val episodeFragments = ArrayList<EpisodeFragment>()

    init {
        for (episode in episodes) {
            val bundle = Bundle()
            bundle.putStringArray("episode", episode)
            val episodeFragment = EpisodeFragment()
            episodeFragment.arguments = bundle
            this.episodeFragments.add(episodeFragment)
        }
    }

    override fun getPageTitle(position: Int): CharSequence? {
        return String.format(Locale.ENGLISH, "S%02dE%02d", this.seasonNumber, position + 1)
    }

    override fun getItem(position: Int): Fragment {
        return this.episodeFragments[position]
    }

    override fun getCount(): Int {
        return this.tabs
    }
}
