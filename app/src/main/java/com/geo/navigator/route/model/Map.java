package com.geo.navigator.route.model;

/**
 * Created by nikita on 04.06.17.
 */

public class Map {
    private int mId;
    private String mDescription;

    public Map(int id, String description){
        mId = id;
        mDescription = description;
    }

    public int getId() {
        return mId;
    }

    public String getDescription() {
        return mDescription;
    }

    @Override
    public String toString() {
        return mDescription;
    }
}
