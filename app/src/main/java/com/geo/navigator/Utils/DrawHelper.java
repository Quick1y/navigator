package com.geo.navigator.Utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

import com.geo.navigator.Model.Point;
import com.geo.navigator.R;

import java.util.ArrayList;


/**
 * Created by nikita on 21.04.17.
 */

public class DrawHelper {
    private static Canvas canvas;

    private DrawHelper(){}

    public static Bitmap drawWay(ArrayList<Point> pointsList, Context context, int size){
        Bitmap bitmap = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888);
        canvas = new Canvas(bitmap);
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        int color = context.getResources().getColor(R.color.colorAccent);

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

        drawPoint(context.getResources().getColor(R.color.my_red), pointsList.get(0)); // выделяем начальную точку
        drawPoint(context.getResources().getColor(R.color.my_purpure), pointsList.get(pointsList.size() - 1));  // выделяем конечную точку

        return bitmap;
    }

    public static void drawPoint(int color, Point point){
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setColor(color);
        paint.setStrokeWidth(5);

        canvas.drawCircle(point.getX(), point.getY(), 7, paint);
    }


}
