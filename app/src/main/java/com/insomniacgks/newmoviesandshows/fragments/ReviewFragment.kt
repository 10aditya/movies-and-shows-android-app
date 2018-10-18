package com.insomniacgks.newmoviesandshows.fragments


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
import com.insomniacgks.newmoviesandshows.data.GetCast
import com.insomniacgks.newmoviesandshows.data.GetReviews

import com.insomniacgks.newmoviesandshows.activities.MovieDetailActivity.Companion.movie


/**
 * A simple [Fragment] subclass.
 */
class ReviewFragment : Fragment() {


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val v = inflater.inflate(R.layout.fragment_review, container, false)
        if(PreferenceManager.getDefaultSharedPreferences(this.context).getBoolean("dark_mode", false)){
            v.findViewById<RelativeLayout>(R.id.movie_reviews_rl).setBackgroundColor(ContextCompat.getColor(this.context!!, R.color.black_theme_color))
            v.findViewById<TextView>(R.id.movie_reviews_tv).setTextColor(Color.WHITE)
        }
        GetReviews(context, MovieDetailActivity.movie.id, v.findViewById<View>(R.id.movie_reviews_rv) as RecyclerView, v.findViewById<View>(R.id.movie_reviews_rl) as RelativeLayout, "movie").execute()
        return v
    }

}// Required empty public constructor
