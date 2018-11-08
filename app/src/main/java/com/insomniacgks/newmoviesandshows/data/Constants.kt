package com.insomniacgks.newmoviesandshows.data

import android.util.SparseArray
import java.util.*

object Constants {
    val BASE_POSTER_PATH = "https://image.tmdb.org/t/p/w342"
    internal val BASE_BACKDROP_PATH = "https://image.tmdb.org/t/p/w780"
    internal val BASE_PROFILE_IMAGE_PATH = "https://image.tmdb.org/t/p/w154"
    val API_KEY = "<TMDB API KEY>"
    val YOUTUBE_VIDEO_URL = "http://www.youtube.com/watch?v=%1\$s"
    internal val YOUTUBE_THUMBNAIL_URL = "http://img.youtube.com/vi/%1\$s/0.jpg"
    private val GENRES = HashMap<String, Int>()
    private val GENRES2 = SparseArray<String>()

    fun initializeGenres() {
        GENRES["Action"] = 28
        GENRES["Adventure"] = 12
        GENRES["Animation"] = 16
        GENRES["Comedy"] = 35
        GENRES["Crime"] = 80
        GENRES["Documentary"] = 99
        GENRES["Drama"] = 18
        GENRES["Family"] = 10751
        GENRES["Fantasy"] = 14
        GENRES["History"] = 36
        GENRES["Horror"] = 27
        GENRES["Music"] = 10402
        GENRES["Mystery"] = 9648
        GENRES["Romance"] = 10749
        GENRES["Sci-Fi"] = 878
        GENRES["TV Movie"] = 10770
        GENRES["Thriller"] = 53
        GENRES["War"] = 10752
        GENRES["Western"] = 37
        GENRES["Action & Adventure"] = 10759
        GENRES["Kids"] = 10762
        GENRES["News"] = 10763
        GENRES["Reality"] = 10764
        GENRES["Sci-Fi & Fantasy"] = 10765
        GENRES["Soap"] = 10766
        GENRES["Talk"] = 10767
        GENRES["War & Politics"] = 10768
    }

    fun initializeGenresR() {
        GENRES2.put(28, "Action")
        GENRES2.put(12, "Adventure")
        GENRES2.put(16, "Animation")
        GENRES2.put(35, "Comedy")
        GENRES2.put(80, "Crime")
        GENRES2.put(99, "Documentary")
        GENRES2.put(18, "Drama")
        GENRES2.put(10751, "Family")
        GENRES2.put(14, "Fantasy")
        GENRES2.put(36, "History")
        GENRES2.put(27, "Horror")
        GENRES2.put(10402, "Music")
        GENRES2.put(9648, "Mystery")
        GENRES2.put(10749, "Romance")
        GENRES2.put(878, "Science Fiction")
        GENRES2.put(10770, "TV Movie")
        GENRES2.put(53, "Thriller")
        GENRES2.put(10752, "War")
        GENRES2.put(37, "Western")
        GENRES2.put(10759, "Action & Adventure")
        GENRES2.put(10762, "Kids")
        GENRES2.put(10763, "News")
        GENRES2.put(10764, "Reality")
        GENRES2.put(10765, "Sci-Fi & Fantasy")
        GENRES2.put(10766, "Soap")
        GENRES2.put(10767, "Talk")
        GENRES2.put(10768, "War & Politics")
    }

    fun genreID(genre: String): Int {
        return GENRES[genre]!!
    }

    fun genreString(id: Int): String {
        return GENRES2.get(id)
    }
}
