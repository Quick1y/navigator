package com.geo.navigator.UI;

/**
 * Created by nikita on 12.04.17.
 *
 * Считывние QR-кодов осуществляется благодаря
 * библиотеке ZXing
 * https://github.com/dm77/barcodescanner
 */

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.WindowManager;

import com.geo.navigator.R;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.Result;

import java.util.ArrayList;

import me.dm7.barcodescanner.zxing.ZXingScannerView;


public class QRScannerActivity extends AppCompatActivity implements ZXingScannerView.ResultHandler {
    private final static String TAG = "QRScannerActivity";
    private final static int PERMISSION_REQUEST = 1;
    public final static int RESULT_PERMISSION_DENIED = 2;
    public final static String EXTRA_QR_DATA = "QRScannerActivity.EXTRA_QR_DATA";

    private ZXingScannerView mScannerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);mScannerView = new ZXingScannerView(this);

        //тема без ActionBar
        setTheme(R.style.CameraActivityTheme);

        //полноэкранный режим
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        //проверка разрешения на использование камеры
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            int permission = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA);
            if(permission != PackageManager.PERMISSION_GRANTED){
                requestPermissions(new String[] {Manifest.permission.CAMERA}, PERMISSION_REQUEST); //запрос разрешений
            }
        }

        //Считывание только QR-кодов
        ArrayList<BarcodeFormat> formatList = new ArrayList<BarcodeFormat>();
        formatList.add(BarcodeFormat.QR_CODE);
        mScannerView.setFormats(formatList);

        setContentView(mScannerView);
    }

    @Override //обработка запроса прав на использование камеры
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if(requestCode == PERMISSION_REQUEST){
            if (grantResults[0] != PackageManager.PERMISSION_GRANTED){
                setResult(RESULT_PERMISSION_DENIED); //Если права не предоставлены
                finish();                            //завершает работу с resultCode = RESULT_PERMISSION_DENIED
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        mScannerView.setResultHandler(this); // Register ourselves as a handler for scan results.
        mScannerView.startCamera();          // Start camera on resume
    }

    @Override
    public void onPause() {
        super.onPause();
        mScannerView.stopCamera();           // Stop camera on pause
    }

    @Override
    public void handleResult(Result result) {
        Intent intent = new Intent();                      //если QR-код отсанирован заканчиваем работу
        intent.putExtra(EXTRA_QR_DATA, result.getText());  // и отпраляем данные RouteActivity
        setResult(RESULT_OK, intent);

        finish();
    }

}
