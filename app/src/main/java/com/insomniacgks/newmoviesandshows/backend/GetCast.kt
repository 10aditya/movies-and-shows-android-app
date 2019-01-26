package com.insomniacgks.newmoviesandshows.backend

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Color
import android.os.AsyncTask
import android.preference.PreferenceManager
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatDelegate
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
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
import com.mikhaellopez.circularimageview.CircularImageView
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import java.util.*

class GetCast(@field:SuppressLint("StaticFieldLeak")
              private val context: Context, private val id: Int, @field:SuppressLint("StaticFieldLeak")
              private val recyclerView: RecyclerView, private val type: String, @field:SuppressLint("StaticFieldLeak")
              private val relativeLayout: RelativeLayout) : AsyncTask<String, Void, ArrayList<Array<String>>>() {

    override fun doInBackground(vararg strings: String): ArrayList<Array<String>> {
        val casts = ArrayList<Array<String>>()
        try {
            val client = URL("http://api.themoviedb.org/3/" + type + "/" + id + "/credits?api_key=" + Constants.API_KEY + "&language=en-US").openConnection() as HttpURLConnection
            client.requestMethod = "GET"
            client.connect()
            val reader = BufferedReader(InputStreamReader(client.inputStream))
            val builder = StringBuilder()
            while (true) {
                val line = reader.readLine() ?: break
                builder.append(line)
            }
            val resultArray = JSONObject(builder.toString()).getJSONArray("cast")
            var i = 0
            while (i < 10 && i < resultArray.length()) {
                val `object` = resultArray.getJSONObject(i)
                casts.add(arrayOf(`object`.getString("character"), `object`.getString("name"), `object`.getString("profile_path")))
                i++
            }
        } catch (ignored: Exception) {
        }

        return casts
    }

    override fun onPostExecute(strings: ArrayList<Array<String>>?) {
        super.onPostExecute(strings)
        if (strings != null) {
            if (strings.size != 0) {
                val layoutManager = LinearLayoutManager(this.context, LinearLayoutManager.HORIZONTAL, false)
                val myAdapter = CastsRecyclerViewAdapter(this.context, strings)
                recyclerView.layoutManager = layoutManager
                recyclerView.adapter = myAdapter
            } else {
                relativeLayout.visibility = View.GONE
            }
        } else {
            relativeLayout.visibility = View.GONE
        }
    }


    internal inner class CastsRecyclerViewAdapter(private val context: Context, private val casts: ArrayList<Array<String>>) : RecyclerView.Adapter<CastsRecyclerViewAdapter.ViewHolder>() {
        private val isDarkModeOn: Boolean = PreferenceManager.getDefaultSharedPreferences(context).getBoolean("dark_mode", false)


        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            AppCompatDelegate.setCompatVectorFromResourcesEnabled(true)
            return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.people_card_layout, parent, false))
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            if (isDarkModeOn) {
                holder.LayoutBack.setBackgroundColor(ContextCompat.getColor(context, R.color.black_theme_color))
                holder.roll.setTextColor(Color.WHITE)
            }
            holder.roll.text = String.format("%s\nas\n%s",
                    this.casts[position][1], this.casts[position][0])

            val options = RequestOptions()
                    .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                    .priority(Priority.HIGH)
            if (this.casts[position][2] != "null" && this.casts[position][2] != "") {
                Glide.with(context)
                        .asBitmap()
                        .load(Constants.BASE_PROFILE_IMAGE_PATH + this.casts[position][2])
                        .apply(options)
                        .apply(RequestOptions()
                                .diskCacheStrategy(DiskCacheStrategy.ALL))
                        .apply(RequestOptions().placeholder(ContextCompat.getDrawable(context, R.drawable.profile)))
                        .listener(object : RequestListener<Bitmap> {
                            override fun onLoadFailed(e: GlideException?, model: Any, target: Target<Bitmap>, isFirstResource: Boolean): Boolean {
                                return false
                            }

                            override fun onResourceReady(resource: Bitmap, model: Any, target: Target<Bitmap>, dataSource: DataSource, isFirstResource: Boolean): Boolean {
                                Log.i("profile image download:", "complete")
                                holder.ProfileImageView.setImageBitmap(resource)
                                return false
                            }
                        })
                        .transition(withCrossFade())
                        .into(holder.ProfileImageView)
            }
        }

        override fun getItemCount(): Int {
            return casts.size
        }

        internal inner class ViewHolder(itemView: View) : android.support.v7.widget.RecyclerView.ViewHolder(itemView) {
            var ProfileImageView: CircularImageView
            var LayoutBack: LinearLayout
            var roll: TextView

            init {
                this.LayoutBack = itemView.findViewById(R.id.layout_back)
                this.ProfileImageView = itemView.findViewById(R.id.profile_image)
                this.roll = itemView.findViewById(R.id.roll)
            }

        }
    }
}