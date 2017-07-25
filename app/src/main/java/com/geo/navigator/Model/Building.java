package com.geo.navigator.Model;

/**
 * Created by nikita on 21.07.17.
 */

public class Building implements ISpinnerItem {
    private int mId;
    private String mInfo;

    public Building(int id, String info){
        mId = id;
        mInfo = info;
    }

    public String getInfo() {
        return mInfo;
    }

    public int getId(){
        return mId;
    }


}
