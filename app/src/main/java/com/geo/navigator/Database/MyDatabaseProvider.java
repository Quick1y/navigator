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

    @Nullable
    public static Point[] getPointsByMeta(Context context, int id_map, int meta){
        init(context);

        //Делаем запрос к бд
        Cursor cursor;
        try {
            cursor = mDatabase.query(
                    DatabaseTable.POINTS,
                    null, // выбрать все столбцы
                    "" + DatabaseTable.Column.POINTS_META + "=? AND "
                     + DatabaseTable.Column.POINTS_ID_MAP + "=?",
                    new String[] {String.valueOf(meta), String.valueOf(id_map)},
                    null,
                    null,
                    null
            );
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }

        Log.d(TAG, "getPointsByMeta: cursor size = " + cursor.getCount());
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

        //достаем массив дуг
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


        // теперь строим двумерную матрицу переход и
        // заполняем отсутствующие переходы невозможными

        Point points[] = getPointsForMap(context, map_id);

        if(points == null){
            return null;
        }


        int length = points.length;
        Edge edgesMatrix[][] = new Edge[length][length];

        //заполнение матрицы дуг в соответствии с массивом точек
        for(int i = 0; i < length; i++){
            for (int j = 0; j < length; j++){

                for(Edge e : edges){
                    if(e.getIdPointA() == points[j].getId()
                            && e.getIdPointB() == points[i].getId()){
                        edgesMatrix[j][i] = e;                                 //вставляем так для того, что если граф симметричный
                        if(edgesMatrix[i][j] == null || edgesMatrix[i][j].getDescription().equals("generated")){ //и дуга обратно пустая или сгенерирована
                            edgesMatrix[i][j] = new Edge(e.getIdPointB(), e.getIdPointA(), e.getWeight(), e.getId_map(),e.getDescription());
                        }
                    } else {
                        if(edgesMatrix[j][i] == null){
                            edgesMatrix[j][i] = new Edge(points[j].getId(), points[i].getId(), 10000, map_id, "generated");
                        }
                    }
                }
            }
        }



        Log.d(TAG, "Матрица дуг: ");
        for (int i = 0; i < length; i++){
            for (int j = 0; j < length; j++){
                System.out.print(edgesMatrix[i][j].getIdPointA() + ", " + edgesMatrix[i][j].getIdPointB()
                        + ", w" + edgesMatrix[i][j].getWeight() + " || ");
            }
            System.out.println();
        }


        Log.d(TAG, "getEdgesMatrix running time: " + (System.nanoTime() - time) / 1000000.0 + "ms");
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

        Log.d(TAG, "setPoints: ");
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
            Log.d(TAG, "id = " + p.getId()+ "; desc = " + p.getDescription()
                    + "; map = " + p.getMapId() + "; meta = " + p.getMeta());
        }

    }

    //добавляет дуги в базу данных
    public static void setEdges(Context context, Edge[] edges){
        init(context);

        ContentValues values = new ContentValues();

        Log.d(TAG, "setEdges: ");
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

        Log.d(TAG, "setMaps: ");
        for(Map m : maps){
            values.put(DatabaseTable.Column.MAPS_ID, m.getId());
            values.put(DatabaseTable.Column.MAPS_DESC, m.getDescription());
            values.put(DatabaseTable.Column.MAPS_IMG_PATH, m.getImagePath());
            values.put(DatabaseTable.Column.MAPS_BUILDING_ID, m.getBuildingId());

            mDatabase.insert(DatabaseTable.MAPS, null, values); // добавляем в бд
            Log.d(TAG, "m.id = " + m.getId() + "; desc = " +  m.getDescription()
                    + "; path = " + m.getImagePath() + "; building id = " + m.getBuildingId());
        }

    }

}
