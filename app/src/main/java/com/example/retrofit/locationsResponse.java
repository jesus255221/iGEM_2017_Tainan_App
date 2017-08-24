package com.example.retrofit;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by david on 8/11/2017.
 */

public class locationsResponse {

    @SerializedName("locations")
    @Expose
    private List<Locations> locations;

    public List<Locations> getLocations(){
        return locations;
    }

}
