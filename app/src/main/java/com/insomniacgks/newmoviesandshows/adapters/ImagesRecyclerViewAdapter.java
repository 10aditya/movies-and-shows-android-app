package com.insomniacgks.newmoviesandshows.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.insomniacgks.newmoviesandshows.R;
import com.insomniacgks.newmoviesandshows.activities.ImageViewerActivity;

import java.util.ArrayList;

import static com.bumptech.glide.load.resource.bitmap.BitmapTransitionOptions.withCrossFade;

public class ImagesRecyclerViewAdapter extends RecyclerView.Adapter<ImagesRecyclerViewAdapter.ViewHolder> {

    private Context context;
    private ArrayList<String> images;

    public ImagesRecyclerViewAdapter(Context context, ArrayList<String> images) {
        this.images = images;
        this.context = context;
    }

    @NonNull
    @Override
    public ImagesRecyclerViewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.images_card_layout, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ImagesRecyclerViewAdapter.ViewHolder holder, int position) {
        RequestOptions options = new RequestOptions()
                .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                .priority(Priority.HIGH);

        Glide.with(context)
                .asBitmap()
                .load(images.get(position))
                .apply(options)
                .apply(new RequestOptions()
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .apply(new RequestOptions()
                                .placeholder(this.context.getResources().getDrawable(R.drawable.gradient))))
                .transition(withCrossFade())
                .into(holder.backdrop);
    }

    @Override
    public int getItemCount() {
        return images.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        ImageView backdrop;

        ViewHolder(View itemView) {
            super(itemView);
            backdrop = itemView.findViewById(R.id.image_backdrop);
            backdrop.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            context.startActivity(new Intent(context, ImageViewerActivity.class));
            
        }
    }
}
