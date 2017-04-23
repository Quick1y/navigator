package com.geo.navigator.route.model;

/**
 * Created by nikita on 22.04.17.
 */

public class Point {
    private float mX;
    private float mY;

    public Point(float x, float y){
        mX = x;
        mY = y;
    }

    public float getX() {
        return mX;
    }

    public float getY() {
        return mY;
    }
}
