package com.insomniacgks.newmoviesandshows.backend

import android.os.AsyncTask
import com.insomniacgks.newmoviesandshows.models.ShowModel
import org.json.JSONException
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import java.util.*

class FetchShows(private var link: String) : AsyncTask<Void, Void, ArrayList<ShowModel>>() {

    var delegate: FetchShows.AsyncResponse? = null

    interface AsyncResponse {
        fun processFinish(output: ArrayList<ShowModel>, total_pages: Int)
    }

    override fun doInBackground(vararg voids: Void): ArrayList<ShowModel>? {
        var shows: ArrayList<ShowModel>? = null
        try {
            val url = URL(link)
            val client = url.openConnection() as HttpURLConnection
            client.requestMethod = "GET"
            client.connect()
            val reader = BufferedReader(InputStreamReader(client.inputStream))
            val builder = StringBuilder()
            while (true) {
                val line = reader.readLine() ?: break
                builder.append(line)
            }
            shows = getShows(builder.toString())

        } catch (ignored: Exception) {
        }

        return shows
    }

    @Throws(JSONException::class)
    private fun getShows(JSONstr: String): ArrayList<ShowModel>? {
        total_pages = JSONObject(JSONstr).getInt("total_pages")
        val resultArray = JSONObject(JSONstr).getJSONArray("results")
        var shows: ArrayList<ShowModel>? = null
        if (resultArray.length() != 0) {
            shows = ArrayList()
            for (i in 0 until resultArray.length()) {
                val item = resultArray.getJSONObject(i)
                if (item.getString("original_language") == "en") {
                    val genres = item.getJSONArray("genre_ids")
                    val genresList = ArrayList<Int>()
                    for (j in 0 until genres.length()) {
                        genresList.add(genres.getInt(j))
                    }
                    shows.add(ShowModel(
                            item.getInt("id"),
                            item.getString("original_name"),
                            item.getString("first_air_date"),
                            item.getString("overview"),
                            item.getDouble("vote_average").toString(),
                            Constants.BASE_POSTER_PATH + item.getString("poster_path"),
                            Constants.BASE_BACKDROP_PATH + item.getString("backdrop_path"),
                            genresList))
                }
            }
        }
        return shows
    }

    override fun onPostExecute(showModels: ArrayList<ShowModel>) {
        super.onPostExecute(showModels)
        delegate!!.processFinish(showModels, total_pages)
    }

    companion object {
        private var total_pages: Int = 0
    }
}