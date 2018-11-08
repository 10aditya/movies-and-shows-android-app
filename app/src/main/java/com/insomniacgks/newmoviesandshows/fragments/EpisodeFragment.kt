package com.insomniacgks.newmoviesandshows.fragments

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.text.method.ScrollingMovementMethod
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
import com.insomniacgks.newmoviesandshows.data.Constants
import java.util.*

class EpisodeFragment : Fragment() {
    private var episode: Array<String>? = null


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val v = inflater.inflate(R.layout.fragment_episode, container, false)
        val episodeAirDate = v.findViewById<TextView>(R.id.episode_air_date)
        val episodeOverview = v.findViewById<TextView>(R.id.episode_overview)
        val episodeStill = v.findViewById<ImageView>(R.id.episode_still)
        episode = Objects.requireNonNull<Bundle>(arguments).getStringArray("episode")

        (v.findViewById<View>(R.id.episode_name) as TextView).text = this.episode!![0]
        episodeOverview.text = this.episode!![1]
        episodeAirDate.text = this.episode!![2]
        episodeOverview.movementMethod = ScrollingMovementMethod()

        val options = RequestOptions()
                .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                .priority(Priority.HIGH)

        Glide.with(this)
                .asBitmap()
                .load(Constants.BASE_POSTER_PATH.substring(0, Constants.BASE_POSTER_PATH.length - 3) + "500" + this.episode!![3])
                .apply(options)
                .apply(RequestOptions()
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .apply(RequestOptions()
                                .placeholder(ContextCompat.getDrawable(context!!, R.drawable.gradient))))
                .transition(withCrossFade())
                .into(episodeStill)

        return v
    }
}
