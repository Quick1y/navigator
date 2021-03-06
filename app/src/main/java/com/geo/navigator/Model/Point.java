package com.geo.navigator.Model;

/**
 * Created by nikita on 22.04.17.
 */

public class Point {

    public static final int META_STAIRS = 1000;
    public static final int META_EXIT = 2000;


    private int mX;
    private int mY;
    private int mId;
    private int mMapId;
  //  private int mNumOnGraph;
    private String mDescription;
    private boolean mVisibleOnMap;
    private int mMeta;

    public Point(int x, int y, int id, int mapId, String description,
                 boolean visibleOnMap, int meta){
        mX = x;
        mY = y;
        mId = id;
        mMapId = mapId;
        mDescription = description;
        mVisibleOnMap = visibleOnMap;
      //  mNumOnGraph = numOnGraph;
        mMeta = meta;
    }

    public int getX() {
        return mX;
    }

    public int getY() {
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

    public int getMeta(){
        return mMeta;
    }

    public static SimplePoint toLocalPoint(Point point){
        return new SimplePoint(point.getId(), point.getMapId(), point.getDescription());
    }

  //  public int getNumOnGraph(){return mNumOnGraph;};
}
