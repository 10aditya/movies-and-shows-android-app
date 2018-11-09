package com.insomniacgks.newmoviesandshows.activities

import android.annotation.SuppressLint
import android.app.ActivityOptions
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.os.AsyncTask
import android.os.Bundle
import android.preference.PreferenceManager
import android.support.constraint.ConstraintLayout
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.support.v7.graphics.Palette
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
import com.insomniacgks.newmoviesandshows.data.*
import com.insomniacgks.newmoviesandshows.models.ShowModel
import eightbitlab.com.blurview.BlurView
import org.json.JSONException
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import java.util.*

class ShowDetailActivity : AppCompatActivity() {

    private lateinit var show: ShowModel
    private lateinit var name: TextView
    private lateinit var rating: TextView
    private lateinit var releaseYear: TextView
    private lateinit var homepage: TextView
    private lateinit var genres: TextView
    private lateinit var networks: TextView
    private lateinit var poster: ImageView
    private lateinit var backdrop: ImageView
    private lateinit var blurView: BlurView
    private var showOverview: TextView? = null
    private var seasonsRV: RecyclerView? = null
    private var imagesRV: RecyclerView? = null
    private var castRV: RecyclerView? = null
    private var crewRV: RecyclerView? = null
    private var reviewsRV: RecyclerView? = null
    private var videosRV: RecyclerView? = null
    private var reviewsRL: RelativeLayout? = null
    private var darkTheme: Boolean = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)
        darkTheme = sharedPreferences.getBoolean("dark_mode", false)
        setContentView(R.layout.activity_show_detail)
        show = intent.extras!!.getSerializable("show") as ShowModel
        initializeViews()
        if(PreferenceManager.getDefaultSharedPreferences(this).getBoolean("dark_mode", false)) turnOnDarkMode()
        FetchShowDetails().execute()
        name.text = show.name
        rating.text = show.rating
        releaseYear.text = show.first_air_date.substring(0, 4)
        val genres = StringBuilder("")
        val genreeIDs = show.genres
        for (i in 0 until genreeIDs.size - 1) {
            genres.append(Constants.genreString(genreeIDs[i])).append(" | ")
        }
        if (genreeIDs.size != 0)
            genres.append(Constants.genreString(genreeIDs[genreeIDs.size - 1]))
        this.genres.text = genres.toString()
        val options = RequestOptions()
                .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                .priority(Priority.HIGH)

        Glide.with(this)
                .asBitmap()
                .load(show.poster_url)
                .apply(options)
                .apply(RequestOptions()
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .apply(RequestOptions()
                                .placeholder(ContextCompat.getDrawable(this, R.drawable.gradient))))
                .transition(withCrossFade())
                .into(poster)

        Glide.with(this)
                .asBitmap()
                .load(show.backdrop_url)
                .apply(options)
                .apply(RequestOptions()
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .apply(RequestOptions()
                                .placeholder(ContextCompat.getDrawable(this, R.drawable.gradient))))
                .transition(withCrossFade())
                .listener(object : RequestListener<Bitmap> {
                    override fun onLoadFailed(e: GlideException?, model: Any, target: Target<Bitmap>, isFirstResource: Boolean): Boolean {
                        return false
                    }

                    override fun onResourceReady(resource: Bitmap, model: Any, target: Target<Bitmap>, dataSource: DataSource, isFirstResource: Boolean): Boolean {
                        blurView.setupWith(backdrop).blurRadius(8.0f)
                        Palette.from(resource).generate { palette ->
                            val swatchD = palette!!.darkMutedSwatch
                            if (swatchD != null) {
                                window.statusBarColor = swatchD.rgb
                            }/*
                                if (swatch_d != null) {
                                    name.setTextColor(swatch_d.getRgb());
                                    rating.setTextColor(swatch_d.getRgb());
                                    releaseYear.setTextColor(swatch_d.getRgb());
                                    networks.setTextColor(swatch_d.getRgb());
                                    ShowDetailActivity.this.genres.setTextColor(swatch_d.getRgb());
                                }*/
                        }
                        return false
                    }
                })
                .into(backdrop)
        showOverview!!.text = show.overview

        GetImages(this, show.id, this.imagesRV!!, "tv", findViewById<View>(R.id.show_images_rl) as RelativeLayout).execute()
        GetCast(this, show.id, this.castRV!!, "tv", findViewById<View>(R.id.show_cast_rl) as RelativeLayout).execute()
        GetCrew(this, show.id, this.crewRV!!, "tv", findViewById<View>(R.id.show_crew_rl) as RelativeLayout).execute()
        GetReviews(this, show.id, this.reviewsRV!!, reviewsRL!!, "tv").execute()
        GetVideos(this, show.id, this.videosRV!!, "tv", findViewById<View>(R.id.show_videos_rl) as RelativeLayout).execute()


    }

    private fun turnOnDarkMode() {
        findViewById<View>(R.id.show_overview_rl).setBackgroundColor(ContextCompat.getColor(this, R.color.black_theme_color))
        findViewById<View>(R.id.show_seasons_rl).setBackgroundColor(ContextCompat.getColor(this, R.color.black_theme_color))
        findViewById<View>(R.id.show_images_rl).setBackgroundColor(ContextCompat.getColor(this, R.color.black_theme_color))
        findViewById<View>(R.id.show_videos_rl).setBackgroundColor(ContextCompat.getColor(this, R.color.black_theme_color))
        findViewById<View>(R.id.show_cast_rl).setBackgroundColor(ContextCompat.getColor(this, R.color.black_theme_color))
        findViewById<View>(R.id.show_reviews_rl).setBackgroundColor(ContextCompat.getColor(this, R.color.black_theme_color))
        findViewById<View>(R.id.show_crew_rl).setBackgroundColor(ContextCompat.getColor(this, R.color.black_theme_color))
        findViewById<TextView>(R.id.show_reviews_tv).setTextColor(Color.WHITE)
        findViewById<TextView>(R.id.show_crew_tv).setTextColor(Color.WHITE)
        findViewById<TextView>(R.id.show_overview_tv).setTextColor(Color.WHITE)
        findViewById<TextView>(R.id.show_seasons_tv).setTextColor(Color.WHITE)
        findViewById<TextView>(R.id.show_images_tv).setTextColor(Color.WHITE)
        findViewById<TextView>(R.id.show_videos_tv).setTextColor(Color.WHITE)
        findViewById<TextView>(R.id.show_cast_tv).setTextColor(Color.WHITE)
        findViewById<TextView>(R.id.show_images_tv).setTextColor(Color.WHITE)
        showOverview!!.setTextColor(Color.WHITE)
    }


    private fun initializeViews() {
        name = findViewById(R.id.show_name)
        rating = findViewById(R.id.show_rating)
        releaseYear = findViewById(R.id.show_runtime)
        homepage = findViewById(R.id.homepage)
        genres = findViewById(R.id.show_genres)
        networks = findViewById(R.id.show_networks)
        poster = findViewById(R.id.tv_show_poster)
        backdrop = findViewById(R.id.show_backdrop)
        blurView = findViewById(R.id.blur_view)
        showOverview = findViewById(R.id.show_overview)
        imagesRV = findViewById(R.id.show_images_rv)
        castRV = findViewById(R.id.show_cast_rv)
        crewRV = findViewById(R.id.show_crew_rv)
        reviewsRV = findViewById(R.id.show_reviews_rv)
        reviewsRL = findViewById(R.id.show_reviews_rl)
        videosRV = findViewById(R.id.show_videos_rv)
        seasonsRV = findViewById(R.id.show_seasons_rv)
    }

    @SuppressLint("StaticFieldLeak")
    private inner class FetchShowDetails : AsyncTask<Void, Void, JSONObject>() {

        override fun onPostExecute(jsonData: JSONObject) {
            super.onPostExecute(jsonData)

            try {
                homepage.text = jsonData.getString("homepage")

                val array = jsonData.getJSONArray("networks")
                val networks = StringBuilder("")
                for (i in 0 until array.length() - 1) {
                    networks.append(array.getJSONObject(i).getString("name")).append(" | ")
                }
                networks.append(array.getJSONObject(array.length() - 1).getString("name"))
                this@ShowDetailActivity.networks.text = networks

                val jsonArray = jsonData.getJSONArray("seasons")
                val seasons = ArrayList<Array<String>>()
                var jsonObject: JSONObject
                for (i in 0 until jsonArray.length()) {
                    jsonObject = jsonArray.getJSONObject(i)
                    if (jsonObject.getInt("season_number") != 0) {
                        seasons.add(arrayOf(jsonObject.getInt("id").toString(), jsonObject.getString("air_date"), jsonObject.getString("poster_path")))
                    }
                }
                val layoutManager = LinearLayoutManager(this@ShowDetailActivity, LinearLayoutManager.HORIZONTAL, false)
                seasonsRV!!.layoutManager = layoutManager
                seasonsRV!!.adapter = SeasonsRecyclerViewAdapter(this@ShowDetailActivity, seasons)

            } catch (e: JSONException) {
                e.printStackTrace()
            }

        }

        override fun doInBackground(vararg voids: Void): JSONObject? {
            var jsonData: JSONObject? = null
            try {
                val client = URL(
                        "http://api.themoviedb.org/3/tv/"
                                + this@ShowDetailActivity.show.id.toString()
                                + "?api_key="
                                + Constants.API_KEY
                                + "&language=en-US"
                ).openConnection() as HttpURLConnection
                client.requestMethod = "GET"
                client.connect()
                val reader = BufferedReader(InputStreamReader(client.inputStream))
                val builder = StringBuilder()
                while (true) {
                    val line = reader.readLine() ?: break
                    builder.append(line)
                }

                jsonData = JSONObject(builder.toString())
            } catch (ignored: Exception) {
            }

            return jsonData
        }
    }

    private inner class SeasonsRecyclerViewAdapter(private val context: Context, var seasons: ArrayList<Array<String>>) : RecyclerView.Adapter<SeasonsRecyclerViewAdapter.ViewHolder>() {

        private inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener {
            var season_air_date: TextView = itemView.findViewById(R.id.season_air_date)
            var season_number: TextView = itemView.findViewById(R.id.season_number)
            var season_poster: ImageView = itemView.findViewById(R.id.season_poster)
            var season_card_back:ConstraintLayout = itemView.findViewById(R.id.season_card_back)

            init {
                itemView.setOnClickListener(this)
            }

            override fun onClick(view: View) {
                val intent = Intent(context, ShowSeasonDetailActivity::class.java)
                intent.putExtra("seasonNumber", adapterPosition + 1)
                intent.putExtra("tvShowID", show.id)
                intent.putExtra("posterURL", show.poster_url)
                startActivity(intent,
                        ActivityOptions.makeScaleUpAnimation(
                                view, 0, 0, view.width, view.height).toBundle())
            }

        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            return ViewHolder(LayoutInflater.from(
                    parent.context).inflate(R.layout.season_card_layout, parent, false))
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            holder.season_number.text = String.format("Season %s", (position + 1).toString())
            holder.season_air_date.text = seasons[position][1]
            val options = RequestOptions()
                    .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                    .priority(Priority.HIGH)
            if (PreferenceManager.getDefaultSharedPreferences(context).getBoolean("dark_mode", false)) {
                holder.season_card_back.setBackgroundColor(ContextCompat.getColor(context, R.color.black_theme_color))
            }
            Glide.with(context)
                    .asBitmap()
                    .load(Constants.BASE_POSTER_PATH + seasons[position][2])
                    .apply(options)
                    .apply(RequestOptions()
                            .diskCacheStrategy(DiskCacheStrategy.ALL)
                            .apply(RequestOptions()
                                    .placeholder(ContextCompat.getDrawable(this.context, R.drawable.gradient))))
                    .transition(withCrossFade())
                    .into(holder.season_poster)
        }

        override fun getItemCount(): Int {
            return seasons.size
        }
    }
}

