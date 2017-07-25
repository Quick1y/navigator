package com.geo.navigator.Model;

/**
 * Created by nikita on 16.07.17.
 *
 * Для хранения точек локально с ограниченным количеством информации
 *
 * Тут храняться ТОЛЬКО ВИДИМЫЕ точки
 */

public class SimplePoint implements ISpinnerItem {

    private int mId;
    private int mMapId;
    private String mDescription;


    public SimplePoint(int id, int mapId, String description){
        mId = id;
        mMapId = mapId;
        mDescription = description;
    }

    public SimplePoint(int id, String description){
        mId = id;
        mMapId = 0;
        mDescription = description;
    }


    public int getId() {
        return mId;
    }

    public int getMapId() {
        return mMapId;
    }

    public String getInfo() {
        return mDescription;
    }
}
