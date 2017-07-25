package com.geo.navigator.Model;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.geo.navigator.R;

import java.util.ArrayList;

/**
 * Created by nikita on 24.07.17.
 */

public class MySpinnerAdapter extends BaseAdapter {
    private static final String TAG = "MySpinnerAdapter";

    private ArrayList<ISpinnerItem> mItemList;
    private LayoutInflater mInflater;
    private String mFirstName;


    //принемает только листы объектов, реализующих интерфейс ISpinnerItem
    public MySpinnerAdapter(ArrayList<ISpinnerItem> list, LayoutInflater inflater, String firstName){

        if(list == null){
            list = new ArrayList<>();
        }
        list.add(0, new SimpleSpinnerItem(-1, firstName));

        mItemList = list;
        mInflater = inflater;
        mFirstName = firstName;
    }

    @Override
    public int getCount() {
        if (mItemList != null){
            return mItemList.size();
        } else {
            return 0;
        }
    }

    @Override
    public Object getItem(int i) {
        return mItemList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return mItemList.get(i).getId();
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        //тут нужно делать View Holder
        view = mInflater.inflate(R.layout.spinner_view, viewGroup, false);

        TextView tv = (TextView) view.findViewById(R.id.spinner_view_text);
        tv.setText(mItemList.get(i).getInfo());
        return view;
    }
}
