package com.geo.navigator.UI;

/**
 * Created by nikita on 12.04.17.
 */

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.geo.navigator.Model.IMapDownloaded;
import com.geo.navigator.Model.ISpinnerItem;
import com.geo.navigator.Model.MySpinnerAdapter;
import com.geo.navigator.Model.SimplePoint;
import com.geo.navigator.Model.ServerAPI;
import com.geo.navigator.R;
import com.geo.navigator.Database.MyDatabaseProvider;
import com.geo.navigator.Model.WayFinder;
import com.geo.navigator.Utils.DrawHelper;
import com.geo.navigator.Model.Edge;
import com.geo.navigator.Model.Map;
import com.geo.navigator.Model.Point;
import com.geo.navigator.Utils.FileHelper;
import com.geo.navigator.Utils.MyJSONParser;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.Locale;

public class RouteActivity extends AppCompatActivity implements IMapDownloaded {
    private static final String TAG = "RouteActivity";
    public static final int REQUEST_CODE_QR = 1;

    private static final int ITS_START_MAP = 410;  // указывают downloadMap на то, какая
    private static final int ITS_FINISH_MAP = 420;  // из карт была загружена

    private ImageView mImageViewDrawing;

    private ImageView mImageViewBackground;
    private Button mFindWayButton;
    private Button mScanQRCode;
    private Spinner mObjectSpinner;
    private Spinner mBuildingSpinner;
    private Spinner mPointSpinner;
    private TextView mWhereAreYouTextView;
    private ImageButton mUpdateSpinButton;
    private AlertDialog adLoading;
    private BottomSheetBehavior mBottomSheetBehavior;

    private LinearLayout mHintQrScanLayout;
    private RelativeLayout mHintLayout;

    Bitmap mWayBitmap;

    private Map mCurrentMap;
    private Point mStartPoint;  // Начальная и конечная
    private Point mFinishPoint; // точки

    private int mIdCurrentMap;
    private int mIdStartPoint;  // Начальная и конечная
    private SimplePoint mSimpleFinishPoint; // точки
    private boolean mIsLoadingNow; // алерт "загрузка" сейчас показан

    private RouteSaveFragment rsf; // удерживаемый фрагмент, в котором сохраняется вся инфа при поворотах

    //вызывать для запуска интентом
    public static Intent newIntent(Context context) {
        return new Intent(context, RouteActivity.class);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_route);
        setTitle(getString(R.string.activity_route_title));

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }


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

            Log.d(TAG, "Состояние восстановлено");
        }


        //алерт "загрузка"
        adLoading = createLoadingAlert();

        //инициализируем вьюхи
        initUI();

        //Устанавливает фиктивное местоположение по клику
        mWhereAreYouTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //  mIdStartPoint = 27;
                //    mIdCurrentMap = 2;
                //    setStartPoint();
            }
        });


        //Если активити пересоздается, то нужно вернуь интерфейс в предыдущее состояние
        setupSavedUI();

        //подписываемся на событие загрузки карты
        ServerAPI.setOnMapDLListener(this);

        if (mIsLoadingNow) {
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

    @Override // вызывается после завершения работы QRScannerActivity
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

    @Override // нажата кнопка Back
    public void onBackPressed() {
        if (mBottomSheetBehavior.getState() == BottomSheetBehavior.STATE_EXPANDED) {
            mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        } else {
            super.onBackPressed();
        }
    }


    //Устанавливает начальную точку пути и просит скачать карту, если у нас такой нет
    private void setStartPoint() {

        Log.d(TAG, "setStartPoint called");

        if (mCurrentMap == null) { // если метод вызван после порота активити, то будет не null
            mCurrentMap = MyDatabaseProvider.getMap(this, mIdCurrentMap); // находим карту
        }

        if (mCurrentMap == null) { //если не нашел такой карты, то качаем ее
            showAlertDownloadMap(mIdCurrentMap, ITS_START_MAP);
            return;
        }


        if (mStartPoint == null) { // если метод вызван после порота активити, то не null
            mStartPoint = MyDatabaseProvider.getPoint(this, mIdStartPoint);
            //mWayBitmap= FileHelper.readImage(getApplicationContext(), mCurrentMap.getImagePath());
        }

        if (mStartPoint == null) { //если не нашел такой точки, то качаем карту (ну мало ли что)
            String toastMessage = getString(R.string.activity_route_json_fail);
            Toast.makeText(this, toastMessage, Toast.LENGTH_SHORT).show();
            return;
        }

        Bitmap mapBitmap = FileHelper.readImage(getApplicationContext(), mCurrentMap.getImagePath());
        mapBitmap = scaleBitmap(mapBitmap); // увеличиваем битмап в соответствии с разрешением юзера

        mImageViewBackground.setImageBitmap(mapBitmap);
        mImageViewBackground.setBackgroundColor(Color.WHITE);
        mHintQrScanLayout.setVisibility(View.GONE);

        mWhereAreYouTextView.setText(mStartPoint.getDescription());

        String messToast = getString(R.string.activity_route_location_ok);
        Toast.makeText(this, messToast, Toast.LENGTH_LONG).show();

    }

    //рассчет пути
    private ArrayList<Point> findWay() {

        Edge[][] edges = MyDatabaseProvider.getEdgesMatrix(this, mCurrentMap.getId()); // получаем матрицу переходов
        WayFinder wf = new WayFinder(edges);
        mFinishPoint = MyDatabaseProvider.getPoint(this, mSimpleFinishPoint.getId());

        hideHint(); // по дефолту лучше убрать подсказку

        if (mFinishPoint == null) {  //если такой точки нет, то качаем карту
            showAlertDownloadMap(mSimpleFinishPoint.getMapId(), ITS_FINISH_MAP);
            return null;
        } else if (mFinishPoint.getMapId() != mCurrentMap.getId()) { //Если есть, но не на этой карте
            //ведем юзера к лестнице или выходу
            Map finishMap = MyDatabaseProvider.getMap(this, mFinishPoint.getMapId());

            if (finishMap.getBuildingId() == mCurrentMap.getBuildingId()) { //если в этом здании
                //ведем к лестнице
                Point[] stairsPoints = MyDatabaseProvider.getPointsByMeta(this, mCurrentMap.getId(), Point.META_STAIRS);

                if (stairsPoints == null) { // Лестниц на карте нет
                    String mess = getString(R.string.activity_route_error_routing);
                    Toast.makeText(this, mess, Toast.LENGTH_LONG).show();
                    Log.d(TAG, "findWay: Не могу построить маршрут до карты finishMap.id = " + finishMap.getId());
                    return null;
                }

                int pointsId[] = new int[stairsPoints.length];
                for (int i = 0; i < stairsPoints.length; i++) {
                    pointsId[i] = stairsPoints[i].getId();
                }

                int nearestPointId = wf.getNearestPoint(mStartPoint.getId(), pointsId);

                Log.d(TAG, "findWay: близжайшая точка = " + nearestPointId);

                mFinishPoint = MyDatabaseProvider.getPoint(this, nearestPointId);

                String dest = String.format(Locale.US,
                        getString(R.string.activity_route_hint_follow_to),
                        finishMap.getDescription());

                showHint(dest); // показываем подсказку
            } else {
                //ведем к выходу
                Point[] exitPoints = MyDatabaseProvider.getPointsByMeta(this, mCurrentMap.getId(), Point.META_EXIT);

                if (exitPoints != null) {

                    int pointsId[] = new int[exitPoints.length];
                    for (int i = 0; i < exitPoints.length; i++) {
                        pointsId[i] = exitPoints[i].getId();
                    }

                    int nearestPointId = wf.getNearestPoint(mStartPoint.getId(), pointsId);
                    mFinishPoint = MyDatabaseProvider.getPoint(this, nearestPointId);

                    String dest = getString(R.string.activity_route_hint_follow_to_exit);
                    showHint(dest);

                } else {   //если выхода на этой карте нет, то ведем к лестнице

                    Point[] stairsPoints = MyDatabaseProvider.getPointsByMeta(this, mCurrentMap.getId(), Point.META_STAIRS);

                    if (stairsPoints == null) { // Лестниц на карте нет
                        String mess = getString(R.string.activity_route_error_routing);
                        Toast.makeText(this, mess, Toast.LENGTH_LONG).show();
                        Log.d(TAG, "findWay: Не могу построить маршрут до карты finishMap.id = " + finishMap.getId());
                        return null;
                    }

                    int pointsId[] = new int[stairsPoints.length];
                    for (int i = 0; i < stairsPoints.length; i++) {
                        pointsId[i] = stairsPoints[i].getId();
                    }

                    int nearestPointId = wf.getNearestPoint(mStartPoint.getId(), pointsId);
                    mFinishPoint = MyDatabaseProvider.getPoint(this, nearestPointId);

                    String dest = getString(R.string.activity_route_hint_follow_to_exit);
                    showHint(dest);
                }

            }

        }

        int[] pointsIdArr;

        //если стартовая точки и есть конечная
        if(mStartPoint.getId() == mFinishPoint.getId()){
            //getShortestRoute в таком случае работает не всегда корректно, делаем так:
            pointsIdArr = new int[] {mStartPoint.getId(), mFinishPoint.getId()};
        } else {
            //находим кратчайший путь в виде массива id точек
            pointsIdArr = wf.getShortestRoute(mStartPoint.getId(), mFinishPoint.getId());
        }


        //отправляем статистику по посещениям
        sendStatistic(pointsIdArr);

        //преобразовываем его к списку точек
        return MyDatabaseProvider.getPointsListFromArray(this, mStartPoint.getMapId(), pointsIdArr);
    }

    //рисует путь и отправляет его на экран
    private void drawWay(ArrayList<Point> pointsList) {
        Bitmap mapBitmap = FileHelper.readImage(getApplicationContext(), mCurrentMap.getImagePath());
        int sizePx = mapBitmap.getHeight(); // получаем размер картинки (она все равно квадратная)

        mWayBitmap = DrawHelper.drawWay(pointsList, this, sizePx);
        mWayBitmap = scaleBitmap(mWayBitmap); // увеличиваем битмап в соответствии с разрешением пользователя
        mImageViewDrawing.setImageBitmap(mWayBitmap);

        hideBottomSheet();
    }


    // Показывает алерт о необходимости загрузки карты и, в случае согласия,
    // загружает ее
    private void showAlertDownloadMap(final int idDLMap, final int which_map) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.alert_title)
                .setMessage(R.string.alert_dl_map)
                .setPositiveButton(R.string.alert_positive, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        adLoading.show(); // показывает алерт "Загрузка..."
                        mIsLoadingNow = true;
                        // загружаем карту, результат придет в mapDownloaded()
                        ServerAPI.downloadMap(getApplicationContext(), idDLMap, which_map);
                    }
                })
                .setNegativeButton(R.string.alert_negative, null)
                .show();
    }

    @Override // вызывается из ServerAPI, когда карта загружена
    public void mapDownloaded(boolean result, int which_map) {
        adLoading.hide();
        mIsLoadingNow = false;
        switch (which_map) {
            case ITS_START_MAP:
                if (result) {
                    setStartPoint();
                } else {
                    showNetworkDisAlert();
                }
                break;

            case ITS_FINISH_MAP:
                if (result) {
                    drawWay(findWay());
                } else {
                    showNetworkDisAlert();
                }
                break;

            default:
                break;
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
        Log.d(TAG, "setupSavedUI: cm = " + mCurrentMap + "; sp = " + mStartPoint);
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

        //это все для floating action button и выезжающего снизу лейаута
        LinearLayout bottomSheet = (LinearLayout) findViewById(R.id.content_route);
        mBottomSheetBehavior = BottomSheetBehavior.from(bottomSheet);
        final FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);

        //скрываем кнопку при разворачивании bottom sheet
        mBottomSheetBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                if (BottomSheetBehavior.STATE_EXPANDED == newState) {
                    fab.animate().scaleX(0).scaleY(0).setDuration(100).start();
                } else if (BottomSheetBehavior.STATE_COLLAPSED == newState) {
                    fab.animate().scaleX(1).scaleY(1).setDuration(100).start();
                }
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {
            }

        });

        //разворачиваем bottom sheet по клику на fab
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                fab.animate().scaleX(0).scaleY(0).setDuration(100).start();
            }
        });


        //слой с подсказкой "отсканируйте qr код"
        mHintQrScanLayout = (LinearLayout) findViewById(R.id.activity_route_hint_qr_layout);

        //текст, отображающий ваше местоположение
        mWhereAreYouTextView = (TextView) findViewById(R.id.activity_route_where_are_you_text);

        //слой, на котором рисуется маршрут
        mImageViewDrawing = (ImageView) findViewById(R.id.activity_route_image_drawing);
        mImageViewDrawing.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View view, MotionEvent event) {
                Log.d(TAG, "mImageViewDrawing touch");
                return false;
            }
        });

        //устанавливает фон
        mImageViewBackground = (ImageView) findViewById(R.id.activity_route_image_background);
        mImageViewBackground.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View view, MotionEvent event) {
                Log.d(TAG, "mImageViewBackground touch");
                return true;
            }
        });

        //кнопка Построить маршрут
        mFindWayButton = (Button) findViewById(R.id.activity_route_findway_button);
        mFindWayButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mStartPoint != null && mSimpleFinishPoint != null) {
                    ArrayList<Point> way = findWay();
                    if (way != null) {
                        drawWay(way);
                        mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
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


        //подсказка сверху, например "следуйте на второй этаж"
        mHintLayout = (RelativeLayout) findViewById(R.id.route_hint);
        ImageButton imageButton = (ImageButton) findViewById(R.id.route_hint_button_close);
        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hideHint();
            }
        });


        spinnerAdapterInit();
    }

    //тут происходит какая-то вакханалия, за которую мне очень стыдно
    //инициализирует спиннеры и адаптеры в них
    private void spinnerAdapterInit() {
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

        /** Ниже листенеры, которые устранавливают адаптер в нижележащий спиннер в соответствии с
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
                if (id != -1) {
                    mSimpleFinishPoint = (SimplePoint) adapterView.getItemAtPosition(i);

                    /**
                     * !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
                     * !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
                     */

                    //     mSimpleFinishPoint = new SimplePoint(mSimpleFinishPoint.getId(), 1, mSimpleFinishPoint.getInfo());


                    Log.d(TAG, "mPointSpinner: finish point id = " + mSimpleFinishPoint.getId()
                            + "; map id = " + mSimpleFinishPoint.getMapId());
                } else {
                    mSimpleFinishPoint = null;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }


    //скрывает BottomSheet
    private void hideBottomSheet(){
        mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
    }


    //показывает подсказку сверху
    private void showHint(String dest) {
        if(mHintLayout.getVisibility() == View.VISIBLE) return;

        TextView tv = (TextView) findViewById(R.id.route_hint_destination);
        tv.setText(dest);

        Animation animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.route_hint_anim_show);
        mHintLayout.startAnimation(animation);
        mHintLayout.setVisibility(View.VISIBLE);
    }

    //скрывает подсказку сверху
    private void hideHint() {
        if(mHintLayout.getVisibility() == View.GONE) return;

        Animation animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.route_hint_anim_hide);
        mHintLayout.startAnimation(animation);
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                mHintLayout.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });
    }

    //отправляет статитстику по посещениям точки
    private void sendStatistic(final int pointsId[]) {

        SharedPreferences sp = getSharedPreferences(getString(R.string.preference_file_key),
                Context.MODE_PRIVATE);

        final String login = sp.getString(getString(R.string.preference_user_login), "default_login");

        if (login.equals("default_login"))
            return;

        new Thread(new Runnable() {
            @Override
            public void run() {
                for (int id : pointsId) {
                    ServerAPI.setPointStat(id, login);
                }
            }
        }).start();

    }

    //увеличивает картинку в соответсвии с dpi устройства (масштаб задается в values/dimens)
    private Bitmap scaleBitmap(Bitmap bitmap) {
        if (bitmap == null) {
            String mess = getString(R.string.activity_route_read_error);
            Toast.makeText(this, mess, Toast.LENGTH_LONG).show();
            return null;
        }

        int scale = getResources().getInteger(R.integer.scale_bitmap);
        return Bitmap.createScaledBitmap(bitmap, scale, scale, false);
    }
}