package com.geo.navigator.Database;

import android.database.Cursor;
import android.database.CursorWrapper;

import com.geo.navigator.Model.Edge;
import com.geo.navigator.Model.Point;

/**
 * Created by nikita on 14.07.17.
 */

public class MyCursorWrapper extends CursorWrapper {
    public MyCursorWrapper(Cursor cursor) {
        super(cursor);
    }

    public Point getPoint() {
        int x = getInt(getColumnIndex(DatabaseTable.Column.POINTS_COORD_X));
        int y = getInt(getColumnIndex(DatabaseTable.Column.POINTS_COORD_Y));
        int id = getInt(getColumnIndex(DatabaseTable.Column.POINTS_ID));
        int mapId = getInt(getColumnIndex(DatabaseTable.Column.POINTS_ID_MAP));
     //   int numOnGraph = getInt(getColumnIndex(DatabaseTable.Column.POINTS_NUM_ON_GRAPH));
        String description = getString(getColumnIndex(DatabaseTable.Column.POINTS_DESC));
        boolean visibleOnMap = getInt(getColumnIndex(DatabaseTable.Column.POINTS_VIS_ON_MAP)) == 1;
        int meta = getInt(getColumnIndex(DatabaseTable.Column.POINTS_META));

        return new Point(x, y, id, mapId, description, visibleOnMap, meta);
    }

    public Edge getEdge() {
        int idA = getInt(getColumnIndex(DatabaseTable.Column.EDGES_ID_A));
        int idB = getInt(getColumnIndex(DatabaseTable.Column.EDGES_ID_B));
        int weight = getInt(getColumnIndex(DatabaseTable.Column.EDGES_WEIGHT));
        int idMap = getInt(getColumnIndex(DatabaseTable.Column.EDGES_IDMAP));
        String desc = getString(getColumnIndex(DatabaseTable.Column.EDGES_DESC));

        return new Edge(idA, idB, weight, idMap, desc);
    }

}
