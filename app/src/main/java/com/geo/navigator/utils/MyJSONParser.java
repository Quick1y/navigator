package com.geo.navigator.utils;



import android.content.Context;
import android.util.Log;

import com.geo.navigator.data.MyDatabaseProvider;
import com.geo.navigator.route.model.Point;

import org.json.JSONException;
import org.json.JSONObject;

public class MyJSONParser {
    private static final String TAG = "MyJSONParser";

    private static final String QR_MAPID = "map";
    private static final String QR_POINTID = "point";

    private static final String USR_ROLE = "meta_value";

    private MyJSONParser(){}

    public static Point getPointToQR(Context context, String jsonStr) throws JSONException{
        JSONObject json = new JSONObject(jsonStr);

        int map_id = json.getInt(QR_MAPID);
        int point_id = json.getInt(QR_POINTID);

        return MyDatabaseProvider.getPoint(context, map_id, point_id);
    }


    public static String getUsrRole(String jsonStr) throws JSONException{
        JSONObject json = new JSONObject(jsonStr);
        return json.getString(USR_ROLE);
    }

}
