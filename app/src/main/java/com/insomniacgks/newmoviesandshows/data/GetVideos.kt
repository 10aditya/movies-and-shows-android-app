package com.insomniacgks.newmoviesandshows.data

import android.annotation.SuppressLint
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.AsyncTask
import android.preference.PreferenceManager
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatDelegate
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout

import com.bumptech.glide.Glide
import com.bumptech.glide.Priority
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.insomniacgks.newmoviesandshows.R

import org.json.JSONArray
import org.json.JSONObject

import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import java.util.ArrayList

import com.bumptech.glide.load.resource.bitmap.BitmapTransitionOptions.withCrossFade

class GetVideos(@field:SuppressLint("StaticFieldLeak")
                private val context: Context, private val id: Int, @field:SuppressLint("StaticFieldLeak")
                private val recyclerView: RecyclerView, private val type: String, @field:SuppressLint("StaticFieldLeak")
                private val rl: RelativeLayout) : AsyncTask<String, Void, ArrayList<String>>() {

    override fun doInBackground(vararg strings: String): ArrayList<String>? {
        var reviews: ArrayList<String>? = null
        try {
            val client = URL("http://api.themoviedb.org/3/" + type + "/" + id + "/videos?api_key=" + Constants.API_KEY + "&language=en-US").openConnection() as HttpURLConnection
            client.requestMethod = "GET"
            client.connect()
            val reader = BufferedReader(InputStreamReader(client.inputStream))
            val builder = StringBuilder()
            while (true) {
                val line = reader.readLine() ?: break
                builder.append(line)
            }
            reviews = ArrayList()
            val resultArray = JSONObject(builder.toString()).getJSONArray("results")
            var i = 0
            while (i < resultArray.length()) {
                val `object` = resultArray.getJSONObject(i)
                if ("YouTube" == `object`.getString("site")) {
                    reviews.add(`object`.getString("key"))
                    i++
                }
            }
        } catch (ignored: Exception) {
        }

        return reviews
    }

    override fun onPostExecute(strings: ArrayList<String>?) {
        super.onPostExecute(strings)
        if (strings != null) {
            if (strings.size != 0) {
                val layoutManager = LinearLayoutManager(this.context, LinearLayoutManager.HORIZONTAL, false)
                val myAdapter = VideosRecyclerViewAdapter(this.context, strings)
                recyclerView.layoutManager = layoutManager
                recyclerView.adapter = myAdapter
            } else {
                rl.visibility = View.GONE
            }
        } else {
            rl.visibility = View.GONE
        }
    }


    internal inner class VideosRecyclerViewAdapter(private val context: Context, private val videoss: ArrayList<String>) : RecyclerView.Adapter<VideosRecyclerViewAdapter.ViewHolder>() {


        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            AppCompatDelegate.setCompatVectorFromResourcesEnabled(true)
            return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.videos_card_layout, parent, false))
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val options = RequestOptions()
                    .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                    .priority(Priority.HIGH)

            if (PreferenceManager.getDefaultSharedPreferences(this.context).getBoolean("dark_mode", false)) {
                holder.layoutBack.setBackgroundColor(ContextCompat.getColor(this.context, R.color.black_theme_color))
            }
            Glide.with(context)
                    .asBitmap()
                    .load(String.format(Constants.YOUTUBE_THUMBNAIL_URL, videoss[position]))
                    .apply(options)
                    .apply(RequestOptions()
                            .diskCacheStrategy(DiskCacheStrategy.ALL)
                            .apply(RequestOptions()
                                    .placeholder(ContextCompat.getDrawable(context, R.drawable.gradient))))
                    .transition(withCrossFade())
                    .into(holder.video_thumbnail)

        }

        override fun getItemCount(): Int {
            return videoss.size
        }

        internal inner class ViewHolder(itemView: View) : android.support.v7.widget.RecyclerView.ViewHolder(itemView), View.OnClickListener {

            var video_thumbnail: ImageView
            var layoutBack: RelativeLayout

            init {
                layoutBack = itemView.findViewById(R.id.layout_back)
                this.video_thumbnail = itemView.findViewById(R.id.trailer_thumbnail)
                itemView.setOnClickListener(this)
            }

            override fun onClick(v: View) {
                val appIntent = Intent("android.intent.action.VIEW", Uri.parse("vnd.youtube:" + videoss[adapterPosition]))
                val webIntent = Intent("android.intent.action.VIEW", Uri.parse("http://www.youtube.com/watch?v=" + videoss[adapterPosition]))
                try {
                    context.startActivity(appIntent)
                } catch (e: ActivityNotFoundException) {
                    context.startActivity(webIntent)
                }

            }
        }
    }
}