package com.insomniacgks.newmoviesandshows.models

import java.io.Serializable
import java.util.ArrayList

class MovieModel(val title: String, val releaseDate: String, val overview: String, val posterURL: String, val backdropURL: String, val id: Int, val rating: Float, val genresID: ArrayList<Int>) : Serializable
