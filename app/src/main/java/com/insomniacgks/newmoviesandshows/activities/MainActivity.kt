package com.insomniacgks.newmoviesandshows.activities

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.res.ColorStateList
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.preference.PreferenceManager
import android.support.design.widget.NavigationView
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.support.v4.view.GravityCompat
import android.support.v4.widget.DrawerLayout
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.SearchView
import android.widget.Switch
import com.insomniacgks.newmoviesandshows.R
import com.insomniacgks.newmoviesandshows.data.Constants
import com.insomniacgks.newmoviesandshows.fragments.MovieFragment
import com.insomniacgks.newmoviesandshows.fragments.ShowFragment

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener, SearchView.OnQueryTextListener, SearchView.OnCloseListener {


    private var currentFragment: Fragment? = null
    private var searchView: SearchView? = null
    private var darkMode: Switch? = null
    private var sharedPreferences: SharedPreferences? = null
    private var toolbar: Toolbar? = null
    private var navigationView: NavigationView? = null
    private var searchMenuItem: MenuItem? = null
    private var movieFragment: Fragment? = null
    private var tvFragment: Fragment? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)
        isDarkMode = sharedPreferences!!.getBoolean("dark_mode", false)
        /*if (isDarkMode) {
            setTheme(R.style.AppThemeDark);
        }*/
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        movieFragment = MovieFragment()
        currentFragment = movieFragment
        tvFragment = ShowFragment()

        val drawer = findViewById<DrawerLayout>(R.id.drawer_layout)
        val toggle = ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        drawer.addDrawerListener(toggle)
        toggle.syncState()

        navigationView = findViewById(R.id.nav_view)
        navigationView!!.setNavigationItemSelectedListener(this)

        val menuItem = navigationView!!.menu.findItem(R.id.switch_layout)
        darkMode = menuItem.actionView.findViewById(R.id.dark_mode)

        darkMode!!.isChecked = isDarkMode

        darkMode!!.setOnCheckedChangeListener { buttonView, isChecked ->
            val spe = sharedPreferences!!.edit()
            spe.putBoolean("dark_mode", isChecked)
            spe.apply()
            isDarkMode = isChecked
            if (isChecked) {
                turnOnDarkMode()
            } else {
                turnOffDarkMode()
            }
        }

        Constants.initializeGenres()
        Constants.initializeGenresR()

        if (isDarkMode) {
            turnOnDarkMode()
        } else {
            turnOffDarkMode()
        }

        navigationView!!.menu.getItem(0).isChecked = true
    }

    private fun turnOnDarkMode() {
        navigationView!!.itemTextColor = ColorStateList.valueOf(Color.WHITE)
        navigationView!!.itemIconTintList = ColorStateList.valueOf(Color.WHITE)
        navigationView!!.setBackgroundColor(ContextCompat.getColor(this, R.color.colorPrimaryDarkTheme))
        window.statusBarColor = ContextCompat.getColor(this, R.color.colorPrimaryDarkDarkTheme)
        toolbar!!.setTitleTextColor(ContextCompat.getColor(this, android.R.color.white))
        toolbar!!.setBackgroundColor(ContextCompat.getColor(this, R.color.colorPrimaryDarkTheme))
        toolbar!!.setNavigationIcon(R.drawable.ic_menu_white_24dp)
        if (currentFragment === movieFragment && currentFragment != null) {
            currentFragment!!.onDestroy()
            movieFragment = MovieFragment()
            currentFragment = movieFragment
            tvFragment = ShowFragment()
            navigationView!!.setCheckedItem(R.id.movies)
            replaceFragment()
        } else if (currentFragment === tvFragment && currentFragment != null) {
            currentFragment!!.onDestroy()
            movieFragment = MovieFragment()
            tvFragment = ShowFragment()
            currentFragment = tvFragment
            navigationView!!.setCheckedItem(R.id.tv_shows)
            replaceFragment()
        }
        if (searchMenuItem != null) {
            searchMenuItem!!.setIcon(R.drawable.ic_search_white_24dp)
        }
        navigationView!!.setCheckedItem(R.id.movies)
    }

    private fun turnOffDarkMode() {
        navigationView!!.itemTextColor = ColorStateList.valueOf(Color.rgb(66, 66, 66))
        navigationView!!.itemIconTintList = ColorStateList.valueOf(Color.rgb(66, 66, 66))
        navigationView!!.setBackgroundColor(ContextCompat.getColor(this, android.R.color.white))
        setTheme(R.style.AppTheme_NoActionBar)
        toolbar!!.setBackgroundColor(ContextCompat.getColor(this, android.R.color.holo_blue_light))
        toolbar!!.setNavigationIcon(R.drawable.ic_menu_black_24dp)
        window.statusBarColor = ContextCompat.getColor(this, android.R.color.holo_blue_dark)
        toolbar!!.setTitleTextColor(ContextCompat.getColor(this, android.R.color.black))
        if (currentFragment === movieFragment && currentFragment != null) {
            currentFragment!!.onDestroy()
            movieFragment = MovieFragment()
            currentFragment = movieFragment
            tvFragment = ShowFragment()
            navigationView!!.setCheckedItem(R.id.movies)
            replaceFragment()
        } else if (currentFragment === tvFragment && currentFragment != null) {
            currentFragment!!.onDestroy()
            movieFragment = MovieFragment()
            tvFragment = ShowFragment()
            currentFragment = tvFragment
            navigationView!!.setCheckedItem(R.id.tv_shows)
            replaceFragment()
        }
        if (searchMenuItem != null) {
            searchMenuItem!!.setIcon(R.drawable.ic_search_black_24dp)
        }
        navigationView!!.setCheckedItem(R.id.movies)
    }

    override fun onBackPressed() {
        val drawer = findViewById<DrawerLayout>(R.id.drawer_layout)
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START)
        } else if (!this.searchView!!.isIconified) {
            this.searchView!!.isIconified = true
        } else {
            super.onBackPressed()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main, menu)
        //val searchManager = getSystemService(Context.SEARCH_SERVICE) as SearchManager
        searchMenuItem = menu.findItem(R.id.search)
        if (isDarkMode) {
            searchMenuItem!!.setIcon(R.drawable.ic_search_white_24dp)
        }
        searchView = searchMenuItem!!.actionView as SearchView
        searchView!!.queryHint = "Search Movies and TV Shows..."
        this.searchView!!.maxWidth = Integer.MAX_VALUE
        this.searchView!!.setOnQueryTextListener(this)
        this.searchView!!.setOnCloseListener(this)
        searchView!!.setOnQueryTextFocusChangeListener { view, hasFocus ->
            if (hasFocus) {
                showInputMethod(view.findFocus())
            }
        }
        return super.onCreateOptionsMenu(menu)
    }

    private fun showInputMethod(view: View) {
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.showSoftInput(view, 0)
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        val id = item.itemId

        when (id) {
            R.id.movies -> {
                currentFragment = movieFragment
                replaceFragment()
            }
            R.id.tv_shows -> {
                currentFragment = tvFragment
                replaceFragment()
            }
            R.id.switch_layout -> {
                darkMode!!.toggle()
            }
            R.id.nav_share -> {
                val appPackageName = packageName
                val i = Intent()
                i.action = "android.intent.action.SEND"
                i.putExtra("android.intent.extra.TEXT", "Try \"Movies and Shows\" App to get information about Movies and TV shows!\nhttps://play.google.com/store/apps/details?id=$appPackageName")
                i.type = "text/plain"
                startActivity(Intent.createChooser(i, "Share App"))
            }
            R.id.nav_send -> {
                val appPackageName = packageName
                try {
                    startActivity(Intent("android.intent.action.VIEW", Uri.parse("market://details?id=$appPackageName")))
                } catch (e: ActivityNotFoundException) {
                    startActivity(Intent("android.intent.action.VIEW", Uri.parse("https://play.google.com/store/apps/details?id=$appPackageName")))
                }

            }
        }
        val drawer = findViewById<DrawerLayout>(R.id.drawer_layout)
        drawer.closeDrawer(GravityCompat.START)
        return true
    }

    private fun replaceFragment() {
        val fragmentTransaction = supportFragmentManager.beginTransaction()
        fragmentTransaction.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out)
        fragmentTransaction.replace(R.id.fragment_base, currentFragment!!)
        fragmentTransaction.commitAllowingStateLoss()
    }

    override fun onQueryTextSubmit(query: String): Boolean {
        val intent = Intent(this, SearchResultActivity::class.java)
        intent.putExtra("searchQuery", query)
        startActivity(intent)
        return false
    }

    override fun onQueryTextChange(newText: String): Boolean {
        return false
    }

    override fun onClose(): Boolean {
        return false
    }

    companion object {
        var isDarkMode = false
    }
}
