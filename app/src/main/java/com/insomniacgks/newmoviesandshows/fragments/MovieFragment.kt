package com.insomniacgks.newmoviesandshows.fragments


import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ObjectAnimator
import android.content.res.ColorStateList
import android.content.res.Configuration
import android.graphics.Color
import android.os.Bundle
import android.support.design.widget.CoordinatorLayout
import android.support.design.widget.FloatingActionButton
import android.support.design.widget.NavigationView
import android.support.design.widget.Snackbar
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.FrameLayout
import android.widget.RelativeLayout

import com.insomniacgks.newmoviesandshows.activities.MainActivity
import com.insomniacgks.newmoviesandshows.data.Constants
import com.insomniacgks.newmoviesandshows.data.FetchMovies
import com.insomniacgks.newmoviesandshows.R
import com.insomniacgks.newmoviesandshows.adapters.MoviesRecyclerViewAdapter
import com.insomniacgks.newmoviesandshows.models.MovieModel

import java.util.ArrayList
import java.util.Objects

import eightbitlab.com.blurview.BlurView

/**
 * A simple [Fragment] subclass.
 */
class MovieFragment : Fragment(), NavigationView.OnNavigationItemSelectedListener, FetchMovies.AsyncResponse {

    internal var layout: RelativeLayout
    internal var fab: FloatingActionButton
    internal var tpview: NavigationView
    private var recyclerView: RecyclerView? = null
    internal var firstPageFlag = true
    internal var currentURL: String
    internal var blurView: FrameLayout
    internal var coordinatorLayout: CoordinatorLayout
    internal var movies: ArrayList<MovieModel>
    internal var fetchMovies: FetchMovies
    private var myAdapter: MoviesRecyclerViewAdapter? = null
    internal var page_count = 1
    private var total_pages = 999999


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_movie, container, false)

        fab = view.findViewById(R.id.FAB)
        tpview = view.findViewById(R.id.timepassview)

        tpview.setNavigationItemSelectedListener(this)
        tpview.menu.getItem(0).isChecked = true

        layout = view.findViewById(R.id.timepass)
        recyclerView = view.findViewById(R.id.movies_recycler_view)

        blurView = view.findViewById(R.id.blur_view)
        currentURL = "https://api.themoviedb.org/3/movie/now_playing?api_key=" + Constants.API_KEY + "&language=en-US&page="

        coordinatorLayout = view.findViewById(R.id.coordinator_layout)
        val snackbar = Snackbar.make(coordinatorLayout, "Loading Movies...", Snackbar.LENGTH_SHORT)
        snackbar.show()
        recyclerView!!.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                if (!recyclerView.canScrollVertically(1)) {
                    page_count++
                    if (page_count <= total_pages) {
                        fetchMovies = FetchMovies(currentURL + page_count)
                        fetchMovies.delegate = this@MovieFragment
                        fetchMovies.execute()
                        snackbar.show()
                    }
                }
            }
        })

        blurView.setOnClickListener {
            val animation = ObjectAnimator.ofFloat(layout, "translationX", -tpview.width)
            animation.setDuration(125)
            animation.addListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator) {
                    super.onAnimationEnd(animation)
                    tpview.visibility = View.INVISIBLE
                    fab.setImageDrawable(ContextCompat.getDrawable(Objects.requireNonNull<Context>(context), R.drawable.ic_chevron_right_black_24dp))
                }
            })
            blurView.visibility = View.INVISIBLE
            animation.start()
        }

        movies = ArrayList()

        fetchMovies = FetchMovies(currentURL + page_count)
        fetchMovies.delegate = this@MovieFragment
        fetchMovies.execute()

        if (MainActivity.getIsDarkMode()) {
            fab.backgroundTintList = ColorStateList.valueOf(Color.rgb(66, 66, 66))
            fab.imageTintList = ColorStateList.valueOf(Color.WHITE)
            recyclerView!!.setBackgroundColor(Color.rgb(224, 224, 224))
        }
        fab.setOnClickListener {
            if (tpview.visibility == View.INVISIBLE) {
                tpview.visibility = View.VISIBLE
                val animation = ObjectAnimator.ofFloat(layout, "translationX", 0)
                animation.setDuration(125)
                animation.start()
                animation.addListener(object : AnimatorListenerAdapter() {
                    override fun onAnimationEnd(animation: Animator) {
                        super.onAnimationEnd(animation)
                        fab.setImageDrawable(ContextCompat.getDrawable(Objects.requireNonNull<Context>(context), R.drawable.ic_chevron_left_black_24dp))
                    }
                })
                blurView.visibility = View.VISIBLE
            } else {
                val animation = ObjectAnimator.ofFloat(layout, "translationX", -tpview.width)
                animation.setDuration(125)
                animation.addListener(object : AnimatorListenerAdapter() {
                    override fun onAnimationEnd(animation: Animator) {
                        super.onAnimationEnd(animation)
                        tpview.visibility = View.INVISIBLE
                        fab.setImageDrawable(ContextCompat.getDrawable(Objects.requireNonNull<Context>(context), R.drawable.ic_chevron_right_black_24dp))
                    }
                })
                blurView.visibility = View.INVISIBLE
                animation.start()

            }
            /*Animation fadeAnimation = AnimationUtils.loadAnimation(this, R.anim.blur_view_animation);
                passwordField.startAnimation(fadeAnimation);*/
        }

        return view
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        val id = item.itemId

        when (id) {
            R.id.movies_in_theatres -> {
                if (!item.isChecked) {
                    page_count = 1
                    firstPageFlag = true
                    currentURL = "https://api.themoviedb.org/3/movie/now_playing?api_key=" + Constants.API_KEY + "&language=en-US&page="
                    fetchMovies = FetchMovies(currentURL + page_count)
                    fetchMovies.delegate = this@MovieFragment
                    fetchMovies.execute()
                }
            }
            R.id.movie_top_rated -> {
                if (!item.isChecked) {
                    page_count = 1
                    firstPageFlag = true
                    currentURL = "https://api.themoviedb.org/3/movie/top_rated?api_key=" + Constants.API_KEY + "&language=en-US&page="
                    fetchMovies = FetchMovies(currentURL + page_count)
                    fetchMovies.delegate = this@MovieFragment
                    fetchMovies.execute()
                }
            }
            R.id.movies_popular -> {
                if (!item.isChecked) {
                    page_count = 1
                    firstPageFlag = true
                    currentURL = "https://api.themoviedb.org/3/movie/popular?api_key=" + Constants.API_KEY + "&language=en-US&page="
                    fetchMovies = FetchMovies(currentURL + page_count)
                    fetchMovies.delegate = this@MovieFragment
                    fetchMovies.execute()
                }
            }
            else -> {
                if (!item.isChecked) {
                    page_count = 1
                    firstPageFlag = true
                    currentURL = ("https://api.themoviedb.org/3/discover/movie?api_key="
                            + Constants.API_KEY
                            + "&language=en-US&sort_by=popularity.desc&with_genres="
                            + Constants.genreID(item.title.toString())
                            + "&with_original_language=en&page=")
                    fetchMovies = FetchMovies(currentURL + page_count)
                    fetchMovies.delegate = this@MovieFragment
                    fetchMovies.execute()
                }
            }
        }

        val animation = ObjectAnimator.ofFloat(layout, "translationX", -tpview.width)
        animation.setDuration(125)
        animation.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                super.onAnimationEnd(animation)
                tpview.visibility = View.INVISIBLE
                fab.setImageDrawable(ContextCompat.getDrawable(Objects.requireNonNull<Context>(context), R.drawable.ic_chevron_right_black_24dp))
            }
        })
        blurView.visibility = View.INVISIBLE
        animation.start()
        return true
    }

    fun changeFabPosition() {
        val animation = ObjectAnimator.ofFloat(layout, "translationX", -tpview.width)
        animation.setDuration(300)
        animation.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                super.onAnimationEnd(animation)
                tpview.visibility = View.INVISIBLE
                fab.setImageDrawable(ContextCompat.getDrawable(Objects.requireNonNull<Context>(context), R.drawable.ic_chevron_right_black_24dp))
            }
        })
        animation.start()
    }

    override fun processFinish(output: ArrayList<MovieModel>, total_pages: Int) {
        movies = output
        if (firstPageFlag) {
            val columns: Int
            columns = 3
            this.total_pages = total_pages
            val layoutManager = GridLayoutManager(context, columns)
            myAdapter = MoviesRecyclerViewAdapter(movies, context)
            this.recyclerView!!.layoutManager = layoutManager
            this.recyclerView!!.adapter = myAdapter
            firstPageFlag = false
        } else {
            myAdapter!!.addMoreItems(movies)
            myAdapter!!.notifyDataSetChanged()
        }
    }
}// Required empty public constructor
