package com.snaporaz.snaporaz;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.widget.TextView;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.loopj.android.http.*;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private static final int RC_SIGN_IN = 9001;
    private SearchView searchView;
    private RecyclerView recyclerView;
    private MoviesAdapter moviesAdapter;
    private GoogleSignInClient gsc;
    private SignInButton login;
    private Button logout;
    private Button proj;
    private Button collab;
    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().hide();
        login = findViewById(R.id.login_button);
        logout = findViewById(R.id.logout_button);
        proj = findViewById(R.id.proj_button);
        collab = findViewById(R.id.collab_button);
        login.setOnClickListener(this);
        logout.setOnClickListener(this);
        proj.setOnClickListener(this);
        collab.setOnClickListener(this);
        changeUI(false);
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestIdToken(getString(R.string.server_client_id)).build();
        gsc = GoogleSignIn.getClient(this, gso);
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
        //UTENTE GIÀ LOGGATO
        if (account != null) {
            SnaporazRestClient.login(account.getIdToken(), new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    // If the response is JSONObject instead of expected JSONArray
                    Log.e("JSON", "JSONObject");
                    Log.e("response", response.toString());
                }
            });
            changeUI(true);
        }
        /*
        getSupportActionBar().setLogo(R.drawable.logo);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayUseLogoEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
*/
        // show The Image in a ImageView
        searchView = findViewById(R.id.search);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                Log.e("query", query);
                SnaporazRestClient.listProjectsByTitle(query, new JsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                        // If the response is JSONObject instead of expected JSONArray
                    }

                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                        moviesAdapter.setMoviesList(response);
                        moviesAdapter.notifyDataSetChanged();
                    }
                });
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {

                return false;
            }
        });

        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(this, 1);
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(mLayoutManager);
        moviesAdapter = new MoviesAdapter(this);
        recyclerView.setAdapter(moviesAdapter);
        Log.e("Before","mainPage");
        SnaporazRestClient.mainPage(new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response){
                Log.e("Main", "Page");
                try {
                    moviesAdapter.setMoviesList(response.getJSONArray("mostFundedProjects"));
                    moviesAdapter.notifyDataSetChanged();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void changeUI(boolean logged) {
        if (logged) {
            login.setVisibility(View.GONE);
            logout.setVisibility(View.VISIBLE);
            proj.setVisibility(View.VISIBLE);
            collab.setVisibility(View.VISIBLE);
        } else {
            login.setVisibility(View.VISIBLE);
            logout.setVisibility(View.GONE);
            proj.setVisibility(View.GONE);
            collab.setVisibility(View.GONE);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.login_button:
                signIn();
                changeUI(true);
            case R.id.logout_button:
                gsc.signOut();
                userId = "";
                changeUI(false);
                break;
            case R.id.proj_button:
                SnaporazRestClient.getProjectByOwner(userId, new JsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject response){
                        try {
                            moviesAdapter.setMoviesList(response.getJSONArray("mostFundedProjects"));
                            moviesAdapter.notifyDataSetChanged();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
                break;
            case R.id.collab_button:
                SnaporazRestClient.getProjectByCollab(userId, new JsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject response){
                        try {
                            moviesAdapter.setMoviesList(response.getJSONArray("mostFundedProjects"));
                            moviesAdapter.notifyDataSetChanged();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
                break;
        }
    }

    private void signIn() {
        Log.e("Sign in","True");
        Intent signInIntent = gsc.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.e("onActivity","True");

        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            // The Task returned from this call is always completed, no need to attach
            // a listener.
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }
    }

    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            final GoogleSignInAccount account = completedTask.getResult(ApiException.class);
            userId = account.getIdToken();
            SnaporazRestClient.login(userId, new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    Log.e("JSON", "JSONObject");
                    Log.e("response", response.toString());
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                    Log.e("Error","sign in");
                    super.onFailure(statusCode, headers, throwable, errorResponse);
                }
            });
            changeUI(true);
        } catch (ApiException e) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            Log.e("LoginException",e.getStatusCode() + "");
        }
    }
}