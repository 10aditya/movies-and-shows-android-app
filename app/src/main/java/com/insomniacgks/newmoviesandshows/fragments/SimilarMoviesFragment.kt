package com.insomniacgks.newmoviesandshows.fragments


import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.preference.PreferenceManager
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.bumptech.glide.Glide
import com.bumptech.glide.Priority
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.bitmap.BitmapTransitionOptions
import com.bumptech.glide.request.RequestOptions
import com.insomniacgks.newmoviesandshows.R
import com.insomniacgks.newmoviesandshows.activities.MovieDetailActivity
import com.insomniacgks.newmoviesandshows.adapters.MoviesRecyclerViewAdapter
import com.insomniacgks.newmoviesandshows.backend.Constants
import com.insomniacgks.newmoviesandshows.backend.FetchMovies
import com.insomniacgks.newmoviesandshows.models.MovieModel
import java.util.ArrayList


/**
 * A simple [Fragment] subclass.
 *
 */
class SimilarMoviesFragment : Fragment(), FetchMovies.AsyncResponse {
    lateinit var v: View
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        v = inflater.inflate(R.layout.fragment_similar_movies, container, false)
        if (PreferenceManager.getDefaultSharedPreferences(this.context).getBoolean("dark_mode", false)) {
            (v.findViewById<View>(R.id.similar_movies_tv) as TextView).setTextColor(Color.WHITE)
            v.findViewById<View>(R.id.similar_movies_rl).setBackgroundColor(ContextCompat.getColor(this.context!!, R.color.black_theme_color))
        }
        val link = ("https://api.themoviedb.org/3/movie/"
                + MovieDetailActivity.movie.id.toString()
                + "/similar?api_key="
                + Constants.API_KEY
                + "&language=en-US&page=1")
        val fetchMovies = FetchMovies(link)
        fetchMovies.delegate = this@SimilarMoviesFragment
        fetchMovies.execute()
        return v
    }

    override fun processFinish(output: ArrayList<MovieModel>, total_pages: Int) {
        val layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        val recyclerViewAdapter = SimilarMoviesRecyclerViewAdapter(output, context!!)
        val recyclerView = v.findViewById<RecyclerView>(R.id.similar_movies_rv)
        recyclerView.layoutManager = layoutManager
        recyclerView.adapter = recyclerViewAdapter
    }

    inner class SimilarMoviesRecyclerViewAdapter(movies: ArrayList<MovieModel>, private val context: Context) : RecyclerView.Adapter<SimilarMoviesRecyclerViewAdapter.ViewHolder>() {
        private val mMovies = movies
        override fun onCreateViewHolder(p0: ViewGroup, p1: Int): ViewHolder {
            return ViewHolder(LayoutInflater.from(p0.context).inflate(R.layout.season_card_layout, p0, false))
        }

        override fun getItemCount(): Int {
            return mMovies.size
        }

        override fun onBindViewHolder(holder: ViewHolder, p1: Int) {
            val movie = mMovies.get(p1)
            holder.movieDate.text = movie.releaseDate
            holder.movieTitle.text = movie.title

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
                    .transition(BitmapTransitionOptions.withCrossFade())
                    .into(holder.moviePoster)
        }

        inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            internal val moviePoster = itemView.findViewById<ImageView>(R.id.season_poster)
            internal val movieTitle = itemView.findViewById<TextView>(R.id.season_number)
            internal val movieDate = itemView.findViewById<TextView>(R.id.season_air_date)

        }

    }
}


