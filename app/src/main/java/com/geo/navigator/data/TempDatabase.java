package com.geo.navigator.data;

import android.util.Log;

import com.geo.navigator.R;
import com.geo.navigator.route.model.Edge;
import com.geo.navigator.route.model.Map;
import com.geo.navigator.route.model.Point;

import java.util.ArrayList;

/**
 * Created by nikita on 04.06.17.
 */

public class TempDatabase {
    private static final String TAG = "TempDatabase";


    private static Point[] mPointDataBase = {
            new Point(100, 460, 1004, 101, "101", true, 0),
            new Point(270, 460, 1003, 101, "102", true, 0),
            new Point(430, 460, 1002, 101, "-", false, 0),
            new Point(430, 720, 1001, 101, "Выход", true, 0),
            new Point(674, 460, 1005, 101, "103", true, 0),
            new Point(880, 460, 1006, 101, "104", true, 0),
            new Point(880, 660, 1007, 101, "105", true, 0),
    };

    private static Map[] mMapDatabase = {
            new Map(101, "Корпус 1", R.drawable.korpus1),
            new Map(102, "QR_icon", R.drawable.qrcode_icon)
    };

    private static Edge[][] mEdges = {
            {new Edge(1001,1001,10000, 101, ""), new Edge(1001,1002,30, 101, ""), new Edge(1001,1003,10000, 101, ""), new Edge(1001,1004,10000, 101, ""), new Edge(1001,1005,10000, 101, ""), new Edge(1001,1006,10000, 101, ""), new Edge(1001,1007,10000,  101,"")},
            {new Edge(1002,1001,30, 101, ""), new Edge(1002,1002,10000, 101, ""), new Edge(1002,1003,20, 101, ""), new Edge(1002,1004,10000, 101, ""), new Edge(1002,1005,20, 101, ""), new Edge(1002,1006,10000, 101, ""), new Edge(1002,1007,10000,  101,"")},
            {new Edge(1003,1001,10000, 101, ""), new Edge(1003,1002,20, 101, ""), new Edge(1003,1003,10000, 101, ""), new Edge(1003,1004,20,  101,""), new Edge(1003,1005,10000,  101,""), new Edge(1003,1006,10000,  101,""), new Edge(1003,1007,10000,  101,"")},
            {new Edge(1004,1001,10000, 101, ""), new Edge(1004,1002,10000, 101, ""), new Edge(1004,1003,20, 101, ""), new Edge(1004,1004,10000, 101, ""), new Edge(1004,1005,10000, 101, ""), new Edge(1004,1006,10000,  101,""), new Edge(1004,1007,10000,  101,"")},
            {new Edge(1005,1001,10000, 101, ""), new Edge(1005,1002,20, 101, ""), new Edge(1005,1003,10000, 101, ""), new Edge(1005,1004,10000,  101,""), new Edge(1005,1005,10000,  101,""), new Edge(1005,1006,10,  101,""), new Edge(1005,1007,10000,  101,"")},
            {new Edge(1006,1001,10000, 101, ""), new Edge(1006,1002,10000, 101, ""), new Edge(1006,1003,10000, 101, ""), new Edge(1006,1004,10000,  101,""), new Edge(1006,1005,10, 101, ""), new Edge(1006,1006,10000,  101,""), new Edge(1006,1007,15,  101,"")},
            {new Edge(1007,1001,10000, 101, ""), new Edge(1007,1002,10000, 101, ""), new Edge(1007,1003,10000, 101, ""), new Edge(1007,1004,10000, 101, ""), new Edge(1007,1005,10000, 101, ""), new Edge(1007,1006,25,  101,""), new Edge(1007,1007,10000,  101,"")},
    };

    public static Map[] getAvailableMaps(){
        return mMapDatabase;
    }

    public static ArrayList<String> getPointsDescsForMap(Map map){
        ArrayList<String> pointsDescsList = new ArrayList<>();

        for(Point point : mPointDataBase){
            if(point.getMapId() == map.getId() && point.isVisibleOnMap()){
                pointsDescsList.add(point.getDescription());
            }
        }
        return pointsDescsList;
    }

    public static ArrayList<Point> getPointsListForMap(Map map){
        ArrayList<Point> pointsList = new ArrayList<>();

        for(Point point : mPointDataBase){
            if(point.getMapId() == map.getId() && point.isVisibleOnMap()){
                pointsList.add(point);
            }
        }
        return pointsList;
    }
    public static ArrayList<Point> getPointsForMap(Map map){
        ArrayList<Point> pointsList = new ArrayList<>();

        for(Point point : mPointDataBase){
            if(point.getMapId() == map.getId() && point.isVisibleOnMap()){
                pointsList.add(point);
            }
        }
        return pointsList;
    }



    public static Point[] getPointsForMapArr(int id_map){
        ArrayList<Point> pointsList = new ArrayList<>();
        Point[] pointsArr;

        for(Point point : mPointDataBase){
            if(point.getMapId() == id_map && point.isVisibleOnMap()){
                pointsList.add(point);
            }
        }

        pointsArr = pointsList.toArray(new Point[]{});
        return pointsArr;
    }


    public static ArrayList<Map> getMaps(){
        ArrayList<Map> mapList = new ArrayList<>();
        Map[] maps = getAvailableMaps();

        for (Map map : maps){
            mapList.add(map);
        }
        return mapList;
    }

    public static ArrayList<String> getMapsDescription(){
        ArrayList<String> mapDescList = new ArrayList<>();
        Map[] maps = getAvailableMaps();

        for (Map map : maps){
            mapDescList.add(map.getDescription());
        }
        return mapDescList;
    }

    public static Edge[] getEdgesInLine() {
        //mEdges = completeInfinityEdges(mEdges);

        Edge[] edges = new Edge[mEdges.length * mEdges[0].length];

        int i = 0;
        for (Edge[] e1 : mEdges){
            for (Edge e2 : e1){
                edges[i] = e2;
                i++;
            }
        }

        return edges;
    }

    public static ArrayList<Point> getPointsListFromArray(int idMap, int[] pointsId){
        ArrayList<Point> pointsList = new ArrayList<>();

        for (int pid : pointsId){
            Point point = getPoint(idMap, pid);
            if(point != null){
                pointsList.add(point);
            }
        }
        return pointsList;
    }

    public static Point getPoint(int idMap, int idPoint){
        for (Point p : mPointDataBase){
            if(p.getMapId() == idMap && p.getId() == idPoint){
                return p;
            }
        }
        return null;
    }

    public static Map getMap(int idMap){
        for (Map map : mMapDatabase){
            if(map.getId() == idMap){
                return map;
            }
        }
        return null;
    }



    private static Edge[][] completeInfinityEdges(Edge[][] edges){
        for (int i = 0; i < edges.length; i++){
            for(int j = 0; j < edges[0].length; j++){
                if(edges[i][j] == null){
                    edges[i][j] = new Edge(0,0,10000, 0,"infinity");
                }
            }
        }
        return edges;
    }
}
