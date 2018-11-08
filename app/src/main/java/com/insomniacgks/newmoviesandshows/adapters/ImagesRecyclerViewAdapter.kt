package com.insomniacgks.newmoviesandshows.adapters

import android.content.Context
import android.content.Intent
import android.preference.PreferenceManager
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatDelegate
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout

import com.bumptech.glide.Glide
import com.bumptech.glide.Priority
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.insomniacgks.newmoviesandshows.R
import com.insomniacgks.newmoviesandshows.activities.ImageViewerActivity

import java.util.ArrayList

import com.bumptech.glide.load.resource.bitmap.BitmapTransitionOptions.withCrossFade

class ImagesRecyclerViewAdapter(private val context: Context, private val images: ArrayList<String>) : RecyclerView.Adapter<ImagesRecyclerViewAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImagesRecyclerViewAdapter.ViewHolder {
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true)
        return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.images_card_layout, parent, false))
    }

    override fun onBindViewHolder(holder: ImagesRecyclerViewAdapter.ViewHolder, position: Int) {
        val options = RequestOptions()
                .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                .priority(Priority.HIGH)
        if (PreferenceManager.getDefaultSharedPreferences(this.context).getBoolean("dark_mode", false)) {
            holder.layoutBack.setBackgroundColor(ContextCompat.getColor(this.context, R.color.black_theme_color))
        }
        Glide.with(context)
                .asBitmap()
                .load(images[position])
                .apply(options)
                .apply(RequestOptions()
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .apply(RequestOptions()
                                .placeholder(ContextCompat.getDrawable(this.context, R.drawable.gradient))))
                .transition(withCrossFade())
                .into(holder.backdrop)
    }

    override fun getItemCount(): Int {
        return images.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener {
        var backdrop: ImageView = itemView.findViewById(R.id.image_backdrop)
        var layoutBack: LinearLayout = itemView.findViewById(R.id.layout_back)

        init {
            backdrop.setOnClickListener(this)
        }

        override fun onClick(v: View) {
            context.startActivity(Intent(context, ImageViewerActivity::class.java))

        }
    }
}
