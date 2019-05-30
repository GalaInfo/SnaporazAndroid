package com.snaporaz.snaporaz;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.util.List;

public class MoviesAdapter extends RecyclerView.Adapter<MoviesAdapter.MyViewHolder> {

    private Context mContext;
    private JSONArray moviesList;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView title, genres;
        public ImageView thumbnail;
        public ProgressBar progressBar;
        public int id;

        public MyViewHolder(View view) {
            super(view);
            title = (TextView) view.findViewById(R.id.title);
            genres = (TextView) view.findViewById(R.id.genres);
            thumbnail = (ImageView) view.findViewById(R.id.thumbnail);
            progressBar = (ProgressBar) view.findViewById(R.id.progress_bar);
            id = -1;

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.e("id", "" + id);
                    Intent intent = new Intent(mContext, ProjectActivity.class);
                    intent.putExtra("PROJECT_ID", id);
                    mContext.startActivity(intent);
                }
            });
        }
    }

    public MoviesAdapter(Context mContext, JSONArray moviesList) {
        this.mContext = mContext;
        this.moviesList = moviesList;
    }

    public MoviesAdapter(Context mContext) {
        this.mContext = mContext;
        this.moviesList = new JSONArray();
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.movie_card, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {
        JSONObject movie;
        try {
            movie = moviesList.getJSONObject(position);
            Log.e("movie", moviesList.getJSONObject(position).toString());
            holder.title.setText(movie.getString("title"));
            holder.genres.setText(movie.getString("genres"));

            int progress = new Double(Math.min(100, movie.getDouble("actual") / movie.getDouble("min") * 100)).intValue();
            Log.e("Progress", "" + progress);
            holder.progressBar.setProgress(progress);

            holder.id = movie.getInt("id");

            new DownloadImageTask(holder.thumbnail).execute(movie.getString("image"));

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        Log.e("count", moviesList.length() + "");
        return moviesList.length();
    }

    public void setMoviesList(JSONArray moviesList) {
        this.moviesList = moviesList;
    }

    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;

        public DownloadImageTask(ImageView bmImage) {
            this.bmImage = bmImage;
        }

        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            Bitmap image = null;
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                image = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return image;
        }

        protected void onPostExecute(Bitmap result) {
            bmImage.setImageBitmap(result);
        }
    }

}
