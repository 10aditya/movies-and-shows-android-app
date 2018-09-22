package com.insomniacgks.newmoviesandshows.activities;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.design.widget.TabLayout.OnTabSelectedListener;
import android.support.design.widget.TabLayout.Tab;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;

import com.insomniacgks.newmoviesandshows.R;
import com.insomniacgks.newmoviesandshows.fragments.SearchMovieResultsFragment;
import com.insomniacgks.newmoviesandshows.fragments.SearchShowResultsFragment;

public class SearchResultActivity extends AppCompatActivity implements OnTabSelectedListener {
    private String searchQuery;
    private TabLayout tabLayout;
    private ViewPager viewPager;

    class ViewPageAdapter extends FragmentPagerAdapter {
        SearchMovieResultsFragment searchMovieResultsFragment;
        SearchShowResultsFragment searchShowResultsFragment;

        ViewPageAdapter(FragmentManager fm) {
            super(fm);
            searchMovieResultsFragment = new SearchMovieResultsFragment();
            Bundle bundle = new Bundle();
            bundle.putString("searchQuery", searchQuery);
            searchMovieResultsFragment.setArguments(bundle);
            this.searchShowResultsFragment = new SearchShowResultsFragment();
            searchShowResultsFragment.setArguments(bundle);
        }

        @Nullable
        public CharSequence getPageTitle(int position) {
            return position == 0 ? "Movies" : "TV Shows";
        }

        public Fragment getItem(int position) {
            return position == 0 ? this.searchMovieResultsFragment : this.searchShowResultsFragment;
        }

        public int getCount() {
            return 2;
        }
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_search_results);
        this.viewPager = findViewById(R.id.search_vp);
        this.tabLayout = findViewById(R.id.search_tl);
        this.searchQuery = getIntent().getStringExtra("searchQuery").trim();
        getSupportActionBar().setTitle("Search results for '" + this.searchQuery + "'");
        this.tabLayout.addTab(this.tabLayout.newTab().setText("Movies"));
        this.tabLayout.addTab(this.tabLayout.newTab().setText("TV Shows"));
        this.viewPager.setAdapter(new ViewPageAdapter(getSupportFragmentManager()));
        this.tabLayout.addOnTabSelectedListener(this);
    }

    public void onTabSelected(Tab tab) {
        this.viewPager.setCurrentItem(tab.getPosition());
    }

    public void onTabUnselected(Tab tab) {
    }

    public void onTabReselected(Tab tab) {
    }
}
