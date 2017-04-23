package com.geo.navigator.login.ui;

/**
 * Created by nikita on 12.04.17.
 */

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.geo.navigator.R;
import com.geo.navigator.camera.ui.CameraActivity;


public class LoginWebFragment extends Fragment {

    private static final String TAG = "LoginWebFragment";
    private static final String LOGIN_URL = "http://geo.websokol.ru/wp-login.php";
    private static final String REGISTRATION_URL = "http://geo.websokol.ru/wp-login.php?action=register";
    private static final String LOGIN_SUCCESS_URL = "http://geo.websokol.ru/wp-admin/profile.php";


    private WebView webView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState){
        View view = inflater.inflate(R.layout.fragment_web_login, container, false);

        //Устанавливает заголовок Activity
        getActivity().setTitle(R.string.activity_entrance_login_title);

        //для версий 6.0+ надо спросить разрешения явно
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.INTERNET)
                    != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[] {"android.permission.INTERNET"}, 1);
            }
        }

        webView = (WebView) view.findViewById(R.id.fragment_web_login_WebView);
        webView.loadUrl(LOGIN_URL);

        //отслеживание изменения URL у WebView
        webView.setWebViewClient(new WebViewClient(){
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                // return true; //Indicates WebView to NOT load the url;
                Log.d(TAG, "Currnet URL: " + view.getUrl() +"\nnNew URL: " + url);

                switch (url){
                    case LOGIN_URL: {
                        getActivity().setTitle(getString(R.string.activity_entrance_login_title));
                        return false; //страница входа
                    }
                    case REGISTRATION_URL:{
                        getActivity().setTitle(getString(R.string.activity_entrance_registration_title));
                        return false; //страница регистрации
                    }
                    case LOGIN_SUCCESS_URL: {  //вошел удачно
                        Intent intent = CameraActivity.newIntent(getContext());
                        startActivity(intent);
                        return true;
                    }

                    default: return true;
                }
            }
        });

        return view;
    }

}
