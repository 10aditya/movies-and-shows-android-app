package com.insomniacgks.newmoviesandshows.data;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.insomniacgks.newmoviesandshows.R;
import com.mikhaellopez.circularimageview.CircularImageView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;

import static com.bumptech.glide.load.resource.bitmap.BitmapTransitionOptions.withCrossFade;

public class GetCast extends AsyncTask<String, Void, ArrayList<String[]>> {
    @SuppressLint("StaticFieldLeak")
    private Context context;
    private int id;
    @SuppressLint("StaticFieldLeak")
    private RecyclerView recyclerView;
    private String type;
    @SuppressLint("StaticFieldLeak")
    private RelativeLayout relativeLayout;

    public GetCast(Context context, int id, RecyclerView recyclerView, String type, RelativeLayout relativeLayout) {
        this.context = context;
        this.id = id;
        this.recyclerView = recyclerView;
        this.type = type;
        this.relativeLayout = relativeLayout;
    }

    protected ArrayList<String[]> doInBackground(String... strings) {
        ArrayList<String[]> casts = new ArrayList<>();
        try {
            HttpURLConnection client = (HttpURLConnection) new URL("http://api.themoviedb.org/3/" + type + "/" + id + "/credits?api_key=" + Constants.API_KEY + "&language=en-US").openConnection();
            client.setRequestMethod("GET");
            client.connect();
            BufferedReader reader = new BufferedReader(new InputStreamReader(client.getInputStream()));
            StringBuilder builder = new StringBuilder();
            while (true) {
                String line = reader.readLine();
                if (line == null) {
                    break;
                }
                builder.append(line);
            }
            JSONArray resultArray = new JSONObject(builder.toString()).getJSONArray("cast");
            int i = 0;
            while (i < 10 && i < resultArray.length()) {
                JSONObject object = resultArray.getJSONObject(i);
                casts.add(new String[]{object.getString("character"), object.getString("name"), object.getString("profile_path")});
                i++;
            }
        } catch (Exception ignored) {
        }
        return casts;
    }

    protected void onPostExecute(ArrayList<String[]> strings) {
        super.onPostExecute(strings);
        if (strings != null) {
            if (strings.size() != 0) {
                RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this.context, LinearLayoutManager.HORIZONTAL, false);
                CastsRecyclerViewAdapter myAdapter = new CastsRecyclerViewAdapter(this.context, strings);
                recyclerView.setLayoutManager(layoutManager);
                recyclerView.setAdapter(myAdapter);
            } else {
                relativeLayout.setVisibility(View.GONE);
            }
        } else {
            relativeLayout.setVisibility(View.GONE);
        }
    }


    class CastsRecyclerViewAdapter extends RecyclerView.Adapter<CastsRecyclerViewAdapter.ViewHolder> {
        private ArrayList<String[]> casts;
        private Context context;
        private boolean isDarkModeOn;

        CastsRecyclerViewAdapter(Context context, ArrayList<String[]> casts) {
            this.context = context;
            this.casts = casts;
            this.isDarkModeOn = PreferenceManager.getDefaultSharedPreferences(context).getBoolean("dark_mode", false);
        }


        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
            return new ViewHolder(LayoutInflater.
                    from(parent.getContext()).inflate(R.layout.people_card_layout, parent, false));
        }

        @Override
        public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
            if(isDarkModeOn){
                holder.LayoutBack.setBackgroundColor(ContextCompat.getColor(context, R.color.black_theme_color));
                holder.roll.setTextColor(Color.WHITE);
            }
            holder.roll.setText(String.format("%s\nas\n%s",
                    this.casts.get(position)[1], this.casts.get(position)[0]));

            RequestOptions options = new RequestOptions()
                    .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                    .priority(Priority.HIGH);
            if (!this.casts.get(position)[2].equals("null") && this.casts.get(position)[2] != null) {
                Glide.with(context)
                        .asBitmap()
                        .load(Constants.BASE_PROFILE_IMAGE_PATH + this.casts.get(position)[2])
                        .apply(options)
                        .apply(new RequestOptions()
                                .diskCacheStrategy(DiskCacheStrategy.ALL))
                        .apply(new RequestOptions().placeholder(ContextCompat.getDrawable(context, R.drawable.profile)))
                        .listener(new RequestListener<Bitmap>() {
                            @Override
                            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Bitmap> target, boolean isFirstResource) {
                                return false;
                            }

                            @Override
                            public boolean onResourceReady(Bitmap resource, Object model, Target<Bitmap> target, DataSource dataSource, boolean isFirstResource) {
                                Log.i("profile image download:","complete");
                                holder.ProfileImageView.setImageBitmap(resource);
                                return false;
                            }
                        })
                        .transition(withCrossFade())
                        .into(holder.ProfileImageView);
            }
        }

        @Override
        public int getItemCount() {
            return casts.size();
        }

        class ViewHolder extends android.support.v7.widget.RecyclerView.ViewHolder {
            CircularImageView ProfileImageView;
            LinearLayout LayoutBack;
            TextView roll;

            ViewHolder(View itemView) {
                super(itemView);
                this.LayoutBack = itemView.findViewById(R.id.layout_back);
                this.ProfileImageView = itemView.findViewById(R.id.profile_image);
                this.roll = itemView.findViewById(R.id.roll);
            }

        }
    }
}