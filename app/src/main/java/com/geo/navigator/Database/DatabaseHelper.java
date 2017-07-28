package com.geo.navigator.Database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by nikita on 14.07.17.
 */

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String TAG = "DatabaseHelper";

    private static final String DATABASE_NAME = "navigator.db";
    private static final int VERSION = 1;

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, VERSION);
    }


    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        //Создаем таблицу карт
        sqLiteDatabase.execSQL("CREATE TABLE " + DatabaseTable.MAPS + "("
                + "_id INTEGER PRIMARY KEY AUTOINCREMENT, "
                + DatabaseTable.Column.MAPS_ID + " INTEGER,  "
                + DatabaseTable.Column.MAPS_IMG_PATH + " STRING, "
                + DatabaseTable.Column.MAPS_DESC + " STRING, "
                + DatabaseTable.Column.MAPS_BUILDING_ID + " INTEGER)"
        );

        //Создаем таблицу точек
        sqLiteDatabase.execSQL("CREATE TABLE " + DatabaseTable.POINTS + "("
                + "_id INTEGER PRIMARY KEY AUTOINCREMENT, "
                + DatabaseTable.Column.POINTS_ID + " INTEGER, "
                + DatabaseTable.Column.POINTS_ID_MAP + " INTEGER, "
                + DatabaseTable.Column.POINTS_COORD_X + " INTEGER, "
                + DatabaseTable.Column.POINTS_COORD_Y + " INTEGER, "
                + DatabaseTable.Column.POINTS_DESC + " STRING, "
                //+ DatabaseTable.Column.POINTS_NUM_ON_GRAPH + " INTEGER, "
                + DatabaseTable.Column.POINTS_VIS_ON_MAP + "  INTEGER, "
                + DatabaseTable.Column.POINTS_META + " INTEGER)"

        );


        //Создаем таблицу дуг
        sqLiteDatabase.execSQL("CREATE TABLE " + DatabaseTable.EDGES + "("
                + "_id INTEGER PRIMARY KEY AUTOINCREMENT, "
               // + DatabaseTable.Column.EDGES_ID + " INTEGER,  "
                + DatabaseTable.Column.EDGES_ID_A + " INTEGER, "
                + DatabaseTable.Column.EDGES_ID_B + " INTEGER, "
                + DatabaseTable.Column.EDGES_WEIGHT + " INTEGER, "
                + DatabaseTable.Column.EDGES_IDMAP + " INTEGER, "
                + DatabaseTable.Column.EDGES_DESC + " STRING)"
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {

    }
}
