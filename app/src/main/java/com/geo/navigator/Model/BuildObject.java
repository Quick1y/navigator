package com.geo.navigator.Model;

/**
 * Created by nikita on 21.07.17.
 *
 * Объект как строение, комплекс зданий. Например: ЮРГПУ
 */

public class BuildObject implements ISpinnerItem {
    private int mId;
    private String mInfo;

    public BuildObject(int id, String info){
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
