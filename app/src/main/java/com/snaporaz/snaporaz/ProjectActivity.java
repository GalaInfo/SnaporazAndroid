package com.snaporaz.snaporaz;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;

import cz.msebera.android.httpclient.Header;

public class ProjectActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.project);
        getSupportActionBar().hide();
        int id = getIntent().getIntExtra("PROJECT_ID", -1);
        Log.e("PROJECT_ID", "" + id);
        SnaporazRestClient.getProjectById(id, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                Log.e("JSON", "JSONObject");
                Log.e("response", response.toString());
                try {
                    ((TextView) findViewById(R.id.title)).setText(response.getString("title"));
                    ((TextView) findViewById(R.id.genres)).setText(response.getString("genres"));
                    ((TextView) findViewById(R.id.donations)).setText(response.getString("actual") + "€ / " + response.getString("min") + "€");
                    ((ProgressBar) findViewById(R.id.progress_bar)).setProgress(new Double(Math.min(100, response.getDouble("actual") / response.getDouble("min") * 100)).intValue());
                    ((TextView) findViewById(R.id.plot)).setText(response.getString("plot"));
                    ((TextView) findViewById(R.id.prizes)).setText(response.getString("prizes"));

                    new DownloadImageTask((ImageView) findViewById(R.id.thumbnail)).execute(response.getString("image"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                Log.e("JSON", "JSONArray");
                Log.e("response", response.toString());
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String s, Throwable throwable) {
                Log.e("Failure", s);
            }
        });
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
