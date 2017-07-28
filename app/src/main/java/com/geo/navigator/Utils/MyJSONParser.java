package com.geo.navigator.Utils;



import android.util.Log;

import com.geo.navigator.Model.BuildObject;
import com.geo.navigator.Model.Building;
import com.geo.navigator.Model.Edge;
import com.geo.navigator.Model.ISpinnerItem;
import com.geo.navigator.Model.SimplePoint;
import com.geo.navigator.Model.Map;
import com.geo.navigator.Model.Point;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class MyJSONParser {
    private static final String TAG = "MyJSONParser";

    private static final String QR_MAPID = "map";
    private static final String QR_POINTID = "point";

    private static final String USR_ROLE = "meta_value";

    private static final String DL_MAP_INDEX= "0";
    private static final String DL_MAP = "Maps";
    private static final String DL_MAP_ID = "Id";
    private static final String DL_MAP_DESC = "INFO";
    private static final String DL_MAP_BUILDING_ID = "id_Buildings"; //////////////

    private static final String DL_POINTS_INDEX= "1";
    private static final String DL_POINTS = "points";
    private static final String DL_POINTS_X = "X";
    private static final String DL_POINTS_Y = "Y";
    private static final String DL_POINTS_ID = "Id";
    private static final String DL_POINTS_ID_MAP = "iDmaps";
    private static final String DL_POINTS_DESC = "Info";
    private static final String DL_POINTS_VIS = "Visible";
    private static final String DL_POINTS_META = "Meta";

    private static final String DL_EDGES_INDEX= "2";
    private static final String DL_EDGES = "Edges";
    private static final String DL_EDGES_ID_A = "IdA";
    private static final String DL_EDGES_ID_B = "IdB";
    private static final String DL_EDGES_WEIGHT = "Ves";
    private static final String DL_EDGES_ID_MAP = "iDmaps";
    private static final String DL_EDGES_DESC = "Info";
    private static final String DL_IMG_URL = "URL";

    private static final String BUILDING_ARRAY = "buildings";
    private static final String BUILDING_ID = "Id";
    private static final String BUILDING_INFO = "INFO";

    private static final String OBJECT_ARRAY = "object";
    private static final String OBJECT_ID = "Id";
    private static final String OBJECT_INFO = "INFO";

    private static final String POINT_ARRAY = "points";
    private static final String POINT_ID = "Id";
    private static final String POINT_INFO = "Info";
    private static final String POINT_MAP= "iDmaps";



    private MyJSONParser(){}

    public static int getPointIdToQR(String jsonStr) throws JSONException{
        JSONObject json = new JSONObject(jsonStr);
        return json.getInt(QR_POINTID);
    }

    public static int getMapIdToQR(String jsonStr) throws JSONException{
        JSONObject json = new JSONObject(jsonStr);
        return json.getInt(QR_MAPID);
    }


    public static String getUsrRole(String jsonStr) throws JSONException{
        JSONObject json = new JSONObject(jsonStr);
        return json.getString(USR_ROLE);
    }


    //Возвращают листы для адаптеров спеннеров на RouteActivity
    public static ArrayList<ISpinnerItem> getBuildings(String jsonStr) throws JSONException{
        Log.d(TAG, "getBuildings");
        JSONArray json = new JSONObject(jsonStr).getJSONArray(BUILDING_ARRAY);

        ArrayList<ISpinnerItem> buildings = new ArrayList<>();

        for(int i = 0; i < json.length(); i++){
            int id = json.getJSONObject(i).getInt(BUILDING_ID);
            String info = json.getJSONObject(i).getString(BUILDING_INFO);
            buildings.add(new Building(id, info));

            Log.d(TAG, "Building: id = "+id+"; desc = " + info);
        }

        return buildings;
    }

    public static ArrayList<ISpinnerItem> getObjects(String jsonStr) throws JSONException{
        Log.d(TAG, "getObjects");
        JSONArray json = new JSONObject(jsonStr).getJSONArray(OBJECT_ARRAY);

        ArrayList<ISpinnerItem> objects = new ArrayList<>();

        for(int i = 0; i < json.length(); i++){
            int id = json.getJSONObject(i).getInt(OBJECT_ID);
            String info = json.getJSONObject(i).getString(OBJECT_INFO);
            objects.add(new BuildObject(id, info));

            Log.d(TAG, "BuildObject: id = "+id+"; desc = " + info);
        }

        return objects;
    }

    public static ArrayList<ISpinnerItem> getPointsForBuilding(String jsonStr) throws JSONException{
        Log.d(TAG, "getPointsForBuilding");
        JSONArray json = new JSONObject(jsonStr).getJSONArray(POINT_ARRAY);

        ArrayList<ISpinnerItem> points = new ArrayList<>();


        for(int i = 0; i < json.length(); i++){
            int id = json.getJSONObject(i).getInt(POINT_ID);
            String info = json.getJSONObject(i).getString(POINT_INFO);
            int id_map = json.getJSONObject(i).getInt(POINT_MAP);

            points.add(new SimplePoint(id, id_map, info));

            Log.d(TAG, "SimplePoint: id = "+id+"; desc = " + info+"; map_id = " + id_map);
        }



        return points;
    }


    // 4 метода ниже служат для получения всей карты целиком из одного json'a
    public static Map getDownloadMap(String jsonStr)throws JSONException {
        JSONArray json = new JSONArray(jsonStr); //создаем JSON из строки

        JSONObject mapObject = json.getJSONObject(0).getJSONArray(DL_MAP).getJSONObject(0); // достаем Map

        int id = mapObject.getInt(DL_MAP_ID);
        String desc = mapObject.getString(DL_MAP_DESC);
        int building_id = mapObject.getInt(DL_MAP_BUILDING_ID);

        Log.d(TAG, "getDownloadMap");
        Log.d(TAG, "id = "+id+"; desc = " + desc);
        return new Map(id, desc, null, building_id); //путь картинки на диске пока null
    }

    public static Point[] getDownloadPoints(String jsonStr) throws JSONException{
        JSONArray json = new JSONArray(jsonStr);
        JSONArray jsonArr = json.getJSONObject(1).getJSONArray(DL_POINTS); // получаем массив точек
        Point[] points;

        int length = jsonArr.length();
        points = new Point[length];

        Log.d(TAG, "getDownloadPoints");
        for(int i = 0; i < length; i++){
            JSONObject jsonPoint = jsonArr.getJSONObject(i);
            int x = jsonPoint.getInt(DL_POINTS_X);
            int y = jsonPoint.getInt(DL_POINTS_Y);
            int id = jsonPoint.getInt(DL_POINTS_ID);
            int id_map = jsonPoint.getInt(DL_POINTS_ID_MAP);
            String desc = jsonPoint.getString(DL_POINTS_DESC);
            boolean vis = jsonPoint.getInt(DL_POINTS_VIS) == 1;
            int meta = jsonPoint.getInt(DL_POINTS_META);

            points[i] = new Point(x, y, id, id_map, desc, vis, meta);

            Log.d(TAG, "id = "+id+"; id_map" + id_map + "; desc = " + desc + "; x = " + x + "; y = " + y + "; vis = " + vis
            + "; meta = " + meta);
        }

        return points;
    }

    public static Edge[] getDownloadEdges(String jsonStr) throws JSONException{
        JSONArray json = new JSONArray(jsonStr);
        JSONArray jsonArr = json.getJSONObject(2).getJSONArray(DL_EDGES); // получаем массив точек
        Edge[] edges;

        int length = jsonArr.length();
        edges = new Edge[length];

        Log.d(TAG, "getDownloadEdges");
        for(int i = 0; i < length; i++){
            JSONObject jsonPoint = jsonArr.getJSONObject(i);
            int id_a = jsonPoint.getInt(DL_EDGES_ID_A);
            int id_b = jsonPoint.getInt(DL_EDGES_ID_B);
            int weight = jsonPoint.getInt(DL_EDGES_WEIGHT);
            int id_map = jsonPoint.getInt(DL_EDGES_ID_MAP);
            String desc = jsonPoint.getString(DL_EDGES_DESC);


            edges[i] = new Edge(id_a,id_b,weight,id_map,desc);
            Log.d(TAG, "id_a = " + id_a + "; id_b = " + id_b + "; weight = " + weight +
            "; id_map = " + id_map + "; desc = " + desc);
        }

        return edges;
    }

    public static String getImageUrl(String jsonStr) throws JSONException{
        JSONObject mapObject = new JSONArray(jsonStr).getJSONObject(0).getJSONArray(DL_MAP).getJSONObject(0); // какой ужас
        String url = mapObject.getString(DL_IMG_URL);

        Log.d(TAG, "getImageUrl(): url = " + url);
        return url;
    }
    ///////////////
}
