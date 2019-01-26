package com.insomniacgks.newmoviesandshows.models

import java.io.Serializable
import java.util.ArrayList

class ShowModel(val id: Int, val name: String, val first_air_date: String, val overview: String, val rating: String, val poster_url: String, val backdrop_url: String, val genres: ArrayList<Int>) : Serializable
