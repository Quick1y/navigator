package com.geo.navigator.Model;

/**
 * Created by nikita on 04.06.17.
 */

public class Map {
    private int mId;
    private String mDescription;
    private int mImageId;

    public Map(int id, String description, int imageId){
        mId = id;
        mDescription = description;
        mImageId = imageId;
    }

    public int getmImageId() {
        return mImageId;
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
