package com.example.fmonsalve.flicks.models;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by fmonsalve on 6/23/17.
 */

public class Config {

    //base url for images
    String imageBaseUrl;
    //poster size to use when fetching images, part of URL
    String posterSize;
    //backdrop size to use when fetching images
    String backdropSize;

    public Config(JSONObject object) throws JSONException {
        //parse images object, where all info needed is contained
        JSONObject images=object.getJSONObject("images");
        //get the image base URL
        imageBaseUrl=images.getString("secure_base_url");
        //get poster size... if nothing at index 3 in sizes array use w342
        posterSize=images.getJSONArray("poster_sizes").optString(3,"w342");
        //parse backdrop sizes and get option at idx 1 or w780 as a fallback
        backdropSize=images.getJSONArray("backdrop_sizes").optString(1,"w780");
    }

    public String getImageBaseUrl() {
        return imageBaseUrl;
    }

    public String getPosterSize() {
        return posterSize;
    }

    public String getBackdropSize() {
        return backdropSize;
    }

    //helper method to create urls
    public String getImageUrl(String size,String path){
        return String.format("%s%s%s", imageBaseUrl,size,path); // concat all three
    }
}
