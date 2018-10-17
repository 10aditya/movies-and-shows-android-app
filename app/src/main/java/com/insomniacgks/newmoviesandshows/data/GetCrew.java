package com.insomniacgks.newmoviesandshows.data;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
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

import static com.bumptech.glide.load.resource.bitmap.BitmapTransitionOptions.withCrossFade;

public class GetCrew extends AsyncTask<String, Void, ArrayList<String[]>> {
    private Context context;
    private int id;
    private RecyclerView recyclerView;
    private String type;
    private RelativeLayout rl;

    public GetCrew(Context context, int id, RecyclerView recyclerView, String type, RelativeLayout rl) {
        this.context = context;
        this.id = id;
        this.recyclerView = recyclerView;
        this.type = type;
        this.rl = rl;
    }

    protected ArrayList<String[]> doInBackground(String... strings) {
        ArrayList<String[]> Crews = new ArrayList<>();
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
            JSONArray resultArray = new JSONObject(builder.toString()).getJSONArray("crew");
            int i = 0;
            while (i < 8 && i < resultArray.length()) {
                JSONObject object = resultArray.getJSONObject(i);
                Crews.add(new String[]{object.getString("job"), object.getString("name"), object.getString("profile_path")});
                i++;
            }
        } catch (Exception ignored) {
        }
        return Crews;
    }

    protected void onPostExecute(ArrayList<String[]> strings) {
        super.onPostExecute(strings);
        if (strings != null) {
            if (strings.size() != 0) {
                RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this.context, LinearLayoutManager.HORIZONTAL, false);
                CrewsRecyclerViewAdapter myAdapter = new CrewsRecyclerViewAdapter(this.context, strings);
                recyclerView.setLayoutManager(layoutManager);
                recyclerView.setAdapter(myAdapter);
            } else {
                rl.setVisibility(View.GONE);
            }
        } else {
            rl.setVisibility(View.GONE);
        }
    }


    class CrewsRecyclerViewAdapter extends RecyclerView.Adapter<CrewsRecyclerViewAdapter.ViewHolder> {
        private ArrayList<String[]> Crews;
        private Context context;

        CrewsRecyclerViewAdapter(Context context, ArrayList<String[]> Crews) {
            this.context = context;
            this.Crews = Crews;
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
            holder.roll.setText(String.format("%s\n%s",
                    this.Crews.get(position)[1], this.Crews.get(position)[0]));

            RequestOptions options = new RequestOptions()
                    .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                    .priority(Priority.HIGH);
            if (!this.Crews.get(position)[2].equals("null") && this.Crews.get(position)[2] != null) {
                Glide.with(context)
                        .asBitmap()
                        .load(Constants.BASE_PROFILE_IMAGE_PATH + this.Crews.get(position)[2])
                        .apply(options)
                        .apply(new RequestOptions()
                                .diskCacheStrategy(DiskCacheStrategy.ALL)
                                .apply(new RequestOptions().placeholder(ContextCompat.getDrawable(context, R.drawable.profile))))
                        .listener(new RequestListener<Bitmap>() {
                            @Override
                            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Bitmap> target, boolean isFirstResource) {
                                return false;
                            }

                            @Override
                            public boolean onResourceReady(Bitmap resource, Object model, Target<Bitmap> target, DataSource dataSource, boolean isFirstResource) {
                                holder.ProfileImageView.setImageBitmap(resource);
                                return false;
                            }
                        }).transition(withCrossFade())
                        .into(holder.ProfileImageView);
            }
        }

        @Override
        public int getItemCount() {
            return Crews.size();
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