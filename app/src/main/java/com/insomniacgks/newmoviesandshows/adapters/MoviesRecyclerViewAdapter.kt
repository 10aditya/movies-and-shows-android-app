package com.insomniacgks.newmoviesandshows.adapters

import android.app.Activity
import android.app.ActivityOptions
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatDelegate
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.bumptech.glide.Priority
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.bitmap.BitmapTransitionOptions.withCrossFade
import com.bumptech.glide.request.RequestOptions
import com.insomniacgks.newmoviesandshows.R
import com.insomniacgks.newmoviesandshows.activities.MainActivity
import com.insomniacgks.newmoviesandshows.activities.MovieDetailActivity
import com.insomniacgks.newmoviesandshows.models.MovieModel
import java.util.*

class MoviesRecyclerViewAdapter
//    private InterstitialAd interstitialAd;

(movies: ArrayList<MovieModel>, private val context: Context) : RecyclerView.Adapter<MoviesRecyclerViewAdapter.ViewHolder>() {
    init {
        mMovies = movies
        //this.interstitialAd = new InterstitialAd(context);
        //this.interstitialAd.setAdUnitId(Constants.ADMOB_MOVIE_AD_ID);
        //this.interstitialAd.loadAd(new Builder().addTestDevice("872C1F2EAF7C1188C2E85D0A61C9E668").build());
    }

    fun addMoreItems(moreMovies: ArrayList<MovieModel>) {
        mMovies!!.addAll(moreMovies)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true)
        return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.movie_show_card_layout, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val movie = mMovies!![position]
        if (MainActivity.isDarkMode) {
            holder.titleBackground.setImageResource(R.drawable.wave_3)
            holder.title.setTextColor(Color.WHITE)
        }
        if (movie.posterURL != "null" && movie.posterURL!="") {
            holder.title.text = movie.title
            holder.title.isSelected = true
            val options = RequestOptions()
                    .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                    .priority(Priority.HIGH)

            Glide.with(this.context)
                    .asBitmap()
                    .load(movie.posterURL)
                    .apply(options)
                    .apply(RequestOptions()
                            .diskCacheStrategy(DiskCacheStrategy.ALL)
                            .apply(RequestOptions()
                                    .placeholder(ContextCompat.getDrawable(this.context, R.drawable.gradient))))
                    .transition(withCrossFade())
                    .into(holder.moviePoster)
        }
    }

    override fun getItemCount(): Int {
        return if (mMovies == null) 0 else mMovies!!.size
    }

    inner class ViewHolder internal constructor(itemView: View) : android.support.v7.widget.RecyclerView.ViewHolder(itemView), View.OnClickListener {
        internal var moviePoster: ImageView = itemView.findViewById(R.id.poster)
        internal var titleBackground: ImageView = itemView.findViewById(R.id.title_background)
        internal var title: TextView = itemView.findViewById(R.id.movie_name)

        init {
            itemView.setOnClickListener(this)
        }

        override fun onClick(view: View) {
            val clickedItem = MoviesRecyclerViewAdapter.mMovies!![adapterPosition]
            val i = Intent(this@MoviesRecyclerViewAdapter.context, MovieDetailActivity::class.java)
            //       final ActivityOptions options = ActivityOptions.makeScaleUpAnimation(view, 0, 0, view.getWidth(), view.getHeight());
            val options = ActivityOptions.makeSceneTransitionAnimation(context as Activity,
                    view.findViewById(R.id.poster),
                    "testing")
            val bundle = Bundle()
            bundle.putSerializable("movie", clickedItem)
            i.putExtras(bundle)

            this@MoviesRecyclerViewAdapter.context.startActivity(i, options.toBundle())
        }

    }

    companion object {
        private var mMovies: ArrayList<MovieModel>? = null
    }
}
