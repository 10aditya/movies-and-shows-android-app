package com.insomniacgks.newmoviesandshows.fragments


import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.preference.PreferenceManager
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import android.widget.TextView

import com.insomniacgks.newmoviesandshows.R
import com.insomniacgks.newmoviesandshows.activities.MovieDetailActivity
import com.insomniacgks.newmoviesandshows.data.GetVideos

import java.util.Objects


/**
 * A simple [Fragment] subclass.
 */
class VideoFragment : Fragment() {


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val v = inflater.inflate(R.layout.fragment_video, container, false)
        if (PreferenceManager.getDefaultSharedPreferences(this.context).getBoolean("dark_mode", false)) {
            v.findViewById<View>(R.id.movie_videos_rl).setBackgroundColor(ContextCompat.getColor(Objects.requireNonNull<Context>(this.context), R.color.black_theme_color))
            (v.findViewById<View>(R.id.movie_videos_tv) as TextView).setTextColor(Color.WHITE)
        }
        val videos_rv = v.findViewById<RecyclerView>(R.id.movie_videos_rv)
        GetVideos(this.context!!, MovieDetailActivity.movie.id, videos_rv, "movie", v.findViewById<View>(R.id.movie_videos_rl) as RelativeLayout).execute()
        return v
    }

}// Required empty public constructor
