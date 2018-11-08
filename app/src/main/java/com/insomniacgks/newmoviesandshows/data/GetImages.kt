package com.insomniacgks.newmoviesandshows.data

import android.content.Context
import android.os.AsyncTask
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.RelativeLayout

import com.insomniacgks.newmoviesandshows.adapters.ImagesRecyclerViewAdapter

import org.json.JSONArray
import org.json.JSONObject

import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import java.util.ArrayList

class GetImages(private val context: Context, private val id: Int, private val recyclerview: RecyclerView, private val type: String, private val rl: RelativeLayout) : AsyncTask<Void, Void, ArrayList<String>>() {

    override fun doInBackground(vararg voids: Void): ArrayList<String>? {
        var images: ArrayList<String>? = null
        try {
            val url = URL("https://api.themoviedb.org/3/" + type + "/" + id + "/images?api_key=" + Constants.API_KEY)
            val client = url.openConnection() as HttpURLConnection
            client.requestMethod = "GET"
            client.connect()
            val reader = BufferedReader(InputStreamReader(client.inputStream))
            val builder = StringBuilder()
            while (true) {
                val line = reader.readLine() ?: break
                builder.append(line)
            }
            val `object` = JSONObject(builder.toString())
            val backdrops = `object`.getJSONArray("backdrops")

            images = ArrayList()
            for (i in 0 until backdrops.length()) {
                images.add(Constants.BASE_BACKDROP_PATH + backdrops.getJSONObject(i).getString("file_path"))
            }
            return images

        } catch (ignored: Exception) {
        }

        return images
    }

    override fun onPostExecute(strings: ArrayList<String>?) {
        super.onPostExecute(strings)
        if (strings != null) {
            if (strings.size != 0) {
                val linearLayoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
                recyclerview.layoutManager = linearLayoutManager
                recyclerview.adapter = ImagesRecyclerViewAdapter(context, strings)
            } else {
                rl.visibility = View.GONE
            }
        } else {
            rl.visibility = View.GONE
        }
    }
}

