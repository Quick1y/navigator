package com.geo.navigator.UI;

/**
 * Created by nikita on 12.04.17.
 */

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;


import com.geo.navigator.Model.Building;
import com.geo.navigator.Model.IMapDownloaded;
import com.geo.navigator.Model.ISpinnerItem;
import com.geo.navigator.Model.LocalMap;
import com.geo.navigator.Model.MySpinnerAdapter;
import com.geo.navigator.Model.SimplePoint;
import com.geo.navigator.Model.ServerAPI;
import com.geo.navigator.R;
import com.geo.navigator.Database.MyDatabaseProvider;
import com.geo.navigator.Database.TempDatabase;
import com.geo.navigator.Model.DijkstrasAlgorithm;
import com.geo.navigator.Utils.DrawHelper;
import com.geo.navigator.Model.Edge;
import com.geo.navigator.Model.Map;
import com.geo.navigator.Model.Point;
import com.geo.navigator.Utils.FileHelper;
import com.geo.navigator.Utils.MyJSONParser;

import org.json.JSONException;

import java.util.ArrayList;

public class RouteActivity extends AppCompatActivity implements IMapDownloaded {
    private static final String TAG = "RouteActivity";
    public static final int REQUEST_CODE_QR = 1;
    private static final String IS_SAVED = "RouteActivity.IS_SAVED";

    private static final int tempIdMap = 101;
    private static final int tempIdPoint = 1001;

    private ImageView mImageViewDrawing;
    private ImageView mImageViewBackground;
    private FrameLayout mFindWayButton;
    private Button mScanQRCode;
    private Spinner mObjectSpinner;
    private Spinner mBuildingSpinner;
    private Spinner mPointSpinner;
    private TextView mWhereAreYouTextView;
    private ImageButton mUpdateSpinButton;
    private AlertDialog adLoading;

    ArrayList<LocalMap> mMaps;  // Все доступные карты
    SimplePoint[] mPoints; // Все доступные точки назначения
    Bitmap mWayBitmap;
    AsyncTask mDownloadAsync;

    private Map mCurrentMap;
    private Point mStartPoint;  // Начальная и конечная
    private Point mFinishPoint; // точки

    private int mIdCurrentMap;
    private int mIdStartPoint;  // Начальная и конечная
    private SimplePoint mSimpleFinishPoint; // точки
    private boolean mIsLoadingNow; // алерт "загрузка" сейчас показан

    private RouteSaveFragment rsf;
    private int mMapSpinPos;
    private int mPointSpinPos;


    //вызывать для запуска интентом
    public static Intent newIntent(Context context) {
        return new Intent(context, RouteActivity.class);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_route);
        setTitle(getString(R.string.activity_route_title));

        //достаем данные из фрагмента
        FragmentManager fm = getSupportFragmentManager();
        rsf = (RouteSaveFragment) fm.findFragmentByTag(RouteSaveFragment.TAG);

        if (rsf == null) {
            rsf = RouteSaveFragment.newInstance();
            fm.beginTransaction()
                    .add(rsf, RouteSaveFragment.TAG)
                    .commit();
        } else {
            mCurrentMap = rsf.currentMap;
            mStartPoint = rsf.startPoint;
            mFinishPoint = rsf.finishPoint;
            mWayBitmap = rsf.wayBitmap;
            mIdCurrentMap = rsf.idCurrentMap;
            mIsLoadingNow = rsf.isLoadingNow;
            mIdStartPoint = rsf.idStartPoint;

            //mMapSpinPos = rsf.mapSpinPos;

            Log.d(TAG, "Состояние восстановлено");
            //  mPointSpinPos = rsf.pointSpinPos; // пока не работает
        }


        adLoading = createLoadingAlert();
        mMaps = TempDatabase.getMaps(); //получаем список карт

        //инициализируем вьюхи
        initUI();

        //Устанавливает фиктивное местоположение по клику
        mWhereAreYouTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mIdStartPoint = 1;
                mIdCurrentMap = 2;
                setStartPoint();
            }
        });


        //Если активити пересоздается, то нужно вернуь интерфейс в предыдущее состояние
        setupSavedUI();

        //подписываемся на событие загрузки карты
        ServerAPI.setOnMapDLListener(this);

        if(mIsLoadingNow){
            // если перед повортом был показан алерт "загрузка", то показываем его
            adLoading.show();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        rsf.currentMap = mCurrentMap;
        rsf.startPoint = mStartPoint;
        rsf.finishPoint = mFinishPoint;
        rsf.wayBitmap = mWayBitmap;
        rsf.isLoadingNow = mIsLoadingNow;
        rsf.idCurrentMap = mIdCurrentMap;
        rsf.idStartPoint = mIdStartPoint;

        ServerAPI.deleteOnMapDLListener(this); // отписывамся от оповещений о загрузке карты
    }

    @Override // вызываетс после завершения работы QRScannerActivity
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != RESULT_OK) {
            switch (resultCode) {
                case QRScannerActivity.RESULT_PERMISSION_DENIED: {
                    //Если пользователь не дал разрешение на использование камеры
                    Toast.makeText(this,
                            getString(R.string.activity_route_need_permission),
                            Toast.LENGTH_LONG)
                            .show();
                    return;
                }

                default:
                    return;
            }
        }

        //обработка результата сканирования qr-кода
        if (requestCode == REQUEST_CODE_QR) {
            if (data != null) {
                String qr_data = data.getStringExtra(QRScannerActivity.EXTRA_QR_DATA);

                try {
                    mIdStartPoint = MyJSONParser.getPointIdToQR(qr_data);        // получем id текущей точки
                    mIdCurrentMap = MyJSONParser.getMapIdToQR(qr_data);

                    mCurrentMap = null;
                    mStartPoint = null;   // этакий костыль, потому что после вызова QR-сканнера
                    mFinishPoint = null;  // эти поля активити восстанавливаются, а не должны бы
                    mWayBitmap = null;
                    resetUI();

                    Log.d(TAG, "onActivityResult вызван");

                    setStartPoint();
                } catch (JSONException e) {
                    String toastMessage = getString(R.string.activity_route_json_fail);
                    Toast.makeText(this, toastMessage, Toast.LENGTH_LONG).show();
                    e.printStackTrace();
                }
            }
        }
    }




    //Устанавливает начальную точку пути и просит скачать карту, если у нас такой нет
    private void setStartPoint() {

        Log.d(TAG, "setStartPoint called");

        if (mCurrentMap == null) { // если метод вызван после порота активити, то будет не null
            mCurrentMap = MyDatabaseProvider.getMap(this, mIdCurrentMap); // находим карту
        }

        if (mCurrentMap == null) { //если не нашел такой карты, то качаем ее
            showAlertDownloadMap(mIdCurrentMap);
            return;
        }


        if (mStartPoint == null) { // если метод вызван после порота активити, то не null
            mStartPoint = MyDatabaseProvider.getPoint(this, mIdStartPoint);
            mWayBitmap = FileHelper.readImage(getApplicationContext(), mCurrentMap.getImagePath());
        }

        if (mStartPoint == null) { //если не нашел такой точки, то качаем карту (ну мало ли что)
            String toastMessage = getString(R.string.activity_route_json_fail);
            Toast.makeText(this, toastMessage, Toast.LENGTH_SHORT).show();
            return;
        }

        mWhereAreYouTextView.setText(mStartPoint.getDescription());
        mImageViewBackground.setImageBitmap(mWayBitmap);

        String messToast = getString(R.string.activity_route_location_ok);
        Toast.makeText(this, messToast, Toast.LENGTH_SHORT).show();
    }

    //рассчет пути
    private ArrayList<Point> findWay() {

        mFinishPoint = MyDatabaseProvider.getPoint(this, mSimpleFinishPoint.getId());

        if (mFinishPoint == null) {
            Toast.makeText(this, "Такой конечной точки нет, надо качать", Toast.LENGTH_SHORT).show();
            showAlertDownloadMap(mSimpleFinishPoint.getMapId());
            return null;
        } else if (mFinishPoint.getMapId() != mCurrentMap.getId()) {
            Toast.makeText(this, "Это другая карта", Toast.LENGTH_SHORT).show();
            //ведем юзера к лестнице или выходу
            return null;
        }

        ArrayList<Point> points;

        Edge[][] edges = MyDatabaseProvider.getEdgesMatrix(this, mCurrentMap.getId()); // получаем матрицу переходов

        DijkstrasAlgorithm dAlgorithm = new DijkstrasAlgorithm(edges);

        //находим кратчайший путь в виде массива id точек
        int[] pointsIdArr = dAlgorithm.getShortestRoute(mStartPoint.getId(), mFinishPoint.getId());

        //преобразовываем его к списку точек
        points = MyDatabaseProvider.getPointsListFromArray(this, mStartPoint.getMapId(), pointsIdArr);

        return points;
    }

    //рисует путь и отправляет его на экран
    private void drawWay(ArrayList<Point> pointsList) {
        mWayBitmap = DrawHelper.drawWay(pointsList, getResources().getColor(R.color.colorAccent));
        mImageViewDrawing.setImageBitmap(mWayBitmap);
    }




    // Показывает алерт о необходимости загрузки карты и, в случае согласия,
    // загружает ее
    private void showAlertDownloadMap(final int idDLMap) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.alert_title)
                .setMessage(R.string.alert_dl_map)
                .setPositiveButton(R.string.alert_positive, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        adLoading.show(); // показывает алерт "Загрузка..."
                        mIsLoadingNow = true;
                        // загружаем карту, результат придет в mapDownloaded()
                        ServerAPI.downloadMap(getApplicationContext(), idDLMap);
                    }
                })
                .setNegativeButton(R.string.alert_negative, null)
                .show();
    }

    @Override // вызывается из ServerAPI, когда карта загружена
    public void mapDownloaded(boolean result){
        adLoading.hide();
        mIsLoadingNow = false;
        if (result) {
            setStartPoint();
        } else {
            showNetworkDisAlert();
        }
    }

    //показывает алерт "Ошибка загрузки"
    private void showNetworkDisAlert() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.alert_network_title)
                .setMessage(R.string.alert_network_disable)
                .setIcon(R.drawable.ic_problem)
                .setPositiveButton(R.string.alert_positive, null)
                .show();
    }

    //создает и возвращает алерт "Загрузка..."
    private AlertDialog createLoadingAlert() {
        final IMapDownloaded context = this;
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        return builder.setView(R.layout.alert_loading)
                .setCancelable(false)
                .setNegativeButton(R.string.alert_network_cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        ServerAPI.deleteOnMapDLListener(context);
                        ServerAPI.getDownloadThread().interrupt();
                    }
                })
                .create();
    }




    //восстанавливает UI после поворота
    private void setupSavedUI() {
        if (mCurrentMap != null && mStartPoint != null) {
            setStartPoint();
        }

        if (mWayBitmap != null) {
            mImageViewDrawing.setImageBitmap(mWayBitmap);
        }
    }

    //сбрасывает UI в дефолтное состояние
    private void resetUI() {
        mImageViewDrawing.setImageBitmap(null);
        mImageViewBackground.setImageDrawable(getResources().getDrawable(R.drawable.qrcode_icon_with_text));
        mWhereAreYouTextView.setText(R.string.activity_route_location);
        spinnerAdapterInit();
    }

    //инициализируют все UI-элементы
    private void initUI() {
        //текст, отображающий ваше местоположение
        mWhereAreYouTextView = (TextView) findViewById(R.id.activity_route_where_are_you_text);

        //слой, на котором рисуется маршрут
        mImageViewDrawing = (ImageView) findViewById(R.id.activity_route_image_drawing);

        //устанавливает фон
        mImageViewBackground = (ImageView) findViewById(R.id.activity_route_image_background);
        mImageViewBackground.setImageDrawable(getResources().getDrawable(R.drawable.qrcode_icon_with_text));

        //кнопка Построить маршрут
        mFindWayButton = (FrameLayout) findViewById(R.id.activity_route_findway_button);
        mFindWayButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mStartPoint != null && mSimpleFinishPoint != null) {
                    ArrayList<Point> way = findWay();
                    if (way != null) {
                        drawWay(way);
                    }
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

        mUpdateSpinButton = (ImageButton) findViewById(R.id.activity_route_update_spinner);
        mUpdateSpinButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                spinnerAdapterInit();
            }
        });

        spinnerAdapterInit();
    }

    //тут происходит какая-то вакханалия, за которую мне очень стыдно
    //инициализирует спиннеры и адаптеры в них
    private void spinnerAdapterInit(){
        mObjectSpinner = (Spinner) findViewById(R.id.activity_route_object_spinner);      //Спиннер выбора объекта
        mBuildingSpinner = (Spinner) findViewById(R.id.activity_route_building_spinner);  //Спиннер выбора корпуса
        mPointSpinner = (Spinner) findViewById(R.id.activity_route_point_spinner);        //Спиннер выбора места назначения

        final String selectObject = getString(R.string.activity_route_select_object);
        final String selectBuilding = getString(R.string.activity_route_select_building);
        final String selectPoint = getString(R.string.activity_route_select_point);

        //устанавливает в спиннер выбора объекта адаптер
        new AsyncTask<Void, Void, MySpinnerAdapter>() {
            @Override
            protected MySpinnerAdapter doInBackground(Void... voids) {
                ArrayList<ISpinnerItem> objects = ServerAPI.getObjects();
                if (objects == null) {
                    return null;
                } else {
                    return new MySpinnerAdapter(objects, getLayoutInflater(), selectObject);
                }
            }

            @Override
            protected void onPostExecute(MySpinnerAdapter adapter) {
                //если adapter == null, то ошибка сети
                if (adapter == null) {
                    mObjectSpinner.setAdapter(
                            new MySpinnerAdapter(null, getLayoutInflater(), selectObject));
                    mBuildingSpinner.setAdapter(
                            new MySpinnerAdapter(null, getLayoutInflater(), selectBuilding));
                    mPointSpinner.setAdapter(
                            new MySpinnerAdapter(null, getLayoutInflater(), selectPoint));
                    showNetworkDisAlert();
                } else {
                    mObjectSpinner.setAdapter(adapter);
                    mObjectSpinner.refreshDrawableState();
                }
            }
        }.execute();

        /* Ниже листенеры, которые устранавливают адаптер в нижележащий спиннер в соответствии с
        * выбранным значением в данном спиннере. Т.к при повороте экрана все равно данные о выбранном пункте
        * спиннера потеряются, то ничего страшного, что это асинки. Ну разве что из-за них могут быть
        * утечки контекста, но это же ерунда, правда? */
        mObjectSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, final long id) {
                new AsyncTask<Void, Void, MySpinnerAdapter>() {
                    boolean defaultSelected = false; // true, если выбрано дефолтное значение

                    @Override
                    protected MySpinnerAdapter doInBackground(Void... voids) {
                        if (id != -1) { // если не выбран дефолтный вариант "выберите объект"
                            ArrayList<ISpinnerItem> buildings = ServerAPI.getBuildings(id);
                            if (buildings == null) {
                                defaultSelected = false;
                                return null;
                            } else {
                                defaultSelected = false;
                                return new MySpinnerAdapter(buildings, getLayoutInflater(), selectBuilding);
                            }
                        } else {
                            defaultSelected = true;
                            return null;
                        }
                    }

                    @Override
                    protected void onPostExecute(MySpinnerAdapter adapter) {
                        //если adapter == null, то ошибка сети
                        if (defaultSelected) {
                            //устанавливаем спиннеры в дефолтное состояние
                            mBuildingSpinner.setAdapter(
                                    new MySpinnerAdapter(null, getLayoutInflater(), selectBuilding));
                            mPointSpinner.setAdapter(
                                    new MySpinnerAdapter(null, getLayoutInflater(), selectPoint));

                            return;
                        }

                        if (adapter == null) {
                            showNetworkDisAlert();
                            mBuildingSpinner.setAdapter(
                                    new MySpinnerAdapter(null, getLayoutInflater(), selectBuilding));
                            mPointSpinner.setAdapter(
                                    new MySpinnerAdapter(null, getLayoutInflater(), selectPoint));
                            Log.d(TAG, "mObjectSpinner adapter is null");
                        } else {
                            mBuildingSpinner.setAdapter(adapter);
                            mBuildingSpinner.refreshDrawableState();
                        }
                    }
                }.execute();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        mBuildingSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(final AdapterView<?> adapterView, View view, int i, final long id) {
                new AsyncTask<Void, Void, MySpinnerAdapter>() {
                    boolean defaultSelected = false; // true, если выбрано дефолтное значение

                    @Override
                    protected MySpinnerAdapter doInBackground(Void... voids) {
                        if (id != -1) { // если не выбран дефолтный вариант
                            ArrayList<ISpinnerItem> points = ServerAPI.getPointsForBuilding(id);
                            if (points == null) {
                                defaultSelected = false;
                                return null;
                            } else {
                                defaultSelected = false;
                                return new MySpinnerAdapter(points, getLayoutInflater(), selectPoint);
                            }
                        } else {
                            defaultSelected = true;
                            return null;
                        }
                    }

                    @Override
                    protected void onPostExecute(MySpinnerAdapter adapter) {
                        //если adapter == null, то ошибка сети
                        if (defaultSelected) {
                            //устанавливаем спиннеры в дефолтное состояние
                            mPointSpinner.setAdapter(
                                    new MySpinnerAdapter(null, getLayoutInflater(), selectPoint));
                            return;
                        }

                        if (adapter == null) {
                            showNetworkDisAlert();
                            mPointSpinner.setAdapter(
                                    new MySpinnerAdapter(null, getLayoutInflater(), selectPoint));
                        } else {
                            mPointSpinner.setAdapter(adapter);
                            mPointSpinner.refreshDrawableState();
                        }
                    }
                }.execute();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        mPointSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long id) {
                if(id != -1){
                    mSimpleFinishPoint = (SimplePoint) adapterView.getItemAtPosition(i);
                    mCurrentMap = MyDatabaseProvider.getMapByPointId(getApplicationContext(), mSimpleFinishPoint.getId());

                    Log.d(TAG, "mPointSpinner: finish point id = " + mSimpleFinishPoint.getId() + ";" +
                            " map id = " + mSimpleFinishPoint.getMapId());
                } else {
                    mSimpleFinishPoint = null;
                    mCurrentMap = null;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

}