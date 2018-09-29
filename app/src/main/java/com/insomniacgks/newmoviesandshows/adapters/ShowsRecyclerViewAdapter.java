package com.insomniacgks.newmoviesandshows.adapters;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.insomniacgks.newmoviesandshows.R;
import com.insomniacgks.newmoviesandshows.activities.MainActivity;
import com.insomniacgks.newmoviesandshows.activities.ShowDetailActivity;
import com.insomniacgks.newmoviesandshows.models.ShowModel;

import java.util.ArrayList;

import static com.bumptech.glide.load.resource.bitmap.BitmapTransitionOptions.withCrossFade;

public class ShowsRecyclerViewAdapter extends RecyclerView.Adapter<ShowsRecyclerViewAdapter.ViewHolder> {
    private static ArrayList<ShowModel> mShows;
    private Context context;

    public ShowsRecyclerViewAdapter(ArrayList<ShowModel> shows, Context context) {
        this.context = context;
        mShows = shows;
    }

    public void addMoreItems(ArrayList<ShowModel> moreShows) {
        mShows.addAll(moreShows);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
        return new ShowsRecyclerViewAdapter.ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.movie_show_card_layout, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ShowsRecyclerViewAdapter.ViewHolder holder, int position) {
        ShowModel show = mShows.get(position);
        if(MainActivity.isDarkMode){
            holder.titleBackground.setImageResource(R.drawable.wave_3);
            holder.title.setTextColor(Color.WHITE);
        }
        if (show.getPoster_url() != null) {
            holder.title.setText(show.getName());
            holder.title.setSelected(true);
            RequestOptions options = new RequestOptions()
                    .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                    .priority(Priority.HIGH);

            Glide.with(this.context)
                    .asBitmap()
                    .load(show.getPoster_url())
                    .apply(options)
                    .apply(new RequestOptions()
                            .diskCacheStrategy(DiskCacheStrategy.ALL)
                            .apply(new RequestOptions()
                                    .placeholder(this.context.getResources().getDrawable(R.drawable.gradient))))
                    .transition(withCrossFade())
                    .into(holder.showPoster);
        }
    }

    @Override
    public int getItemCount() {
        return (mShows != null) ? mShows.size() : 0;
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        ImageView showPoster,titleBackground;
        TextView title;

        ViewHolder(View itemView) {
            super(itemView);
            this.titleBackground = itemView.findViewById(R.id.title_background);
            this.title = itemView.findViewById(R.id.movie_name);
            this.showPoster = itemView.findViewById(R.id.poster);
            itemView.setOnClickListener(this);
        }

        public void onClick(View view) {
            ShowModel clickedItem = ShowsRecyclerViewAdapter.mShows.get(getAdapterPosition());
            final Intent i = new Intent(context, ShowDetailActivity.class);
            //final ActivityOptions options = ActivityOptions.makeScaleUpAnimation(view, 0, 0, view.getWidth(), view.getHeight());
            Bundle bundle = new Bundle();
            bundle.putSerializable("show", clickedItem);
            ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation((Activity) context,
                    view.findViewById(R.id.poster),
                    "testing");
            i.putExtras(bundle);

            context.startActivity(i, options.toBundle());
        }
    }
}
