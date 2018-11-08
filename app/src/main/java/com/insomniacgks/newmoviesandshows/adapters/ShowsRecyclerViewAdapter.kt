package com.insomniacgks.newmoviesandshows.adapters

import android.app.Activity
import android.app.ActivityOptions
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatDelegate
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.bumptech.glide.Priority
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.bitmap.BitmapTransitionOptions.withCrossFade
import com.bumptech.glide.request.RequestOptions
import com.insomniacgks.newmoviesandshows.R
import com.insomniacgks.newmoviesandshows.activities.MainActivity
import com.insomniacgks.newmoviesandshows.activities.ShowDetailActivity
import com.insomniacgks.newmoviesandshows.adapters.ShowsRecyclerViewAdapter.ViewHolder
import com.insomniacgks.newmoviesandshows.models.ShowModel
import java.util.*

class ShowsRecyclerViewAdapter(shows: ArrayList<ShowModel>, private val context: Context) : RecyclerView.Adapter<ViewHolder>() {

    init {
        mShows = shows
    }

    fun addMoreItems(moreShows: ArrayList<ShowModel>) {
        mShows!!.addAll(moreShows)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true)
        return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.movie_show_card_layout, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val show = mShows!![position]
        if (MainActivity.isDarkMode) {
            holder.titleBackground.setImageResource(R.drawable.wave_3)
            holder.title.setTextColor(Color.WHITE)
        }
        if (show.poster_url != "null" && show.poster_url!="") {
            holder.title.text = show.name
            holder.title.isSelected = true
            val options = RequestOptions()
                    .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                    .priority(Priority.HIGH)

            Glide.with(this.context)
                    .asBitmap()
                    .load(show.poster_url)
                    .apply(options)
                    .apply(RequestOptions()
                            .diskCacheStrategy(DiskCacheStrategy.ALL)
                            .apply(RequestOptions()
                                    .placeholder(ContextCompat.getDrawable(this.context, R.drawable.gradient))))
                    .transition(withCrossFade())
                    .into(holder.showPoster)
        }
    }

    override fun getItemCount(): Int {
        return if (mShows != null) mShows!!.size else 0
    }

    inner class ViewHolder internal constructor(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener {
        internal var showPoster: ImageView = itemView.findViewById(R.id.movie_name)
        internal var titleBackground: ImageView = itemView.findViewById(R.id.title_background)
        internal var title: TextView = itemView.findViewById(R.id.poster)

        init {
            itemView.setOnClickListener(this)
        }

        override fun onClick(view: View) {
            val clickedItem = ShowsRecyclerViewAdapter.mShows!![adapterPosition]
            val i = Intent(context, ShowDetailActivity::class.java)
            //final ActivityOptions options = ActivityOptions.makeScaleUpAnimation(view, 0, 0, view.getWidth(), view.getHeight());
            val bundle = Bundle()
            bundle.putSerializable("show", clickedItem)
            val options = ActivityOptions.makeSceneTransitionAnimation(context as Activity,
                    view.findViewById(R.id.poster),
                    "testing")
            i.putExtras(bundle)

            context.startActivity(i, options.toBundle())
        }
    }

    companion object {
        private var mShows: ArrayList<ShowModel>? = null
    }
}
