package com.example.fmonsalve.flicks;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.Toast;

import com.example.fmonsalve.flicks.models.Config;
import com.example.fmonsalve.flicks.models.Movie;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;

public class MovieListActivity extends AppCompatActivity {

    //constants
    public final static String API_BASE_URL="https://api.themoviedb.org/3";
    //parameter name for API key
    public final static String API_KEY_PARAM="api_key";
    //tag for logging from this activity
    public final String TAG="MovieListActivity";

    //instance field
    AsyncHttpClient client;
    //list of currently playing movies
    ArrayList<Movie> movies;
    // the recycler view
    RecyclerView rvMovies;
    // the adapter wired to the recycler view
    MovieAdapter adapter;
    //image config
    Config config;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_list);
        //init client
        client=new AsyncHttpClient();
        //init list of movies
        movies=new ArrayList<>();
        //initialize adapter -- movies arraylist cannot be reinitialized after this point
        adapter=new MovieAdapter(movies);

        //resolve the recycler view and connect a layout manager and the adapter
        rvMovies= (RecyclerView) findViewById(R.id.rvMovies);
        rvMovies.setLayoutManager(new LinearLayoutManager(this));
        rvMovies.setAdapter(adapter);

        //get the configuration
        getConfiguration();
    }

    //get list of currently playing movies from the API
    private void getNowPlaying(){
        //create url to acces
        String url=API_BASE_URL+"/movie/now_playing";
        //set parameters for request to API
        RequestParams params=new RequestParams();
        params.put(API_KEY_PARAM,getString(R.string.api_key)); //API key param: always required
        //execute get request that expects a configuration JSON file
        client.get(url,params,new JsonHttpResponseHandler(){
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                try {
                    JSONArray results= response.getJSONArray("results");
                    //iterate through result set and add Movie objects to movies ArrayList
                    for (int i = 0; i < results.length(); i++){
                        movies.add(new Movie(results.getJSONObject(i)));
                        //notify adapter that a row was added
                        adapter.notifyItemInserted(movies.size()-1);
                    }
                    Log.i(TAG,String.format("Loaded %s movies", results.length()));
                } catch (JSONException e) {
                    logError("Failed to parse now playing movies",e,true);
                }
            }
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                logError("Failed getting data from now_playign endpoint.",throwable,true);
            }
        });
    }


    //get config from API
    private void getConfiguration(){
        //create url to acces
        String url=API_BASE_URL+"/configuration";
        //set parameters for request to API
        RequestParams params=new RequestParams();
        params.put(API_KEY_PARAM,getString(R.string.api_key)); //API key param: always required
        //execute get request that expects a configuration JSON file
        client.get(url,params,new JsonHttpResponseHandler(){
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                try {
                    config=new Config(response);
                    //Log success on retreiving configuration
                    Log.i(TAG,String.format("Loaded configuration with imageBaseUrl %s and posterSize %s", config.getImageBaseUrl(),config.getPosterSize()));
                    //pass config to adapter
                    adapter.setConfig(config);
                    //get the now playing movie list
                    getNowPlaying();
                } catch (JSONException e) {
                    logError("Failed parsing configuration.",e,true);
                }
            }
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                logError("Failed getting configuration",throwable,true);
            }
        });
    }

    //method for handling errors: log and alert user
    private void logError(String mss, Throwable error, boolean alertUser){
        //log error
        Log.e(TAG,mss,error);
        //alert user if alertUser is true
        if (alertUser){
            //display a long toast with the error mss
            Toast.makeText(getApplicationContext(),mss,Toast.LENGTH_LONG).show();
        }
    }
}
