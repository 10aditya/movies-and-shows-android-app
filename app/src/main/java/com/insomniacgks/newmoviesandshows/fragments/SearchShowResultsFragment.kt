package com.insomniacgks.newmoviesandshows.fragments


import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView

import com.insomniacgks.newmoviesandshows.R
import com.insomniacgks.newmoviesandshows.adapters.MoviesRecyclerViewAdapter
import com.insomniacgks.newmoviesandshows.adapters.ShowsRecyclerViewAdapter
import com.insomniacgks.newmoviesandshows.data.Constants
import com.insomniacgks.newmoviesandshows.data.FetchMovies
import com.insomniacgks.newmoviesandshows.data.FetchShows
import com.insomniacgks.newmoviesandshows.models.MovieModel
import com.insomniacgks.newmoviesandshows.models.ShowModel

import java.util.ArrayList

/**
 * A simple [Fragment] subclass.
 */
class SearchShowResultsFragment : Fragment(), FetchShows.AsyncResponse {


    private var searchQuery: String? = null
    internal var recyclerView: RecyclerView
    private var shows: ArrayList<ShowModel>? = null
    private var firstPageFlag: Boolean = false
    private var total_pages = 9999
    private var myAdapter: ShowsRecyclerViewAdapter? = null
    private var fetchShows: FetchShows? = null
    internal var url: String
    internal var page_count = 1
    private var view: View? = null


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        view = inflater.inflate(R.layout.fragment_show, container, false)
        view!!.findViewById<View>(R.id.timepass).visibility = View.GONE
        view!!.findViewById<View>(R.id.blur_view).visibility = View.GONE
        firstPageFlag = true
        searchQuery = arguments!!.getString("searchQuery")

        url = "https://api.themoviedb.org/3/search/tv?api_key=" + Constants.API_KEY + "&language=en-US&query=" + searchQuery + "&page="
        recyclerView = view!!.findViewById(R.id.shows_recycler_view)
        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                if (!recyclerView.canScrollVertically(1)) {
                    page_count++
                    if (page_count <= total_pages) {
                        fetchShows = FetchShows(url + page_count)
                        fetchShows!!.delegate = this@SearchShowResultsFragment
                        fetchShows!!.execute()
                    }
                }
            }
        })
        fetchShows = FetchShows(url + page_count)
        fetchShows!!.delegate = this
        fetchShows!!.execute()
        return view
    }

    override fun processFinish(output: ArrayList<ShowModel>?, total_pages: Int) {
        shows = output
        if (output == null) {
            view!!.findViewById<View>(R.id.hehe).visibility = View.VISIBLE
            recyclerView.visibility = View.GONE
            return
        }

        if (output.size == 0) {
            view!!.findViewById<View>(R.id.hehe).visibility = View.VISIBLE
            recyclerView.visibility = View.GONE
            return
        }

        if (firstPageFlag) {
            val columns: Int
            columns = 3
            this.total_pages = total_pages
            val layoutManager = GridLayoutManager(context, columns)
            myAdapter = ShowsRecyclerViewAdapter(shows, context)
            this.recyclerView.layoutManager = layoutManager
            this.recyclerView.adapter = myAdapter
            firstPageFlag = false
        } else {
            myAdapter!!.addMoreItems(shows)
            myAdapter!!.notifyDataSetChanged()
        }

    }
}// Required empty public constructor
