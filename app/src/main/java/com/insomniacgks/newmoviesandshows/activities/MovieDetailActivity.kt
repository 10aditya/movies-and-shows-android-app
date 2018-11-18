package com.insomniacgks.newmoviesandshows.activities

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.graphics.Color
import android.os.AsyncTask
import android.os.Bundle
import android.preference.PreferenceManager
import android.support.design.widget.FloatingActionButton
import android.support.v4.content.ContextCompat
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.support.v7.graphics.Palette
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.widget.*
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
import com.insomniacgks.newmoviesandshows.backend.Constants
import com.insomniacgks.newmoviesandshows.fragments.*
import com.insomniacgks.newmoviesandshows.models.MovieModel
import com.xw.repo.BubbleSeekBar
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.URL
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*
import javax.net.ssl.HttpsURLConnection

class MovieDetailActivity : AppCompatActivity() {

    private var ratingButton: FloatingActionButton? = null
    private var movieTitle: TextView? = null
    private var movieRatings: TextView? = null
    private var movieOverview: TextView? = null
    private var movieReleaseDate: TextView? = null
    private var movieGenres: TextView? = null
    private var moviePoster: ImageView? = null
    private var movieBackdrop: ImageView? = null
    private var ratingBar: BubbleSeekBar? = null
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

        ratingButton!!.bringToFront()
        ratingButton!!.setOnClickListener {
            createRatingDialog()
        }

        /*
        new GetImages(this, movie.getId(), images_rv, "movie", (RelativeLayout) findViewById(R.id.movie_images_rl)).execute();
        new GetCast(this, movie.getId(), cast_rv, "movie", (RelativeLayout) findViewById(R.id.movie_cast_rl)).execute();
        new GetCrew(this, movie.getId(), crew_rv, "movie",(RelativeLayout)findViewById(R.id.movie_crew_rl)).execute();
        new GetReviews(this, movie.getId(), reviews_rv, reviews_rl, "movie").execute();
        new GetVideos(this, movie.getId(), videos_rv, "movie",(RelativeLayout)findViewById(R.id.movie_videos_rl)).execute();
*/
    }

    @SuppressLint("InflateParams")
    private fun createRatingDialog() {
        val alertDialog: AlertDialog.Builder = AlertDialog.Builder(this)
        val view: View = LayoutInflater.from(this).inflate(R.layout.rating_dialog_layout, null, false)
        ratingBar = view.findViewById(R.id.rating_bar)
        val rateTV = view.findViewById<TextView>(R.id.rate_tv)
        rateTV.text = String.format("Rate %s", movie.title)
        alertDialog.setView(view)

        alertDialog.setPositiveButton("Rate") { dialog, _ ->
            val sp = PreferenceManager.getDefaultSharedPreferences(this@MovieDetailActivity)
            if (!sp.getBoolean("success", false)) {
                val status = createGuestSession()
                if (!status) {
                    dialog.dismiss()
                    return@setPositiveButton
                }
            }
            val expireTime = sp.getString("expires_at", "-1")
            if (expireTime == "-1") {
                Toast.makeText(this@MovieDetailActivity, "Couldn't complete process! Try Again.", Toast.LENGTH_LONG).show()
                val spe = sp.edit()
                spe.putBoolean("success", false)
                spe.apply()
                return@setPositiveButton
            }
            val currentUTCTime = getCurrentUTCTime()
            val sdf = SimpleDateFormat("yyyy-MM-dd hh:mm:ss", Locale.US)
            val date1 = sdf.parse(currentUTCTime)
            val date2 = sdf.parse(expireTime)
            if (date1 < date2) {
                val status = createGuestSession()
                if (!status) {
                    dialog.dismiss()
                    return@setPositiveButton
                }
            }
            val guestSessionId = sp.getString("guest_session_id", "-1")
            if (guestSessionId == "-1") {
                Toast.makeText(this@MovieDetailActivity, "Couldn't complete process! Try Again.", Toast.LENGTH_LONG).show()
                val spe = sp.edit()
                spe.putBoolean("success", false)
                spe.apply()
                return@setPositiveButton
            }
            RateMovie(this@MovieDetailActivity).execute(guestSessionId, String.format("%.1f", ratingBar!!.progressFloat))
        }

        alertDialog.setNegativeButton("Cancel") { _, _ ->

        }
        val dialog = alertDialog.create()
        dialog.show()
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).isEnabled = false
        ratingBar!!.onProgressChangedListener = object : BubbleSeekBar.OnProgressChangedListener {
            override fun onProgressChanged(bubbleSeekBar: BubbleSeekBar?, progress: Int, progressFloat: Float, fromUser: Boolean) {
                dialog.getButton(AlertDialog.BUTTON_POSITIVE).isEnabled = true
                val color: Int = when {
                    progressFloat <= 2.8f -> ContextCompat.getColor(this@MovieDetailActivity, android.R.color.holo_red_dark)
                    progressFloat <= 4.6f -> ContextCompat.getColor(this@MovieDetailActivity, android.R.color.holo_red_light)
                    progressFloat <= 6.4f -> ContextCompat.getColor(this@MovieDetailActivity, R.color.neutral)
                    progressFloat <= 8.2f -> ContextCompat.getColor(this@MovieDetailActivity, android.R.color.holo_green_light)
                    else -> ContextCompat.getColor(this@MovieDetailActivity, android.R.color.holo_green_dark)
                }
                dialog.getButton(AlertDialog.BUTTON_POSITIVE).text = String.format("Rate %.1f/10", progressFloat)
                ratingBar!!.setSecondTrackColor(color)
                ratingBar!!.setBubbleColor(color)
                ratingBar!!.setThumbColor(color)

            }

            override fun getProgressOnActionUp(bubbleSeekBar: BubbleSeekBar?, progress: Int, progressFloat: Float) {

            }

            override fun getProgressOnFinally(bubbleSeekBar: BubbleSeekBar?, progress: Int, progressFloat: Float, fromUser: Boolean) {

            }
        }


    }

    private fun createGuestSession(): Boolean {
        val createGuestSession = GuestSession(this@MovieDetailActivity)
        createGuestSession.execute()
        while (createGuestSession.status != AsyncTask.Status.FINISHED) {
        }
        val sp = PreferenceManager.getDefaultSharedPreferences(this@MovieDetailActivity)
        val status = sp.getBoolean("success", false)
        if (!status) {
            Toast.makeText(this@MovieDetailActivity, "Failed to connect! Try Again.", Toast.LENGTH_LONG).show()
            return false
        }
        return true
    }

    private fun initializeViews() {
        ratingButton = findViewById(R.id.rating_button)
        movieTitle = findViewById(R.id.movie_title)
        movieReleaseDate = findViewById(R.id.movie_release_date)
        movieRatings = findViewById(R.id.movie_ratings)
        movieGenres = findViewById(R.id.movie_genres)
        moviePoster = findViewById(R.id.movie_poster)
        movieBackdrop = findViewById(R.id.movie_backdrop)
        movieOverview = findViewById(R.id.show_overview)

        /*images_rv = findViewById(R.id.movie_images_rv);
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

    @SuppressLint("StaticFieldLeak")
    internal inner class GuestSession(var context: Context) : AsyncTask<Void, Void, Void>() {

        override fun doInBackground(vararg params: Void?): Void? {
            val link = URL("https://api.themoviedb.org/3/authentication/guest_session/new?api_key=${Constants.API_KEY}")
            val client = link.openConnection() as HttpsURLConnection
            client.requestMethod = "GET"
            client.connect()
            val responseCode = client.responseCode
            val sharedPreferences: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
            val spe: SharedPreferences.Editor = sharedPreferences.edit()
            if (responseCode != 200) {
                spe.putBoolean("success", false)
            }
            val reader = BufferedReader(InputStreamReader(client.inputStream))
            val stringBuilder = StringBuilder()
            while (true) {
                stringBuilder.append(reader.readLine() ?: break)
            }
            val jsonString = stringBuilder.toString()
            val jsonObject = JSONObject(jsonString)
            if (!jsonObject.getBoolean("success")) {
                spe.putBoolean("success", false)
                spe.apply()
                return null
            }
            spe.putBoolean("success", true)
            spe.putString("guest_session_id", jsonObject.getString("guest_session_id"))
            spe.putString("expires_at", jsonObject.getString("expires_at").substring(0, 19))
            spe.apply()
            return null
        }
    }

    private fun getCurrentUTCTime(): String {
        val df = DateFormat.getDateTimeInstance()
        df.timeZone = TimeZone.getTimeZone("utc")
        return df.format(Date())
    }

    @SuppressLint("StaticFieldLeak")
    internal inner class RateMovie(var context: Context) : AsyncTask<String, Void, Void>() {
        override fun doInBackground(vararg params: String?): Void? {
            val link = URL("https://api.themoviedb.org/3/movie/" +
                    "${MovieDetailActivity.movie.id}" +
                    "/rating?api_key=${Constants.API_KEY}" +
                    "&guest_session_id=${params[0]}")
            val client = link.openConnection() as HttpsURLConnection
            client.requestMethod = "POST"
            client.setRequestProperty("Content-Type", "application/json;charset=utf-8")
            client.connect()
            if (client.responseCode != 200) {
                Toast.makeText(context, "Connection Failed! Try Again.", Toast.LENGTH_LONG).show()
                return null
            }
            val requestBody = "{\"value\": ${params[1]}"
            val outputStream = client.outputStream
            outputStream.write(requestBody.toByteArray(Charsets.UTF_8))
            outputStream.close()
            client.disconnect()
            Toast.makeText(context, "Rating successfully added!", Toast.LENGTH_SHORT).show()
            return null
        }
    }
}
