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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.geo.navigator.R;
import com.geo.navigator.database.TempDatabase;
import com.geo.navigator.qrscanner.ui.QRScannerActivity;
import com.geo.navigator.route.dijkstra.DijkstrasAlgorithm;
import com.geo.navigator.route.helper.DrawHelper;
import com.geo.navigator.route.model.Edge;
import com.geo.navigator.route.model.Map;
import com.geo.navigator.route.model.Point;

import java.util.ArrayList;

public class RouteActivity extends AppCompatActivity {
    private static final String TAG = "RouteActivity";
    public static final int REQUEST_CODE_QR = 1;

    private static final int tempIdMap = 101;
    private static final int tempIdPoint = 1001;

    private ImageView mImageViewDrawing;
    private ImageView mImageViewBackground;
    private FrameLayout mFindWayButton;
    private Button mScanQRCode;
    private Spinner mRoomSpinner;
    private Spinner mMapSpinner;
    private TextView mWhereAreYouTextView;

    ArrayList<Map> mMaps;  // Все доступные карты
    ArrayList<Point> mPoints; // Все доступные точки назначения
    private Point mStartPoint;  // Начальная и конечная
    private Point mFinishPoint; // точки

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

        mMaps = TempDatabase.getMaps(); //получаем список карт

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
                if(mStartPoint != null && mFinishPoint != null){
                    ArrayList<Point> way = findWay(mStartPoint, mFinishPoint);
                    drawWay(way);
                } else {
                    Toast.makeText(getBaseContext(),
                            getString(R.string.activity_route_choose_loc_and_dest_point), Toast.LENGTH_LONG)
                            .show();
                }
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

        //Спиннер выбора места назначения
        mRoomSpinner = (Spinner) findViewById(R.id.activity_route_room_spinner);
        mRoomSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                mFinishPoint = mPoints.get(position); // устанавливает точку назначения
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        //Спиннер выбора карты
        mMapSpinner = (Spinner) findViewById(R.id.activity_route_map_spinner);
        //Заполняем список карт
        ArrayAdapter<String> mapAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_dropdown_item,
                TempDatabase.getMapsDescription());
        mMapSpinner.setAdapter(mapAdapter);
        //Когда пользователь выберет карту становится доступным выбор точки назначения
        mMapSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Map map = mMaps.get(position);
                ArrayAdapter<String> roomAdapter = new ArrayAdapter<String>(getBaseContext(),
                        android.R.layout.simple_spinner_dropdown_item,
                        TempDatabase.getPointsDescsForMap(map));
                mRoomSpinner.setAdapter(roomAdapter);
                mRoomSpinner.refreshDrawableState();

                mPoints = TempDatabase.getPointsForMap(map); //загружает все точки назначения для этой карты
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });




        //Устанавливает фиктивное местоположение по клику
        mWhereAreYouTextView = (TextView) findViewById(R.id.activity_route_where_are_you_text);
        mWhereAreYouTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mStartPoint = TempDatabase.getPoint(tempIdMap, tempIdPoint);
                mWhereAreYouTextView.setText(mStartPoint.getDescription());
                mImageViewBackground.setImageDrawable(getResources()
                        .getDrawable(R.drawable.korpus1));
            }
        });
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

               // mStartPoint = TempDatabase.getPoint(tempIdMap, tempIdPoint);
               // mWhereAreYouTextView.setText(mStartPoint.getDescription());
            }
        }
    }


    //рассчет пути
    private ArrayList<Point> findWay(Point pointA, Point pointB){
        ArrayList<Point> points = new ArrayList<>();

        Edge[][] edges = TempDatabase.getEdges(); // получаем матрицу переходов

        DijkstrasAlgorithm dAlgorithm = new DijkstrasAlgorithm(edges);

        //находим кратчайший путь в виде массива id точек
        int[] pointsIdArr = dAlgorithm.getShortestRoute(mStartPoint.getId(), mFinishPoint.getId());

        //преобразовываем его к листу точек
        points = TempDatabase.getPointsListFromArray(mStartPoint.getMapId(), pointsIdArr);

        return points;
    }

    //рисует путь и отправляет его на экран
    private void drawWay(ArrayList<Point> pointsList){
        Bitmap bitmap = DrawHelper.drawWay(pointsList, getResources().getColor(R.color.colorPrimary));
        mImageViewDrawing.setImageBitmap(bitmap);
    }

}
