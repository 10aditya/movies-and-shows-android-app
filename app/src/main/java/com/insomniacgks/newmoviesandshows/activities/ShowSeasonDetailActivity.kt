package com.insomniacgks.newmoviesandshows.activities

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.graphics.drawable.ColorDrawable
import android.os.AsyncTask
import android.os.Bundle
import android.support.design.widget.TabLayout
import android.support.design.widget.TabLayout.OnTabSelectedListener
import android.support.design.widget.TabLayout.Tab
import android.support.v4.content.ContextCompat
import android.support.v4.view.ViewPager
import android.support.v7.app.AppCompatActivity
import android.support.v7.graphics.Palette
import android.widget.ImageView
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
import com.insomniacgks.newmoviesandshows.adapters.ShowSeasonViewPageAdapter
import com.insomniacgks.newmoviesandshows.data.Constants
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import java.util.*

class ShowSeasonDetailActivity : AppCompatActivity(), OnTabSelectedListener {
    private var poster: ImageView? = null
    private var posterURL: String? = null
    internal var seasonNumber: Int = 0
    private var tabLayout: TabLayout? = null
    internal var tvShowID: Int = 0

    internal var viewPager: ViewPager? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_show_season_detail)
        this.seasonNumber = intent.getIntExtra("seasonNumber", 0)
        this.tvShowID = intent.getIntExtra("tvShowID", 0)
        this.posterURL = intent.getStringExtra("posterURL")
        supportActionBar!!.title = "Season " + this.seasonNumber
        this.viewPager = findViewById(R.id.seasonDetail_VP)
        this.tabLayout = findViewById(R.id.seasonDetail_TL)
        this.poster = findViewById(R.id.seasonBackground)

        val options = RequestOptions()
                .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                .priority(Priority.HIGH)

        Glide.with(this)
                .asBitmap()
                .load(posterURL)
                .apply(options)
                .apply(RequestOptions()
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .apply(RequestOptions()
                                .placeholder(ContextCompat.getDrawable(this, R.drawable.gradient))))
                .listener(object : RequestListener<Bitmap> {
                    override fun onLoadFailed(e: GlideException?, model: Any, target: Target<Bitmap>, isFirstResource: Boolean): Boolean {
                        return false
                    }

                    override fun onResourceReady(resource: Bitmap, model: Any, target: Target<Bitmap>, dataSource: DataSource, isFirstResource: Boolean): Boolean {
                        Palette.from(resource).generate { p ->
                            // Use generated instance
                            val ps = p!!.vibrantSwatch
                            val psw = p.darkVibrantSwatch
                            if (ps != null) {
                                this@ShowSeasonDetailActivity.supportActionBar!!.setBackgroundDrawable(ColorDrawable(ps.rgb))
                            }
                            if (psw != null) {
                                this@ShowSeasonDetailActivity.window.statusBarColor = psw.rgb
                            }
                        }
                        return false
                    }
                })
                .transition(withCrossFade())
                .into(poster!!)

        FetchEpisodes().execute()
        this.tabLayout!!.addOnTabSelectedListener(this)
    }

    @SuppressLint("StaticFieldLeak")
    internal inner class FetchEpisodes : AsyncTask<Void, Void, ArrayList<Array<String>>>() {

        override fun doInBackground(vararg voids: Void): ArrayList<Array<String>> {
            val episodes = ArrayList<Array<String>>()
            try {
                val client = URL(
                        "http://api.themoviedb.org/3/tv/"
                                + this@ShowSeasonDetailActivity.tvShowID.toString()
                                + "/season/"
                                + this@ShowSeasonDetailActivity.seasonNumber
                                + "?api_key="
                                + Constants.API_KEY
                                + "&language=en-US")
                        .openConnection() as HttpURLConnection
                client.requestMethod = "GET"
                client.connect()
                val reader = BufferedReader(InputStreamReader(client.inputStream))
                val builder = StringBuilder()
                while (true) {
                    val line = reader.readLine() ?: break
                    builder.append(line)
                }
                val jsonArray = JSONObject(builder.toString()).getJSONArray("episodes")
                for (i in 0 until jsonArray.length()) {
                    val `object` = jsonArray.getJSONObject(i)
                    episodes.add(arrayOf(`object`.getString("name"), `object`.getString("overview"), `object`.getString("air_date"), `object`.getString("still_path")))
                }
            } catch (ignored: Exception) {
            }

            return episodes
        }

        override fun onPostExecute(episodes: ArrayList<Array<String>>) {
            super.onPostExecute(episodes)
            for (i in 1..episodes.size) {
                tabLayout!!.addTab(tabLayout!!.newTab().setText(String.format(Locale.ENGLISH, "S%2dE%2d", seasonNumber, i)))
            }
            viewPager!!.adapter = ShowSeasonViewPageAdapter(supportFragmentManager, episodes, episodes.size, this@ShowSeasonDetailActivity.seasonNumber)
        }

    }

    override fun onTabSelected(tab: Tab) {
        this.viewPager!!.currentItem = tab.position
    }

    override fun onTabUnselected(tab: Tab) {}

    override fun onTabReselected(tab: Tab) {}
}
