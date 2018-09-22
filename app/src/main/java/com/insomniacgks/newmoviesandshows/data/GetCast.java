package com.insomniacgks.newmoviesandshows.data;

import android.content.Context;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.insomniacgks.newmoviesandshows.R;
import com.mikhaellopez.circularimageview.CircularImageView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import static com.bumptech.glide.load.resource.bitmap.BitmapTransitionOptions.withCrossFade;

public class GetCast extends AsyncTask<String, Void, ArrayList<String[]>> {
    private Context context;
    private int id;
    private RecyclerView recyclerView;
    private String type;
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

        CastsRecyclerViewAdapter(Context context, ArrayList<String[]> casts) {
            this.context = context;
            this.casts = casts;
        }


        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
            return new ViewHolder(LayoutInflater.
                    from(parent.getContext()).inflate(R.layout.people_card_layout, parent, false));
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
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

            TextView roll;

            ViewHolder(View itemView) {
                super(itemView);
                this.ProfileImageView = itemView.findViewById(R.id.profile_image);
                this.roll = itemView.findViewById(R.id.roll);
            }

        }
    }
}