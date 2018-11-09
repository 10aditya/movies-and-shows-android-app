package com.insomniacgks.newmoviesandshows.activities

import android.graphics.Bitmap
import android.graphics.Color
import android.os.Bundle
import android.preference.PreferenceManager
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.support.v7.graphics.Palette
import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import com.bumptech.glide.Glide
import com.bumptech.glide.Priority
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.load.resource.bitmap.BitmapTransitionOptions.withCrossFade
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.Target
import com.insomniacgks.newmoviesandshows.R
import com.insomniacgks.newmoviesandshows.data.Constants
import com.insomniacgks.newmoviesandshows.fragments.*
import com.insomniacgks.newmoviesandshows.models.MovieModel

class MovieDetailActivity : AppCompatActivity() {

    private var movieTitle: TextView? = null
    private var movieRatings: TextView? = null
    private var movieOverview: TextView? = null
    private var movieReleaseDate: TextView? = null
    private var movieGenres: TextView? = null
    private var moviePoster: ImageView? = null
    private var movieBackdrop: ImageView? = null
    private val images_rv: RecyclerView? = null
    private val cast_rv: RecyclerView? = null
    private val crew_rv: RecyclerView? = null
    private val reviews_rv: RecyclerView? = null
    private val videos_rv: RecyclerView? = null
    private val reviews_rl: RelativeLayout? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_movie_detail)
        movie = intent!!.extras!!.getSerializable("movie") as MovieModel
        initializeViews()

        val sharedPreference = PreferenceManager.getDefaultSharedPreferences(this)
        val isDarkModeOn = sharedPreference.getBoolean("dark_mode", false)

        if(isDarkModeOn){
            turnOnDarkMode()
        }

        movieTitle!!.text = movie.title
        movieOverview!!.text = movie.overview
        movieRatings!!.text = movie.rating.toString()
        movieReleaseDate!!.text = movie.releaseDate
        val genres = movie.genresID
        val genresString = StringBuilder("")
        var i: Int
        i = 0
        while (i < genres.size - 1) {
            genresString.append(Constants.genreString(genres[i])).append(" | ")
            i++
        }
        genresString.append(Constants.genreString(genres[i]))
        movieGenres!!.text = genresString.toString()

        val options = RequestOptions()
                .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                .priority(Priority.HIGH)

        Glide.with(this)
                .asBitmap()
                .load(movie.posterURL)
                .apply(options)
                .apply(RequestOptions()
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .apply(RequestOptions()
                                .placeholder(getDrawable(R.drawable.gradient))))
                .transition(withCrossFade())
                .into(moviePoster!!)

        Glide.with(this)
                .asBitmap()
                .load(movie.backdropURL)
                .apply(options)
                .apply(RequestOptions()
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .apply(RequestOptions()
                                .placeholder(getDrawable(R.drawable.gradient))))
                .listener(object : RequestListener<Bitmap> {
                    override fun onLoadFailed(e: GlideException?, model: Any, target: Target<Bitmap>, isFirstResource: Boolean): Boolean {
                        return false
                    }

                    override fun onResourceReady(resource: Bitmap, model: Any, target: Target<Bitmap>, dataSource: DataSource, isFirstResource: Boolean): Boolean {
                        Palette.from(resource).generate { palette ->
                            val swatch = palette!!.vibrantSwatch
                            if (swatch != null) {
                                window.statusBarColor = swatch.rgb
                            }
                        }

                        return false
                    }
                })
                .transition(withCrossFade())
                .into(movieBackdrop!!)

        val fragmentTransaction = supportFragmentManager.beginTransaction()
        fragmentTransaction.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out)
        fragmentTransaction.replace(R.id.movie_images_fl, ImageFragment())
        fragmentTransaction.replace(R.id.movie_cast_fl, CastFragment())
        fragmentTransaction.replace(R.id.movie_videos_fl, VideoFragment())
        fragmentTransaction.replace(R.id.movie_crew_fl, CrewFragment())
        fragmentTransaction.replace(R.id.movie_reviews_fl, ReviewFragment())
        fragmentTransaction.commitAllowingStateLoss()

        /*
        new GetImages(this, movie.getId(), images_rv, "movie", (RelativeLayout) findViewById(R.id.movie_images_rl)).execute();
        new GetCast(this, movie.getId(), cast_rv, "movie", (RelativeLayout) findViewById(R.id.movie_cast_rl)).execute();
        new GetCrew(this, movie.getId(), crew_rv, "movie",(RelativeLayout)findViewById(R.id.movie_crew_rl)).execute();
        new GetReviews(this, movie.getId(), reviews_rv, reviews_rl, "movie").execute();
        new GetVideos(this, movie.getId(), videos_rv, "movie",(RelativeLayout)findViewById(R.id.movie_videos_rl)).execute();
*/
    }

    private fun initializeViews() {
        movieTitle = findViewById(R.id.movie_title)
        movieReleaseDate = findViewById(R.id.movie_release_date)
        movieRatings = findViewById(R.id.movie_ratings)
        movieGenres = findViewById(R.id.movie_genres)
        moviePoster = findViewById(R.id.movie_poster)
        movieBackdrop = findViewById(R.id.movie_backdrop)
        movieOverview = findViewById(R.id.show_overview)
        /*
        images_rv = findViewById(R.id.movie_images_rv);
        cast_rv = findViewById(R.id.movie_cast_rv);
        crew_rv = findViewById(R.id.movie_crew_rv);
        reviews_rv = findViewById(R.id.movie_reviews_rv);
        reviews_rl = findViewById(R.id.movie_reviews_rl);
        videos_rv = findViewById(R.id.movie_videos_rv);*/
    }

    private fun turnOnDarkMode(){
        findViewById<FrameLayout>(R.id.movie_images_fl).setBackgroundColor(ContextCompat.getColor(this,R.color.black_theme_color))
        findViewById<FrameLayout>(R.id.movie_videos_fl).setBackgroundColor(ContextCompat.getColor(this,R.color.black_theme_color))
        findViewById<FrameLayout>(R.id.movie_cast_fl).setBackgroundColor(ContextCompat.getColor(this,R.color.black_theme_color))
        findViewById<FrameLayout>(R.id.movie_crew_fl).setBackgroundColor(ContextCompat.getColor(this,R.color.black_theme_color))
        findViewById<FrameLayout>(R.id.movie_reviews_fl).setBackgroundColor(ContextCompat.getColor(this,R.color.black_theme_color))
        findViewById<ImageView>(R.id.wave_layout).setImageDrawable(getDrawable(R.drawable.wave_3))
        findViewById<View>(R.id.just_a_view).setBackgroundColor(ContextCompat.getColor(this,R.color.black_theme_color))
        findViewById<ImageView>(R.id.rating_view).setImageDrawable(getDrawable(R.drawable.ic_star_white_24dp))
        findViewById<RelativeLayout>(R.id.show_overview_rl).setBackgroundColor(ContextCompat.getColor(this,R.color.black_theme_color))
        findViewById<TextView>(R.id.show_overview_tv).setTextColor(Color.WHITE)
        window.statusBarColor = ContextCompat.getColor(this,R.color.colorPrimaryDarkDarkTheme)
        movieTitle!!.setTextColor(Color.WHITE)
        movieReleaseDate!!.setTextColor(Color.WHITE)
        movieRatings!!.setTextColor(Color.WHITE)
        movieGenres!!.setTextColor(Color.WHITE)
        movieOverview!!.setTextColor(Color.WHITE)
    }

    companion object {
        lateinit var movie: MovieModel
    }

}
