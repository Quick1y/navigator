package com.geo.navigator.route.helper;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;

import com.geo.navigator.route.model.Point;

import java.util.ArrayList;


/**
 * Created by nikita on 21.04.17.
 */

public class DrawHelper {

    private DrawHelper(){}

    public static Bitmap drawWay(ArrayList<Point> pointsList, int color){
        Bitmap bitmap = Bitmap.createBitmap(1000, 1000, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);

        paint.setColor(color);
        paint.setStrokeWidth(5);

        //отрисовка пути
        int numLine = pointsList.size() - 1;
        for (int i = 0; i < numLine; i++){
            canvas.drawLine(pointsList.get(i).getX(),
                    pointsList.get(i).getY(),
                    pointsList.get(i + 1).getX(),
                    pointsList.get(i + 1).getY(),
                    paint);
        }

        return bitmap;
    }
}
