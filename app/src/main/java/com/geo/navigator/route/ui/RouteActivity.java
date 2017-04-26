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
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.geo.navigator.R;
import com.geo.navigator.qrscanner.ui.QRScannerActivity;
import com.geo.navigator.route.helper.DrawHelper;
import com.geo.navigator.route.model.Point;

import java.util.ArrayList;

public class RouteActivity extends AppCompatActivity {
    private static final String TAG = "RouteActivity";
    public static final int REQUEST_CODE_QR = 1;

    private ImageView mImageViewDrawing;
    private ImageView mImageViewBackground;
    private FrameLayout mFindWayButton;
    private Button mScanQRCode;

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
        mImageViewBackground.setImageDrawable(getResources()
                .getDrawable(R.drawable.qrcode_icon_with_text));

        //кнопка Построить маршрут
        mFindWayButton = (FrameLayout) findViewById(R.id.activity_route_findway_button);
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

        //кнопка Сканировать QR-code
        mScanQRCode = (Button) findViewById(R.id.activity_route_button_qr);
        mScanQRCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //вызов сканера
                Intent intent = new Intent(getApplicationContext(), QRScannerActivity.class);
                startActivityForResult(intent, REQUEST_CODE_QR);
            }
        });


        mRoomSpinner = (Spinner) findViewById(R.id.activity_route_room_spinner);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != RESULT_OK){
            switch (resultCode) {
                case QRScannerActivity.RESULT_PERMISSION_DENIED: {
                    //Если пользователь не дал разрешение на использование камеры
                    Toast.makeText(this,
                            getString(R.string.activity_route_need_permission),
                            Toast.LENGTH_LONG)
                            .show();
                    return;
                }

                default: return;
            }
        }

        //обработка результата сканирования qr-кода
        if (requestCode == REQUEST_CODE_QR){
            if(data != null){
                String qr_data = data.getStringExtra(QRScannerActivity.EXTRA_QR_DATA);
                Toast.makeText(this, qr_data, Toast.LENGTH_SHORT).show();
            }
        }
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
