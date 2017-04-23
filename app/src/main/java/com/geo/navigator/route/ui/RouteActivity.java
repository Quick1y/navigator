package com.geo.navigator.route.ui;

/**
 * Created by nikita on 12.04.17.
 */

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;

import com.geo.navigator.R;
import com.geo.navigator.route.helper.DrawHelper;
import com.geo.navigator.route.model.Point;

import java.util.ArrayList;

public class RouteActivity extends AppCompatActivity {
    private static final String TAG = "RouteActivity";

    private ImageView mImageViewDrawing;
    private ImageView mImageViewBackground;
    private Button mFindWayButton;
    private Spinner mRoomSpinner;

    //вызывать для запуска интентом
    public static Intent newIntent(Context context){
        Intent intent = new Intent(context, RouteActivity.class);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_route);
        setTitle(getString(R.string.activity_route_title));

        mImageViewDrawing = (ImageView) findViewById(R.id.activity_route_image_drawing);

        //устанавливает фон
        mImageViewBackground = (ImageView) findViewById(R.id.activity_route_image_background);
        mImageViewBackground.setImageDrawable(getDrawable(R.drawable.ic_map_route_default_background));

        mFindWayButton = (Button) findViewById(R.id.activity_route_findway_button);
        mFindWayButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //point A & B должны выдергиваться из активити, а пока так:
                Point a = new Point(13,15);
                Point b = new Point(976, 876);

                ArrayList<Point> pointsList = findWay(a, b); //получаем путь
                drawWay(pointsList); //рисуем путь
            }
        });

        mRoomSpinner = (Spinner) findViewById(R.id.activity_route_room_spinner);
    }

    //рассчет пути
    private ArrayList<Point> findWay(Point pointA, Point pointB){
        /* Тут должен быть
        вызов метода, расчитывающего путь от точки А в точку В
         */

        ArrayList<Point> points = new ArrayList<>();

        switch (mRoomSpinner.getSelectedItemPosition()){
            case 0: {
                points.add(pointA);
                points.add(new Point(322,56));
                points.add(new Point(777,567));
                points.add(new Point(244,966));
                points.add(pointB);
                break;
            }

            case 1: {
                points.add(pointA);
                points.add(new Point(300,72));
                points.add(new Point(600,250));
                points.add(new Point(700,700));
                points.add(pointB);
                points.add(new Point(450,900));
                points.add(new Point(120,666));
                break;
            }
        }


        return points;
    }

    //рисует путь и отправляет его на экран
    private void drawWay(ArrayList<Point> pointsList){
        Bitmap bitmap = DrawHelper.drawWay(pointsList, getResources().getColor(R.color.colorPrimary));
        mImageViewDrawing.setImageBitmap(bitmap);
    }

}
