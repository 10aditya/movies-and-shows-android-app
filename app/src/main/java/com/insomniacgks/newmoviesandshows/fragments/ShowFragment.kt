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
import com.insomniacgks.newmoviesandshows.adapters.ShowsRecyclerViewAdapter
import com.insomniacgks.newmoviesandshows.data.Constants
import com.insomniacgks.newmoviesandshows.data.FetchShows
import com.insomniacgks.newmoviesandshows.models.ShowModel
import java.util.*

/**
 * A simple [Fragment] subclass.
 */
class ShowFragment : Fragment(), NavigationView.OnNavigationItemSelectedListener, FetchShows.AsyncResponse {

    private lateinit var layout: RelativeLayout
    private lateinit var fab: FloatingActionButton
    private lateinit var tpview: NavigationView
    private var recyclerView: RecyclerView? = null
    internal var firstPageFlag = true
    private lateinit var currentURL: String
    private lateinit var blurView: FrameLayout
    private lateinit var coordinatorLayout: CoordinatorLayout
    private lateinit var shows: ArrayList<ShowModel>
    private lateinit var fetchShows: FetchShows
    private var myAdapter: ShowsRecyclerViewAdapter? = null
    private var page_count = 1
    private var total_pages = 999999


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_show, container, false)

        fab = view.findViewById(R.id.FAB)
        tpview = view.findViewById(R.id.timepassview)

        tpview.setNavigationItemSelectedListener(this)
        tpview.menu.getItem(0).isChecked = true

        layout = view.findViewById(R.id.timepass)
        recyclerView = view.findViewById(R.id.shows_recycler_view)

        currentURL = "https://api.themoviedb.org/3/tv/on_the_air?api_key=" + Constants.API_KEY + "&language=en-US&page="
        coordinatorLayout = view.findViewById(R.id.coordinator_layout)

        val snackbar = Snackbar.make(coordinatorLayout, "Loading TV Shows...", Snackbar.LENGTH_SHORT)
        snackbar.show()
        recyclerView!!.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                if (!recyclerView.canScrollVertically(1)) {
                    page_count++
                    if (page_count <= total_pages) {
                        fetchShows = FetchShows(currentURL + page_count)
                        fetchShows.delegate = this@ShowFragment
                        fetchShows.execute()
                        snackbar.show()
                    }
                }
            }
        })

        blurView = view.findViewById(R.id.blur_view)

        shows = ArrayList()

        if (MainActivity.isDarkMode) {
            fab.backgroundTintList = ColorStateList.valueOf(Color.rgb(66, 66, 66))
            fab.imageTintList = ColorStateList.valueOf(Color.WHITE)
            recyclerView!!.setBackgroundColor(Color.rgb(224, 224, 224))
        }

        fetchShows = FetchShows(currentURL + page_count)
        fetchShows.delegate = this@ShowFragment
        fetchShows.execute()

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
            animation.start()
            blurView.visibility = View.INVISIBLE
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
                animation.start()
                blurView.visibility = View.INVISIBLE
            }
            /*Animation fadeAnimation = AnimationUtils.loadAnimation(this, R.anim.text_view_animation);
                passwordField.startAnimation(fadeAnimation);*/
        }

        return view
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        val id = item.itemId

        when (id) {
            R.id.shows_on_the_air -> {
                if (!item.isChecked) {
                    page_count = 1
                    firstPageFlag = true
                    currentURL = "https://api.themoviedb.org/3/tv/on_the_air?api_key=" + Constants.API_KEY + "&language=en-US&page="
                    fetchShows = FetchShows(currentURL + page_count)
                    fetchShows.delegate = this@ShowFragment
                    fetchShows.execute()
                }
            }
            R.id.shows_airing_today -> {
                if (!item.isChecked) {
                    page_count = 1
                    firstPageFlag = true
                    currentURL = "https://api.themoviedb.org/3/tv/airing_today?api_key=" + Constants.API_KEY + "&language=en-US&page="
                    fetchShows = FetchShows(currentURL + page_count)
                    fetchShows.delegate = this@ShowFragment
                    fetchShows.execute()
                }
            }
            R.id.show_top_rated -> {
                if (!item.isChecked) {
                    page_count = 1
                    firstPageFlag = true
                    currentURL = "https://api.themoviedb.org/3/tv/top_rated?api_key=" + Constants.API_KEY + "&language=en-US&page="
                    fetchShows = FetchShows(currentURL + page_count)
                    fetchShows.delegate = this@ShowFragment
                    fetchShows.execute()
                }
            }
            R.id.shows_popular -> {
                if (!item.isChecked) {
                    page_count = 1
                    firstPageFlag = true
                    currentURL = "https://api.themoviedb.org/3/tv/popular?api_key=" + Constants.API_KEY + "&language=en-US&page="
                    fetchShows = FetchShows(currentURL + page_count)
                    fetchShows.delegate = this@ShowFragment
                    fetchShows.execute()
                }
            }
            else -> {
                if (!item.isChecked) {
                    page_count = 1
                    firstPageFlag = true
                    currentURL = ("https://api.themoviedb.org/3/discover/tv?api_key="
                            + Constants.API_KEY
                            + "&language=en-US&sort_by=popularity.desc&with_genres="
                            + Constants.genreID(item.title.toString())
                            + "&with_original_language=en&page=")
                    fetchShows = FetchShows(currentURL + page_count)
                    fetchShows.delegate = this@ShowFragment
                    fetchShows.execute()
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
        animation.start()
        blurView.visibility = View.INVISIBLE

        return true
    }


    override fun processFinish(output: ArrayList<ShowModel>, total_pages: Int) {
        shows = output
        if (firstPageFlag) {
            val columns = 3
            this.total_pages = total_pages
            val layoutManager = GridLayoutManager(context, columns)
            myAdapter = ShowsRecyclerViewAdapter(shows, context!!)
            this.recyclerView!!.layoutManager = layoutManager
            this.recyclerView!!.adapter = myAdapter
            firstPageFlag = false
        } else {
            myAdapter!!.addMoreItems(shows)
            myAdapter!!.notifyDataSetChanged()
        }
    }

    fun changeFabPosition() {
        val animation = ObjectAnimator.ofFloat(layout, "translationX", (-tpview.width).toFloat())
        animation.duration = 300
        animation.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                super.onAnimationEnd(animation)
                tpview.visibility = View.INVISIBLE
            }
        })
        animation.start()
        fab.setImageDrawable(ContextCompat.getDrawable(Objects.requireNonNull<Context>(context), R.drawable.ic_chevron_right_black_24dp))
    }
}// Required empty public constructor
