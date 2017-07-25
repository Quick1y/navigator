package com.geo.navigator.Model;

/**
 * Created by nikita on 16.07.17.
 *
 * Для хранения карт локально с ограниченным количеством информации
 */

public class LocalMap {
    private int mId;
    private String mDescription;


    public LocalMap(int id, String description){
        mId = id;
        mDescription = description;
    }

    public int getId() {
        return mId;
    }

    public String getDescription() {
        return mDescription;
    }
}
