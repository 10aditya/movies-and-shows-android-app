package com.insomniacgks.newmoviesandshows.fragments


import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.insomniacgks.newmoviesandshows.R
import com.insomniacgks.newmoviesandshows.adapters.MoviesRecyclerViewAdapter
import com.insomniacgks.newmoviesandshows.backend.Constants
import com.insomniacgks.newmoviesandshows.backend.FetchMovies
import com.insomniacgks.newmoviesandshows.models.MovieModel
import java.util.*

/**
 * A simple [Fragment] subclass.
 */
class SearchMovieResultsFragment : Fragment(), FetchMovies.AsyncResponse {

    private var searchQuery: String? = null
    private var recyclerView: RecyclerView? = null
    private var movies: ArrayList<MovieModel>? = null
    private var firstPageFlag: Boolean = false
    private var total_pages = 9999
    private var myAdapter: MoviesRecyclerViewAdapter? = null
    private var fetchMovies: FetchMovies? = null
    private var url: String? = null
    internal var page_count = 1
    internal var view: View? = null


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        view = inflater.inflate(R.layout.fragment_movie, container, false)
        view!!.findViewById<View>(R.id.timepass).visibility = View.GONE
        view!!.findViewById<View>(R.id.blur_view).visibility = View.GONE
        firstPageFlag = true
        searchQuery = arguments!!.getString("searchQuery")
        url = "https://api.themoviedb.org/3/search/movie?api_key=" + Constants.API_KEY + "&language=en-US&query=" + searchQuery + "&page="
        recyclerView = view!!.findViewById(R.id.movies_recycler_view)
        recyclerView!!.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                if (!recyclerView.canScrollVertically(1)) {
                    page_count++
                    if (page_count <= total_pages) {
                        fetchMovies = FetchMovies(url + page_count)
                        fetchMovies!!.delegate = this@SearchMovieResultsFragment
                        fetchMovies!!.execute()
                    }
                }
            }
        })
        fetchMovies = FetchMovies(url + page_count)
        fetchMovies!!.delegate = this
        fetchMovies!!.execute()
        return view
    }

    override fun processFinish(output: ArrayList<MovieModel>, total_pages: Int) {
        /*if (output == null || output.size==0) {
            view!!.findViewById<View>(R.id.hehe).visibility = View.VISIBLE
            recyclerView.visibility = View.GONE
            return
        }*/

        if (output.size == 0) {
            view!!.findViewById<View>(R.id.hehe).visibility = View.VISIBLE
            recyclerView!!.visibility = View.GONE
            return
        }
        movies = output
        if (firstPageFlag) {
            val columns = 3
            this.total_pages = total_pages
            val layoutManager = GridLayoutManager(context, columns)
            myAdapter = MoviesRecyclerViewAdapter(movies!!, context!!)
            this.recyclerView!!.layoutManager = layoutManager
            this.recyclerView!!.adapter = myAdapter
            firstPageFlag = false
        } else {
            myAdapter!!.addMoreItems(movies!!)
            myAdapter!!.notifyDataSetChanged()
        }
    }
}// Required empty public constructor
