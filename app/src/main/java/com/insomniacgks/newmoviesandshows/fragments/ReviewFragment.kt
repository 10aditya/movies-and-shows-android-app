package com.insomniacgks.newmoviesandshows.fragments


import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.preference.PreferenceManager
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.support.v7.widget.RecyclerView
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import android.widget.TextView
import com.insomniacgks.newmoviesandshows.R
import com.insomniacgks.newmoviesandshows.activities.MovieDetailActivity
import com.insomniacgks.newmoviesandshows.backend.GetReviews


/**
 * A simple [Fragment] subclass.
 */
class ReviewFragment : Fragment() {

    companion object {
        @SuppressLint("StaticFieldLeak")
        lateinit var c:Context
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val v = inflater.inflate(R.layout.fragment_review, container, false)
        c=context!!
        if(PreferenceManager.getDefaultSharedPreferences(this.context).getBoolean("dark_mode", false)){
            v.findViewById<RelativeLayout>(R.id.movie_reviews_rl).setBackgroundColor(ContextCompat.getColor(this.context!!, R.color.black_theme_color))
            v.findViewById<TextView>(R.id.movie_reviews_tv).setTextColor(Color.WHITE)
        }
        GetReviews(context!!, MovieDetailActivity.movie.id, v.findViewById<DamnClass>(R.id.movie_reviews_rv), v.findViewById<RelativeLayout>(R.id.movie_reviews_rl), "movie").execute()
        return v
    }

    @SuppressLint("ViewConstructor")
    class DamnClass: RecyclerView(ReviewFragment.c) {
        override fun onMeasure(widthSpec: Int, heightSpec: Int) {
            val heightSpe = MeasureSpec.makeMeasureSpec(
                    TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                            300f,
                            resources.displayMetrics).toInt(),
                    MeasureSpec.AT_MOST)
            super.onMeasure(widthSpec, heightSpe)
        }
    }

}// Required empty public constructor
