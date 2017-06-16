package com.geo.navigator.data;

import com.geo.navigator.route.model.Edge;
import com.geo.navigator.route.model.Map;
import com.geo.navigator.route.model.Point;

import java.util.ArrayList;

/**
 * Created by nikita on 04.06.17.
 */

public class TempDatabase {

    private static Point[] mPointDataBase = {
            new Point(100, 460, 1004, 101, "101", true),
            new Point(270, 460, 1003, 101, "102", true),
            new Point(430, 460, 1002, 101, "-", false),
            new Point(430, 720, 1001, 101, "Выход", true),
            new Point(674, 460, 1005, 101, "103", true),
            new Point(880, 460, 1006, 101, "104", true),
            new Point(880, 660, 1007, 101, "105", true),
    };

    private static Map[] mMapDatabase = {
            new Map(101, "Корпус 1"),
            new Map(102, "Корпус 2")
    };

    private static Edge[][] mEdges = {
            {new Edge(1001,1001,10000), new Edge(1001,1002,30), new Edge(1001,1003,10000), new Edge(1001,1004,10000), new Edge(1001,1005,10000), new Edge(1001,1006,10000), new Edge(1001,1007,10000)},
            {new Edge(1002,1001,30), new Edge(1002,1002,10000), new Edge(1002,1003,20), new Edge(1002,1004,10000), new Edge(1002,1005,20), new Edge(1002,1006,10000), new Edge(1002,1007,10000)},
            {new Edge(1003,1001,10000), new Edge(1003,1002,20), new Edge(1003,1003,10000), new Edge(1003,1004,20), new Edge(1003,1005,10000), new Edge(1003,1006,10000), new Edge(1003,1007,10000)},
            {new Edge(1004,1001,10000), new Edge(1004,1002,10000), new Edge(1004,1003,20), new Edge(1004,1004,10000), new Edge(1004,1005,10000), new Edge(1004,1006,10000), new Edge(1004,1007,10000)},
            {new Edge(1005,1001,10000), new Edge(1005,1002,20), new Edge(1005,1003,10000), new Edge(1005,1004,10000), new Edge(1005,1005,10000), new Edge(1005,1006,10), new Edge(1005,1007,10000)},
            {new Edge(1006,1001,10000), new Edge(1006,1002,10000), new Edge(1006,1003,10000), new Edge(1006,1004,10000), new Edge(1006,1005,10), new Edge(1006,1006,10000), new Edge(1006,1007,15)},
            {new Edge(1007,1001,10000), new Edge(1007,1002,10000), new Edge(1007,1003,10000), new Edge(1007,1004,10000), new Edge(1007,1005,10000), new Edge(1007,1006,25), new Edge(1007,1007,10000)},
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

    public static ArrayList<Point> getPointsForMap(Map map){
        ArrayList<Point> pointsList = new ArrayList<>();

        for(Point point : mPointDataBase){
            if(point.getMapId() == map.getId() && point.isVisibleOnMap()){
                pointsList.add(point);
            }
        }
        return pointsList;
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

    public static Edge[][] getEdges() {
        //mEdges = completeInfinityEdges(mEdges);
        return mEdges;
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
        Point point;
        for (Point p : mPointDataBase){
            if(p.getMapId() == idMap && p.getId() == idPoint){
                return p;
            }
        }
        return null;
    }



    private static Edge[][] completeInfinityEdges(Edge[][] edges){
        for (int i = 0; i < edges.length; i++){
            for(int j = 0; j < edges[0].length; j++){
                if(edges[i][j] == null){
                    edges[i][j] = new Edge(0,0,10000);
                }
            }
        }
        return edges;
    }
}
