package com.insomniacgks.newmoviesandshows.backend

import android.os.AsyncTask
import com.insomniacgks.newmoviesandshows.models.MovieModel
import org.json.JSONException
import org.json.JSONObject
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import java.util.*

class FetchMovies(private val url: String) : AsyncTask<Void, Void, ArrayList<MovieModel>>() {

    var delegate: AsyncResponse? = null

    interface AsyncResponse {
        fun processFinish(output: ArrayList<MovieModel>, total_pages: Int)
    }

    override fun doInBackground(vararg voids: Void): ArrayList<MovieModel>? {
        return try {
            getJSON(getJSONstr(url))
        } catch (e: JSONException) {
            e.printStackTrace()
            null
        }
    }

    override fun onPostExecute(movieModels: ArrayList<MovieModel>) {
        super.onPostExecute(movieModels)
        delegate!!.processFinish(movieModels, total_pages)
    }

    companion object {
        private var total_pages: Int = 0

        @Throws(JSONException::class)
        private fun getJSON(data: String?): ArrayList<MovieModel>? {
            if (data == null) {
                return null
            }
            total_pages = JSONObject(data).getInt("total_pages")
            val movieItems = ArrayList<MovieModel>()
            val resultArray = JSONObject(data).getJSONArray("results")
            for (i in 0 until resultArray.length()) {
                var movieTitle: String
                var releaseDate: String
                var overView: String
                val movieItem = resultArray.getJSONObject(i)
                movieTitle = if (movieItem.get("title") == null) null.toString() else movieItem.getString("title")
                releaseDate = if (movieItem.get("release_date") == null) null.toString() else movieItem.getString("release_date")

                overView = if (movieItem.get("overview") == null) null.toString() else movieItem.getString("overview")
                val rating = movieItem.getDouble("vote_average")
                val geners = movieItem.getJSONArray("genre_ids")
                val genresList = ArrayList<Int>()
                for (j in 0 until geners.length()) {
                    genresList.add(geners.getInt(j))
                }
                var posterURL: String? = null
                var backdropURL: String? = null
                try {
                    posterURL = movieItem.getString("poster_path")
                    backdropURL = movieItem.getString("backdrop_path")
                } catch (ignored: Exception) {
                }

                val id = movieItem.getInt("id")
                movieItems.add(MovieModel(
                        movieTitle,
                        releaseDate,
                        overView,
                        Constants.BASE_POSTER_PATH + posterURL!!,
                        Constants.BASE_BACKDROP_PATH + backdropURL!!,
                        id,
                        rating.toFloat(),
                        genresList))
            }
            return movieItems
        }

        private fun getJSONstr(link: String): String? {
            var jsonString: String? = null
            try {
                val client = URL(link).openConnection() as HttpURLConnection
                client.requestMethod = "GET"
                client.connect()
                val reader = BufferedReader(InputStreamReader(client.inputStream))
                val builder = StringBuilder()
                while (true) {
                    val line = reader.readLine() ?: break
                    builder.append(line)
                }
                jsonString = builder.toString()
                println(jsonString)
            } catch (e: IOException) {
                e.printStackTrace()
            }

            return jsonString
        }
    }
}
