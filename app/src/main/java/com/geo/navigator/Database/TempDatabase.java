package com.geo.navigator.Database;

import com.geo.navigator.Model.LocalMap;
import com.geo.navigator.Model.SimplePoint;
import com.geo.navigator.Model.Edge;
import com.geo.navigator.Model.Map;
import com.geo.navigator.Model.Point;

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
            new Point(10, 660, 1012, 102, "aaa", true, 0),
            new Point(880, 10, 1013, 102, "bbb", true, 0)
    };

    private static SimplePoint[] localPointDataBase = {
            new SimplePoint(1004, 101, "101"),
            new SimplePoint(1003, 101, "102"),
            new SimplePoint(1001, 101, "Выход"),
            new SimplePoint(1005, 101, "103"),
            new SimplePoint(1006, 101, "104"),
            new SimplePoint(1007, 101, "105"),
            new SimplePoint(1012, 102, "aaa"),
            new SimplePoint(1013, 102, "bbb")
    };

    private static Map[] mMapDatabase = {
            new Map(101, "Корпус 1", "img_korpus1.jpg"),
            new Map(102, "QR_icon", "background.jpg")
    };

    private static LocalMap[] localMapDatabase = {
            new LocalMap(101, "Корпус 1"),
            new LocalMap(102, "Android")
    };

    private static Edge[][] mEdges = {
            {new Edge(1001, 1001, 10000, 101, ""), new Edge(1001, 1002, 30, 101, ""), new Edge(1001, 1003, 10000, 101, ""), new Edge(1001, 1004, 10000, 101, ""), new Edge(1001, 1005, 10000, 101, ""), new Edge(1001, 1006, 10000, 101, ""), new Edge(1001, 1007, 10000, 101, "")},
            {new Edge(1002, 1001, 30, 101, ""), new Edge(1002, 1002, 10000, 101, ""), new Edge(1002, 1003, 20, 101, ""), new Edge(1002, 1004, 10000, 101, ""), new Edge(1002, 1005, 20, 101, ""), new Edge(1002, 1006, 10000, 101, ""), new Edge(1002, 1007, 10000, 101, "")},
            {new Edge(1003, 1001, 10000, 101, ""), new Edge(1003, 1002, 20, 101, ""), new Edge(1003, 1003, 10000, 101, ""), new Edge(1003, 1004, 20, 101, ""), new Edge(1003, 1005, 10000, 101, ""), new Edge(1003, 1006, 10000, 101, ""), new Edge(1003, 1007, 10000, 101, "")},
            {new Edge(1004, 1001, 10000, 101, ""), new Edge(1004, 1002, 10000, 101, ""), new Edge(1004, 1003, 20, 101, ""), new Edge(1004, 1004, 10000, 101, ""), new Edge(1004, 1005, 10000, 101, ""), new Edge(1004, 1006, 10000, 101, ""), new Edge(1004, 1007, 10000, 101, "")},
            {new Edge(1005, 1001, 10000, 101, ""), new Edge(1005, 1002, 20, 101, ""), new Edge(1005, 1003, 10000, 101, ""), new Edge(1005, 1004, 10000, 101, ""), new Edge(1005, 1005, 10000, 101, ""), new Edge(1005, 1006, 10, 101, ""), new Edge(1005, 1007, 10000, 101, "")},
            {new Edge(1006, 1001, 10000, 101, ""), new Edge(1006, 1002, 10000, 101, ""), new Edge(1006, 1003, 10000, 101, ""), new Edge(1006, 1004, 10000, 101, ""), new Edge(1006, 1005, 10, 101, ""), new Edge(1006, 1006, 10000, 101, ""), new Edge(1006, 1007, 15, 101, "")},
            {new Edge(1007, 1001, 10000, 101, ""), new Edge(1007, 1002, 10000, 101, ""), new Edge(1007, 1003, 10000, 101, ""), new Edge(1007, 1004, 10000, 101, ""), new Edge(1007, 1005, 10000, 101, ""), new Edge(1007, 1006, 25, 101, ""), new Edge(1007, 1007, 10000, 101, "")},
    };



    // Служебный
    public static Point[] getPointsForMapArrFull() {
        ArrayList<Point> pointsList = new ArrayList<>();
        Point[] pointsArr;

        for (Point point : mPointDataBase) {
            pointsList.add(point);
        }

        pointsArr = pointsList.toArray(new Point[]{});
        return pointsArr;
    }

    // Служебный
    public static Edge[] getEdgesInLineFull() {
        Edge[] edges = new Edge[mEdges.length * mEdges[0].length];

        int i = 0;
        for (Edge[] e1 : mEdges) {
            for (Edge e2 : e1) {
                edges[i] = e2;
                i++;
            }
        }

        return edges;
    }

    //служебный
    public static ArrayList<Map> getMapsFull() {
        ArrayList<Map> mapList = new ArrayList<>();

        for (Map map : mMapDatabase) {
            mapList.add(map);
        }
        return mapList;
    }




    //должен быть реализован без бд
    public static ArrayList<LocalMap> getMaps() {
        ArrayList<LocalMap> mapList = new ArrayList<>();

        for (LocalMap map : localMapDatabase) {
            mapList.add(map);
        }
        return mapList;
    }

    //должен быть реализован без бд
    public static ArrayList<String> getMapsDescription() {
        ArrayList<String> mapDescList = new ArrayList<>();

        for (LocalMap map : localMapDatabase) {
            mapDescList.add(map.getDescription());
        }
        return mapDescList;
    }

    //должен быть реализован без бд
    public static ArrayList<String> getPointsDescsForMap(int id_map) {
        ArrayList<String> pointsDescsList = new ArrayList<>();

        for (SimplePoint point : localPointDataBase) {
            if (point.getMapId() == id_map) {
                pointsDescsList.add(point.getInfo());
            }
        }
        return pointsDescsList;
    }

    //должен быть реализован без бд
    public static SimplePoint[] getPointsForMap(int id_map) {
        ArrayList<SimplePoint> pointsList = new ArrayList<>();

        for (SimplePoint point : localPointDataBase) {
            if (point.getMapId() == id_map) {
                pointsList.add(point);
            }
        }
        return pointsList.toArray(new SimplePoint[]{});
    }



    // больше не используются
    private static Edge[][] completeInfinityEdges(Edge[][] edges) {
        for (int i = 0; i < edges.length; i++) {
            for (int j = 0; j < edges[0].length; j++) {
                if (edges[i][j] == null) {
                    edges[i][j] = new Edge(0, 0, 10000, 0, "infinity");
                }
            }
        }
        return edges;
    }
    public static ArrayList<Point> getPointsListForMap(Map map) {
        ArrayList<Point> pointsList = new ArrayList<>();

        for (Point point : mPointDataBase) {
            if (point.getMapId() == map.getId() && point.isVisibleOnMap()) {
                pointsList.add(point);
            }
        }
        return pointsList;
    }
    public static ArrayList<Point> getPointsListFromArray(int idMap, int[] pointsId) {
        ArrayList<Point> pointsList = new ArrayList<>();

        for (int pid : pointsId) {
            Point point = getPoint(idMap, pid);
            if (point != null) {
                pointsList.add(point);
            }
        }
        return pointsList;
    }
    public static Point getPoint(int idMap, int idPoint) {
        for (Point p : mPointDataBase) {
            if (p.getMapId() == idMap && p.getId() == idPoint) {
                return p;
            }
        }
        return null;
    }
    public static LocalMap getMap(int idMap) {
        for (LocalMap map : localMapDatabase) {
            if (map.getId() == idMap) {
                return map;
            }
        }
        return null;
    }
}
