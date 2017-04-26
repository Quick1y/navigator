package com.geo.navigator.camera.helper;

/**
 * Created by nikita on 11.04.17.
 */

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.ImageFormat;
import android.graphics.SurfaceTexture;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.params.StreamConfigurationMap;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.util.Size;
import android.view.Surface;
import android.view.TextureView;

import java.util.Arrays;


public class CameraHelper {

    /** для этого класса нужен minSDK 21+


    private static final String TAG = "CameraHelper";
    private Context mContext;

    private CameraDevice mCameraDevice = null;
    private CameraManager mCameraManager = null;
    private String mCameraID = null;
    private TextureView mTextureView;

    private CameraCaptureSession mSession;

    public CameraHelper(@NonNull CameraManager cameraManager, @NonNull String cameraID, @NonNull Context context) {
        mCameraManager = cameraManager;
        mCameraID = cameraID;
        mContext = context;
    }

    //нужно вызывать ДО вызова openCamera
    public void setTextureView(TextureView textureView){
        mTextureView = textureView;
    }

    public boolean isOpen() {
        if (mCameraDevice == null) {
            return false;
        } else {
            return true;
        }
    }

    //нужно вызывать ПОСЛЕ вызова setTextureView
    public void openCamera() {
        try {
            //проверка на наличие разрешений на исп. камеры
            if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                return;
            }

            CameraCallback mCameraCallback = new CameraCallback();
            mCameraManager.openCamera(mCameraID, mCameraCallback, null);
        } catch (CameraAccessException cae){
            cae.printStackTrace();
        }

    }

    public void closeCamera(){
        if (mCameraDevice != null){
            mCameraDevice.close();
            mCameraDevice = null;
        }
    }

    //Основной метод для прорисовки
    private void createCameraPreviewSession(){
        SurfaceTexture texture = mTextureView.getSurfaceTexture();

        if(texture == null) {
            Log.e(TAG, "texture is null in createCameraPreviewSession()");
            return;
        }

        texture.setDefaultBufferSize(1920, 1080);
        Surface surface = new Surface(texture);

        try{
            final CaptureRequest.Builder builder =
                    mCameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);

            builder.addTarget(surface);

            mCameraDevice.createCaptureSession(
                    Arrays.asList(surface),
                    new CameraCaptureSession.StateCallback() {
                        @Override
                        public void onConfigured(@NonNull CameraCaptureSession session) {
                            mSession = session;
                            try {
                                mSession.setRepeatingRequest(builder.build(), null, null);
                            } catch (CameraAccessException cae){
                                cae.printStackTrace();
                            }
                        }

                        @Override
                        public void onConfigureFailed(@NonNull CameraCaptureSession session) {
                        }
                    },
                    null
            );
        } catch (CameraAccessException cae){
            cae.printStackTrace();
        }
    }

    //получение характеристик камеры
    public void viewFormatSize(int formatSize) {
        CameraCharacteristics cc = null;
        try {
            //Получение характеристик камеры
            cc = mCameraManager.getCameraCharacteristics(mCameraID);

            //Получение списка выходного формата камеры
            StreamConfigurationMap configurationMap =
                    cc.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);

            //Получения списка разрешений камеры
            Size[] sizesJPG = configurationMap.getOutputSizes(ImageFormat.JPEG);

            if (sizesJPG != null) {
                for (Size item : sizesJPG) {
                    Log.i(TAG, "w: " + item.getWidth() + "h: " + item.getHeight());
                }
            } else {
                Log.e(TAG, "camera with id " + mCameraID + " don't support format " + formatSize);
            }

        } catch (CameraAccessException cae) {
            cae.printStackTrace();
        }
    }

    //Анонимный класс, реализующий Callback для камеры
    private class CameraCallback extends CameraDevice.StateCallback {
        @Override
        public void onOpened(@NonNull CameraDevice camera) {
            mCameraDevice = camera;
            createCameraPreviewSession();
            Log.i(TAG, "Open camera with id: " + mCameraDevice.getId());
        }

        @Override
        public void onDisconnected(@NonNull CameraDevice camera) {
            Log.i(TAG, "Disconnect camera with id: " + mCameraDevice.getId());
            closeCamera();

        }

        @Override
        public void onError(@NonNull CameraDevice camera, int error) {
            Log.e(TAG, "Error with camera with id: " + mCameraDevice.getId());
        }
    }
 */
}
