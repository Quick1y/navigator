package com.geo.navigator.route.model;

/**
 * Created by nikita on 22.04.17.
 */

public class Point {
    private float mX;
    private float mY;
    private int mId;
    private int mMapId;
    private String mDescription;
    private boolean mVisibleOnMap;

    public Point(float x, float y, int id, int mapId, String description, boolean visibleOnMap){
        mX = x;
        mY = y;
        mId = id;
        mMapId = mapId;
        mDescription = description;
        mVisibleOnMap = visibleOnMap;
    }

    public float getX() {
        return mX;
    }

    public float getY() {
        return mY;
    }

    public int getId() {
        return mId;
    }

    public int getMapId() {
        return mMapId;
    }

    public String getDescription() {
        return mDescription;
    }

    public boolean isVisibleOnMap() {
        return mVisibleOnMap;
    }
}
