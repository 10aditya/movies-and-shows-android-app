package com.insomniacgks.newmoviesandshows.activities;

import android.app.SearchManager;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.CompoundButton;
import android.widget.SearchView;
import android.widget.Switch;

import com.insomniacgks.newmoviesandshows.R;
import com.insomniacgks.newmoviesandshows.data.Constants;
import com.insomniacgks.newmoviesandshows.fragments.MovieFragment;
import com.insomniacgks.newmoviesandshows.fragments.ShowFragment;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, SearchView.OnQueryTextListener, SearchView.OnCloseListener {


    private Fragment currentFragment;
    private SearchView searchView;
    private Switch darkMode;
    private SharedPreferences sharedPreferences;
    public static boolean isDarkMode = false;
    private Toolbar toolbar;
    private NavigationView navigationView;
    private MenuItem searchMenuItem = null;
    private Fragment movieFragment, tvFragment;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        isDarkMode = sharedPreferences.getBoolean("dark_mode", false);
        /*if (isDarkMode) {
            setTheme(R.style.AppThemeDark);
        }*/
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        currentFragment = movieFragment = new MovieFragment();
        tvFragment = new ShowFragment();

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        MenuItem menuItem = navigationView.getMenu().findItem(R.id.switch_layout);
        darkMode = menuItem.getActionView().findViewById(R.id.dark_mode);

        darkMode.setChecked(isDarkMode);

        darkMode.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SharedPreferences.Editor spe = sharedPreferences.edit();
                spe.putBoolean("dark_mode", isChecked);
                spe.apply();
                isDarkMode = isChecked;
                if (isChecked) {
                    turnOnDarkMode();
                } else {
                    turnOffDarkMode();
                }
            }
        });

        Constants.initializeGenres();
        Constants.initializeGenresR();

        if (isDarkMode) {
            turnOnDarkMode();
        } else {
            turnOffDarkMode();
        }

        navigationView.getMenu().getItem(0).setChecked(true);
    }

    void turnOnDarkMode() {
        navigationView.setItemTextColor(ColorStateList.valueOf(Color.WHITE));
        navigationView.setItemIconTintList(ColorStateList.valueOf(Color.WHITE));
        navigationView.setBackgroundColor(ContextCompat.getColor(this, R.color.colorPrimaryDarkTheme));
        getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.colorPrimaryDarkDarkTheme));
        toolbar.setTitleTextColor(ContextCompat.getColor(this, android.R.color.white));
        toolbar.setBackgroundColor(ContextCompat.getColor(this, R.color.colorPrimaryDarkTheme));
        toolbar.setNavigationIcon(R.drawable.ic_menu_white_24dp);
        if (currentFragment == movieFragment && currentFragment != null) {
            currentFragment.onDestroy();
            currentFragment = movieFragment = new MovieFragment();
            tvFragment = new ShowFragment();
            navigationView.setCheckedItem(R.id.movies);
            replaceFragment();
        } else if (currentFragment == tvFragment && currentFragment != null) {
            currentFragment.onDestroy();
            movieFragment = new MovieFragment();
            currentFragment = tvFragment = new ShowFragment();
            navigationView.setCheckedItem(R.id.tv_shows);
            replaceFragment();
        }
        if(searchMenuItem !=null) {
            searchMenuItem.setIcon(R.drawable.ic_search_white_24dp);
        }
        navigationView.setCheckedItem(R.id.movies);
    }

    void turnOffDarkMode() {
        navigationView.setItemTextColor(ColorStateList.valueOf(Color.rgb(66,66,66)));
        navigationView.setItemIconTintList(ColorStateList.valueOf(Color.rgb(66,66,66)));
        navigationView.setBackgroundColor(ContextCompat.getColor(this, android.R.color.white));
        setTheme(R.style.AppTheme_NoActionBar);
        toolbar.setBackgroundColor(ContextCompat.getColor(this, android.R.color.holo_blue_light));
        toolbar.setNavigationIcon(R.drawable.ic_menu_black_24dp);
        getWindow().setStatusBarColor(ContextCompat.getColor(this, android.R.color.holo_blue_dark));
        toolbar.setTitleTextColor(ContextCompat.getColor(this, android.R.color.black));
        if (currentFragment == movieFragment && currentFragment != null) {
            currentFragment.onDestroy();
            currentFragment = movieFragment = new MovieFragment();
            tvFragment = new ShowFragment();
            navigationView.setCheckedItem(R.id.movies);
            replaceFragment();
        } else if (currentFragment == tvFragment && currentFragment != null) {
            currentFragment.onDestroy();
            movieFragment = new MovieFragment();
            currentFragment = tvFragment = new ShowFragment();
            navigationView.setCheckedItem(R.id.tv_shows);
            replaceFragment();
        }
        if (searchMenuItem != null) {
            searchMenuItem.setIcon(R.drawable.ic_search_black_24dp);
        }
        navigationView.setCheckedItem(R.id.movies);
    }

    @Override
    protected void onStart() {
        super.onStart();

        /*darkMode.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    Toast.makeText(MainActivity.this, "hehe", Toast.LENGTH_SHORT).show();
                }
            }
        })*/

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else if (!this.searchView.isIconified()) {
            this.searchView.setIconified(true);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        searchMenuItem = menu.findItem(R.id.search);
        if(isDarkMode){searchMenuItem.setIcon(R.drawable.ic_search_white_24dp);}
        searchView = (SearchView) searchMenuItem.getActionView();
        searchView.setQueryHint("Search Movies and TV Shows...");
        this.searchView.setMaxWidth(Integer.MAX_VALUE);
        this.searchView.setOnQueryTextListener(this);
        this.searchView.setOnCloseListener(this);
        searchView.setOnQueryTextFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (hasFocus) {
                    showInputMethod(view.findFocus());
                }
            }
        });
        return super.onCreateOptionsMenu(menu);
    }

    private void showInputMethod(View view) {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.showSoftInput(view, 0);
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case R.id.movies: {
                currentFragment = movieFragment;
                replaceFragment();
                break;
            }
            case R.id.tv_shows: {
                currentFragment = tvFragment;
                replaceFragment();
                break;
            }
            case R.id.switch_layout: {
                darkMode.toggle();
                break;
            }
            case R.id.nav_share: {
                String appPackageName = getPackageName();
                Intent i = new Intent();
                i.setAction("android.intent.action.SEND");
                i.putExtra("android.intent.extra.TEXT", "Try \"Movies and Shows\" App to get information about Movies and TV shows!\nhttps://play.google.com/store/apps/details?id=" + appPackageName);
                i.setType("text/plain");
                startActivity(Intent.createChooser(i, "Share App"));
                break;
            }
            case R.id.nav_send: {
                String appPackageName = getPackageName();
                try {
                    startActivity(new Intent("android.intent.action.VIEW", Uri.parse("market://details?id=" + appPackageName)));
                } catch (ActivityNotFoundException e) {
                    startActivity(new Intent("android.intent.action.VIEW", Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
                }

                break;
            }
        }
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void replaceFragment() {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out);
        fragmentTransaction.replace(R.id.fragment_base, currentFragment);
        fragmentTransaction.commitAllowingStateLoss();
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        Intent intent = new Intent(this, SearchResultActivity.class);
        intent.putExtra("searchQuery", query);
        startActivity(intent);
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        return false;
    }

    @Override
    public boolean onClose() {
        return false;
    }
}
