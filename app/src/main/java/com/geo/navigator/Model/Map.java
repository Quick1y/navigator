package com.geo.navigator.Model;

/**
 * Created by nikita on 04.06.17.
 */

public class Map {
    private int mId;
    private String mDescription;
    private String mImagePath;

    public Map(int id, String description, String imagePath){
        mId = id;
        mDescription = description;
        mImagePath = imagePath;
    }

    public String getImagePath() {
        return mImagePath;
    }

    public void setImagePath(String path) {
        mImagePath = path;
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

    public static LocalMap toLocalMap(Map map){
        return new LocalMap(map.getId(), map.getDescription());
    }
}
