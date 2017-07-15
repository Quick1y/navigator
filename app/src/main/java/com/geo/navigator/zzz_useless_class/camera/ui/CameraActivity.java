package com.geo.navigator.zzz_useless_class.camera.ui;

/**
 * Created by nikita on 11.04.17.
 */

import android.app.Activity;


public class CameraActivity extends Activity {

    /**
     * класс пока не нужен,
     * но мало ли


    private static final String TAG = "CameraActivity";
    private final int CAMERA0 = 0;


    //UI
    private TextView mCompassTextView;
    private TextView mStepsTextView;
    private TextureView mTextureView;


    //Сенсоры
    private SensorManager mSensorManager;
    private Sensor mStepSensor;
    private Sensor mOrientationSensor;
    private OrientationListener mOrientationListener;
    private StepCounterListener mStepCounterListener;

    //Камера
    private CameraManager mCameraManager = null;
    private CameraHelper myCamera;



    //вызывать для запуска интентом
    public static Intent newIntent(Context context){
        Intent intent = new Intent(context, CameraActivity.class);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        //для версий 6.0+ надо спросить разрешения явно
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            requestPermissions(new String[] {"android.permission.CAMERA"}, 1);
        }

        //тема без ActionBar
        setTheme(R.style.CameraActivityTheme);

        //текстовые поля
        mCompassTextView = (TextView) findViewById(R.id.activity_camera_compassTextView);
        mStepsTextView = (TextView) findViewById(R.id.activity_camera_stepsTextView);

        //TextureView для вывода изображения с камеры
        mTextureView = (TextureView) findViewById(R.id.activity_camera_textureView);


        //Работа с камерой 0 (основной)
        mCameraManager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);

        //получение информации по камере
        Log.i(TAG, "CameraID: " + CAMERA0);
        myCamera = new CameraHelper(mCameraManager, String.valueOf(CAMERA0), this);
        myCamera.viewFormatSize(ImageFormat.JPEG);

        //нужно подождать, когда TextureView будет готов
        mTextureView.setSurfaceTextureListener(new TextureView.SurfaceTextureListener() {
            @Override
            public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
                //устанавливаем View для отрисовки
                myCamera.setTextureView(mTextureView);

                //открытие камеры
                if(myCamera != null){
                    if(!myCamera.isOpen()){
                        myCamera.openCamera();
                    }
                }
            }

            @Override
            public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {

            }
            @Override
            public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
                return false;
            }
            @Override
            public void onSurfaceTextureUpdated(SurfaceTexture surface) {

            }
        });




        /* Ниже код, регистрирующий слушатели
        сенсоров для компаса и шагомера *\/
        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);

        //инициализация сенсоров для шагомера и компаса
        mStepSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);
        mOrientationSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION);

        mOrientationListener = new OrientationListener();
        mStepCounterListener = new StepCounterListener();

        /*
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN); *\/

    }

    @Override
    public void onResume(){
        super.onResume();

        //Регисрируем слушателей
        mSensorManager.registerListener(mOrientationListener, mOrientationSensor,SensorManager.SENSOR_DELAY_GAME);
        mSensorManager.registerListener(mStepCounterListener, mStepSensor, SensorManager.SENSOR_DELAY_GAME);
    }

    @Override
    protected void onPause(){
        super.onPause();

        //Отписываемся от слушателей
        mSensorManager.unregisterListener(mOrientationListener);
        mSensorManager.unregisterListener(mStepCounterListener);
    }




    //Вложенные классы Listener
    private class OrientationListener implements SensorEventListener {

        @Override
        public void onSensorChanged(SensorEvent event) {
            //градусы
            float degree = Math.round(event.values[0]);
            mCompassTextView.setText(getResources().getString(R.string.activity_camera_degree)
                    + ": " + degree);

        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {

        }
    }
    private class StepCounterListener implements SensorEventListener{
        private float mStepOffset;

        @Override
        public void onSensorChanged(SensorEvent event) {

            if (mStepOffset == 0) {
                mStepOffset = event.values[0];
            }

            mStepsTextView.setText(getResources().getString(R.string.activity_camera_steps)
                    + ": " + Float.toString(event.values[0] - mStepOffset));
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {

        }
    }

    */
}
