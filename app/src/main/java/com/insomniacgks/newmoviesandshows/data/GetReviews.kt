package com.insomniacgks.newmoviesandshows.data

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.os.AsyncTask
import android.preference.PreferenceManager
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatDelegate
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import com.insomniacgks.newmoviesandshows.R
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import java.util.*

class GetReviews(@field:SuppressLint("StaticFieldLeak") private val context: Context, private val id: Int, @field:SuppressLint("StaticFieldLeak") private val recyclerView: RecyclerView, @field:SuppressLint("StaticFieldLeak") private val rl: RelativeLayout, private val type: String) : AsyncTask<String, Void, ArrayList<Array<String>>>() {

    override fun doInBackground(vararg strings: String): ArrayList<Array<String>>? {
        var reviews: ArrayList<Array<String>>? = null
        try {
            val client = URL("http://api.themoviedb.org/3/" + type + "/" + id + "/reviews?api_key=" + Constants.API_KEY + "&language=en-US").openConnection() as HttpURLConnection
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
                reviews.add(arrayOf(`object`.getString("author"), `object`.getString("content")))
                i++
            }
        } catch (ignored: Exception) {
        }

        return reviews
    }

    override fun onPostExecute(strings: ArrayList<Array<String>>?) {
        super.onPostExecute(strings)
        if (strings != null && strings.size != 0) {
            val layoutManager = LinearLayoutManager(this.context)
            val myAdapter = ReviewsRecyclerViewAdapter(this.context, strings)
            recyclerView.layoutManager = layoutManager
            recyclerView.adapter = myAdapter
        } else {
            rl.visibility = View.GONE
        }
    }


    internal inner class ReviewsRecyclerViewAdapter(private val context: Context, private val reviewss: ArrayList<Array<String>>) : RecyclerView.Adapter<ReviewsRecyclerViewAdapter.ViewHolder>() {
        private val isDarkModeOn: Boolean = PreferenceManager.getDefaultSharedPreferences(context).getBoolean("dark_mode", false)

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            AppCompatDelegate.setCompatVectorFromResourcesEnabled(true)
            return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.reviews_card_layout, parent, false))
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            if (isDarkModeOn) {
                holder.author.setTextColor(Color.WHITE)
                holder.content.setTextColor(Color.WHITE)
                holder.LayoutBack.setBackgroundColor(ContextCompat.getColor(this.context, R.color.black_theme_color))
            }
            holder.author.text = String.format("Review By %s:", this.reviewss[position][0])
            holder.content.text = this.reviewss[position][1]
        }

        override fun getItemCount(): Int {
            return reviewss.size
        }

        internal inner class ViewHolder(itemView: View) : android.support.v7.widget.RecyclerView.ViewHolder(itemView) {

            var content: TextView
            var author: TextView
            var LayoutBack: LinearLayout

            init {
                this.LayoutBack = itemView.findViewById(R.id.layout_back)
                this.author = itemView.findViewById(R.id.review_author)
                this.content = itemView.findViewById(R.id.review_content)
            }

        }
    }
}