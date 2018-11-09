package com.insomniacgks.newmoviesandshows.fragments


import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ObjectAnimator
import android.content.Context
import android.content.res.ColorStateList
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
import android.widget.FrameLayout
import android.widget.RelativeLayout
import com.insomniacgks.newmoviesandshows.R
import com.insomniacgks.newmoviesandshows.activities.MainActivity
import com.insomniacgks.newmoviesandshows.adapters.MoviesRecyclerViewAdapter
import com.insomniacgks.newmoviesandshows.data.Constants
import com.insomniacgks.newmoviesandshows.data.FetchMovies
import com.insomniacgks.newmoviesandshows.models.MovieModel
import java.util.*

/**
 * A simple [Fragment] subclass.
 */
class MovieFragment : Fragment(), NavigationView.OnNavigationItemSelectedListener, FetchMovies.AsyncResponse {

    private lateinit var layout: RelativeLayout
    private lateinit var fab: FloatingActionButton
    private lateinit var tpview: NavigationView
    private var recyclerView: RecyclerView? = null
    private var firstPageFlag = true
    private lateinit var currentURL: String
    private lateinit var blurView: FrameLayout
    private lateinit var coordinatorLayout: CoordinatorLayout
    private lateinit var movies: ArrayList<MovieModel>
    private lateinit var fetchMovies: FetchMovies
    private var myAdapter: MoviesRecyclerViewAdapter? = null
    private var page_count = 1
    private var total_pages = 999999
    private var savedFragmentState: Bundle? = null
    lateinit var v: View
    val STAV = "saved_state"
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        v = inflater.inflate(R.layout.fragment_movie, container, false)
        if (savedFragmentState == null && savedInstanceState != null) {
            savedFragmentState = savedInstanceState.getBundle(STAV)
        }
        if(savedFragmentState!=null){

        }
        fab = v.findViewById(R.id.FAB)
        tpview = v.findViewById(R.id.timepassview)

        tpview.setNavigationItemSelectedListener(this)
        tpview.menu.getItem(0).isChecked = true

        layout = v.findViewById(R.id.timepass)
        recyclerView = v.findViewById(R.id.movies_recycler_view)

        blurView = v.findViewById(R.id.blur_view)
        currentURL = "https://api.themoviedb.org/3/movie/now_playing?api_key=" + Constants.API_KEY + "&language=en-US&page="

        coordinatorLayout = v.findViewById(R.id.coordinator_layout)
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
            val animation = ObjectAnimator.ofFloat(layout, "translationX", (-tpview.width).toFloat())
            animation.duration = 125
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

        if (MainActivity.isDarkMode) {
            fab.backgroundTintList = ColorStateList.valueOf(Color.rgb(66, 66, 66))
            fab.imageTintList = ColorStateList.valueOf(Color.WHITE)
            recyclerView!!.setBackgroundColor(Color.rgb(224, 224, 224))
        }
        fab.setOnClickListener {
            if (tpview.visibility == View.INVISIBLE) {
                tpview.visibility = View.VISIBLE
                val animation = ObjectAnimator.ofFloat(layout, "translationX", 0.0f)
                animation.duration = 125
                animation.start()
                animation.addListener(object : AnimatorListenerAdapter() {
                    override fun onAnimationEnd(animation: Animator) {
                        super.onAnimationEnd(animation)
                        fab.setImageDrawable(ContextCompat.getDrawable(Objects.requireNonNull<Context>(context), R.drawable.ic_chevron_left_black_24dp))
                    }
                })
                blurView.visibility = View.VISIBLE
            } else {
                val animation = ObjectAnimator.ofFloat(layout, "translationX", (-tpview.width).toFloat())
                animation.duration = 125
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

        return v
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

        val animation = ObjectAnimator.ofFloat(layout, "translationX", (-tpview.width).toFloat())
        animation.duration = 125
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
        val animation = ObjectAnimator.ofFloat(layout, "translationX", (-tpview.width).toFloat())
        animation.duration = 300
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
            val columns = 3
            this.total_pages = total_pages
            val layoutManager = GridLayoutManager(context, columns)
            myAdapter = MoviesRecyclerViewAdapter(movies, context!!)
            this.recyclerView!!.layoutManager = layoutManager
            this.recyclerView!!.adapter = myAdapter
            firstPageFlag = false
        } else {
            myAdapter!!.addMoreItems(movies)
            myAdapter!!.notifyDataSetChanged()
        }
    }
}// Required empty public constructor
