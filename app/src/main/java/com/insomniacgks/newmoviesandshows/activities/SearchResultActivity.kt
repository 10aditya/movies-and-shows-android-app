package com.insomniacgks.newmoviesandshows.activities

import android.os.Bundle
import android.support.design.widget.TabLayout
import android.support.design.widget.TabLayout.OnTabSelectedListener
import android.support.design.widget.TabLayout.Tab
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import android.support.v4.view.ViewPager
import android.support.v7.app.AppCompatActivity

import com.insomniacgks.newmoviesandshows.R
import com.insomniacgks.newmoviesandshows.fragments.SearchMovieResultsFragment
import com.insomniacgks.newmoviesandshows.fragments.SearchShowResultsFragment

class SearchResultActivity : AppCompatActivity(), OnTabSelectedListener {
    private var searchQuery: String? = null
    private var tabLayout: TabLayout? = null
    private var viewPager: ViewPager? = null

    internal inner class ViewPageAdapter(fm: FragmentManager) : FragmentPagerAdapter(fm) {
        var searchMovieResultsFragment: SearchMovieResultsFragment
        var searchShowResultsFragment: SearchShowResultsFragment

        init {
            searchMovieResultsFragment = SearchMovieResultsFragment()
            val bundle = Bundle()
            bundle.putString("searchQuery", searchQuery)
            searchMovieResultsFragment.arguments = bundle
            this.searchShowResultsFragment = SearchShowResultsFragment()
            searchShowResultsFragment.arguments = bundle
        }

        override fun getPageTitle(position: Int): CharSequence? {
            return if (position == 0) "Movies" else "TV Shows"
        }

        override fun getItem(position: Int): Fragment {
            return if (position == 0) this.searchMovieResultsFragment else this.searchShowResultsFragment
        }

        override fun getCount(): Int {
            return 2
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.fragment_search_results)
        this.viewPager = findViewById(R.id.search_vp)
        this.tabLayout = findViewById(R.id.search_tl)
        this.searchQuery = intent.getStringExtra("searchQuery").trim { it <= ' ' }
        supportActionBar!!.title = "Search results for '" + this.searchQuery + "'"
        this.tabLayout!!.addTab(this.tabLayout!!.newTab().setText("Movies"))
        this.tabLayout!!.addTab(this.tabLayout!!.newTab().setText("TV Shows"))
        this.viewPager!!.adapter = ViewPageAdapter(supportFragmentManager)
        this.tabLayout!!.addOnTabSelectedListener(this)
    }

    override fun onTabSelected(tab: Tab) {
        this.viewPager!!.currentItem = tab.position
    }

    override fun onTabUnselected(tab: Tab) {}

    override fun onTabReselected(tab: Tab) {}
}
