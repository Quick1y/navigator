package com.geo.navigator.Model;

/**
 * Created by nikita on 24.07.17.
 *
 * Нужен для того, чтобы добавить в спиннеры новые элементы, типа "Выберите аудиторию"
 */

public class SimpleSpinnerItem implements ISpinnerItem {

    private String mInfo;
    private int mId;

    public SimpleSpinnerItem(int id, String info){
        mId = id;
        mInfo = info;
    }

    @Override
    public String getInfo() {
        return mInfo;
    }

    @Override
    public int getId() {
        return mId;
    }
}
