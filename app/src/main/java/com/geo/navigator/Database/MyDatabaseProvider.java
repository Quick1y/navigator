package com.geo.navigator.Database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import com.geo.navigator.Model.Edge;
import com.geo.navigator.Model.Map;
import com.geo.navigator.Model.Point;

import java.util.ArrayList;

/**
 * Created by nikita on 12.07.17.
 */

public class MyDatabaseProvider {
    private static final String TAG = "MyDatabaseProvider";

    private static SQLiteDatabase mDatabase;

    private MyDatabaseProvider(){}

    //открывает бд на чтение/запись
    private static void init(Context context){
        if(mDatabase == null){
            mDatabase = new DatabaseHelper(context).getWritableDatabase();
        }
    }

    @Nullable  //возвращает точку по ее id и id карты
    public static Point getPoint(Context context, int point_id){
        init(context);

        //Делаем запрос к бд
        Cursor cursor;
        try {
             cursor = mDatabase.query(
                    DatabaseTable.POINTS,
                    null, // выбрать все столбцы
                    "" + DatabaseTable.Column.POINTS_ID +  "=?",   //   где 'id' = point_id
                    new String[] {String.valueOf(point_id)},
                    null,
                    null,
                    null
            );
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }

        Log.d(TAG, "cursor size = " + cursor.getCount());
        if(cursor.getCount() == 0){
            cursor.close();
            return null;
        }

        //достаем точку
        MyCursorWrapper cursorWrapper = new MyCursorWrapper(cursor);
        Point point;

        try {
            cursorWrapper.moveToFirst();
            point = cursorWrapper.getPoint();
        } finally {
            cursor.close();
            cursorWrapper.close();
        }

        return point;
    }

    @Nullable //возвращает карту по id
    public static Point[] getPointsForMap(Context context, int id_map){
        init(context);

        //Делаем запрос к бд
        Cursor cursor;
        try {
            cursor = mDatabase.query(
                    DatabaseTable.POINTS,
                    null, // выбрать все столбцы
                    "" + DatabaseTable.Column.POINTS_ID_MAP + "=?",      //   и 'id_maps' = map_id
                    new String[] {String.valueOf(id_map)},
                    null,
                    null,
                    null
            );
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }

        Log.d(TAG, "cursor size = " + cursor.getCount());
        if(cursor.getCount() == 0){
            cursor.close();
            return null;
        }

        //достаем точки
        Point[] points = new Point[cursor.getCount()];
        MyCursorWrapper cursorWrapper = new MyCursorWrapper(cursor);
        cursorWrapper.moveToFirst();

        try {
            for(int i = 0; i < points.length; i++){
                points[i] = cursorWrapper.getPoint();
                cursorWrapper.moveToNext();
            }
        }
        catch (Exception ex){
            ex.printStackTrace();
        }
        finally{
            cursor.close();
            cursorWrapper.close();
        }

        return points;
    }

    @Nullable // возвращает список описаний(названий) точек для карты
    public static ArrayList<String> getPointsDescsForMap(Context context, int id_map){
        init(context);

        Point[] points = getPointsForMap(context, id_map);
        ArrayList<String> descList = new ArrayList<>();

        for (Point p : points){
            descList.add(p.getDescription());
        }

        return descList;
    }

    @Nullable // преобразует массив id точек к списку точек
    public static ArrayList<Point> getPointsListFromArray(Context context, int id_map, int[] id_points) {
        Point[] points = getPointsForMap(context, id_map);
        ArrayList<Point> pointsList = new ArrayList<>();


        for (int id_p : id_points) {
            pointsList.add(getPoint(context, id_p));
        }

        return pointsList;
    }



    @Nullable // Возможно, очень ресурсоемкий метод
    public static Edge[][] getEdgesMatrix(Context context, int map_id){
        init(context);
        long time = System.nanoTime(); // для замера производительности

        // получаем из бд одномерный(!) не упорядоченный массив дуг
        Cursor cursor;
        try {
            cursor = mDatabase.query(
                    DatabaseTable.EDGES,
                    null, // выбрать все столбцы
                    "" + DatabaseTable.Column.EDGES_IDMAP +  "=?",
                    new String[] {String.valueOf(map_id)},
                    null,
                    null,
                    null
            );
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }

        Log.d(TAG, "cursor size = " + cursor.getCount());
        if(cursor.getCount() == 0){
            cursor.close();
            return null;
        }

        //достаем точку
        MyCursorWrapper cursorWrapper = new MyCursorWrapper(cursor);
        Edge[] edges = new Edge[cursor.getCount()];

        cursorWrapper.moveToFirst();

        try {
            for(int i = 0; i < edges.length; i++){
                edges[i] = cursorWrapper.getEdge();
                cursorWrapper.moveToNext();
            }
        }
        catch (Exception ex){
            ex.printStackTrace();
        }
        finally{
            cursor.close();
            cursorWrapper.close();
        }


        //Теперь преобразуем его в квадратную, упорядоченную(!) матрицу

        //проверяем, квадратная ли матрица. (Берем корень от длины массива
        // и если он не целый, то не квадратная)
        double compLenght = Math.sqrt(edges.length);
        int  length = (int) compLenght;

        if(compLenght != length){
            Log.d(TAG, "Матрица дуг не квадратная!");
            return null;
        }

        //собираем не упорядоченную двумерную матрицу
        Edge[][] edgesMatrix = new Edge[length][length];
        int index = 0;
        for (int j = 0; j < length; j++){
            for (int k = 0; k < length; k++){
                edgesMatrix[j][k] = edges[index];
                index++;
            }
        }

        //сортируем по вертикали
        for (int i = length - 1; i >= 0; i--) {
            for (int j = 0; j < i; j++) {
                if (edgesMatrix[j][0].getIdPointA() > edgesMatrix[j + 1][0].getIdPointA()) {
                    Edge edge = edgesMatrix[j][0];
                    edgesMatrix[j][0] = edgesMatrix[j + 1][0];
                    edgesMatrix[j + 1][0] = edge;
                }
            }
        }


        //сортируем по горизонтали
        for (int i = length - 1; i >= 0; i--) {
            for (int j = 0; j < i; j++) {
                if (edgesMatrix[0][j].getIdPointA() > edgesMatrix[0][j + 1].getIdPointA()) {
                    Edge edge = edgesMatrix[0][j];
                    edgesMatrix[0][j] = edgesMatrix[0][j + 1];
                    edgesMatrix[0][j + 1] = edge;
                }
            }
        }

        Log.d(TAG, "getEdgesMatrix finished in " + (System.nanoTime() - time) / 1000000.0  + " ms\nMatrix:");

        for (int i = 0; i < length; i++){
            for (int j = 0; j < length; j++){
                System.out.print(edgesMatrix[i][j].getIdPointA() + ", " + edgesMatrix[i][j].getIdPointB() + " ");
            }
            System.out.println();
        }

        return edgesMatrix;
    }



    @Nullable //возвращает карту по ее id
    public static Map getMap(Context context, int map_id){
        init(context);

        Cursor cursor;
        try {
            cursor = mDatabase.query(
                    DatabaseTable.MAPS,
                    null, // выбрать все столбцы
                    "" + DatabaseTable.Column.MAPS_ID + "=?",      //   и 'id_maps' = map_id
                    new String[] {String.valueOf(map_id)},
                    null,
                    null,
                    null
            );
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }

        Log.d(TAG, "cursor size = " + cursor.getCount());
        if(cursor.getCount() == 0){
            cursor.close();
            return null;
        }

        //достаем точки
        Map map = null;
        MyCursorWrapper cursorWrapper = new MyCursorWrapper(cursor);
        cursorWrapper.moveToFirst();

        try {
            map = cursorWrapper.getMap();
            cursorWrapper.moveToNext();
        }
        catch (Exception ex){
            ex.printStackTrace();
        }
        finally{
            cursor.close();
            cursorWrapper.close();
        }

        return map;
    }

    @Nullable //возвращает карту по id точки
    public static Map getMapByPointId(Context context, int point_id){
        init(context);

        Point point = getPoint(context, point_id);
        if (point == null){
            return null;
        }
        return getMap(context, point.getMapId());
    }


    public static void clearDatabase(Context context){
        init(context);

        mDatabase.delete( DatabaseTable.MAPS, null, null);
        mDatabase.delete(DatabaseTable.POINTS, null, null);
        mDatabase.delete(DatabaseTable.EDGES, null, null);
    }


    //добавляет точки в базу данных
    public static void setPoints(Context context, Point[] points){
        init(context);

        ContentValues values = new ContentValues();

        Log.d(TAG, "setMaps");
        for(Point p : points){
            values.put(DatabaseTable.Column.POINTS_ID, p.getId());
            values.put(DatabaseTable.Column.POINTS_ID_MAP, p.getMapId());
            values.put(DatabaseTable.Column.POINTS_COORD_X, p.getX());
            values.put(DatabaseTable.Column.POINTS_COORD_Y, p.getY());
            values.put(DatabaseTable.Column.POINTS_DESC, p.getDescription());
           // values.put(DatabaseTable.Column.POINTS_NUM_ON_GRAPH, p.g);
            values.put(DatabaseTable.Column.POINTS_VIS_ON_MAP, p.isVisibleOnMap());
            values.put(DatabaseTable.Column.POINTS_META, p.getMeta());

            mDatabase.insert(DatabaseTable.POINTS, null, values); // добавляем в бд
            Log.d(TAG, "id = " + p.getId()+ "; desc = " + p.getDescription() + "; map = " + p.getMapId());
        }

    }

    //добавляет дуги в базу данных
    public static void setEdges(Context context, Edge[] edges){
        init(context);

        ContentValues values = new ContentValues();

        Log.d(TAG, "setMaps");
        for(Edge e : edges){
            values.put(DatabaseTable.Column.EDGES_ID_A, e.getIdPointA());
            values.put(DatabaseTable.Column.EDGES_ID_B, e.getIdPointB());
            values.put(DatabaseTable.Column.EDGES_WEIGHT, e.getWeight());
            values.put(DatabaseTable.Column.EDGES_IDMAP, e.getId_map());
            values.put(DatabaseTable.Column.EDGES_DESC, e.getDescription());

            mDatabase.insert(DatabaseTable.EDGES, null, values); // добавляем в бд
            Log.d(TAG, "id a = " + e.getIdPointA() + "; id b= " + e.getIdPointB() + "; weight = " + e.getWeight() + "...");
        }

    }

    //добавляет карты в базу данных
    public static void setMaps(Context context, Map[] maps){
        init(context);

        ContentValues values = new ContentValues();

        Log.d(TAG, "setMaps");
        for(Map m : maps){
            values.put(DatabaseTable.Column.MAPS_ID, m.getId());
            values.put(DatabaseTable.Column.MAPS_DESC, m.getDescription());
            values.put(DatabaseTable.Column.MAPS_IMG_PATH, m.getImagePath());

            mDatabase.insert(DatabaseTable.MAPS, null, values); // добавляем в бд
            Log.d(TAG, "m.id = " + m.getId() + "; desc = " +  m.getDescription() + "; path = " + m.getImagePath());
        }

    }

}
