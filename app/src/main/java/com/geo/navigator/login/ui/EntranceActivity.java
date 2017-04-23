package com.geo.navigator.login.ui;

/**
 * Created by nikita on 12.04.17.
 */

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.geo.navigator.R;

public class EntranceActivity extends AppCompatActivity {

    private static final String TAG = "EntranceActivity";
    private FragmentManager fm;

    //вызывать для запуска интентом
    public static Intent newIntent(Context context){
        Intent intent = new Intent(context, EntranceActivity.class);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_entrance);

        fm = getSupportFragmentManager();

       // startFragment(R.id.fragment_login_root, new LoginFragment());      //логин на клиенте
        startFragment(R.id.fragment_web_login_WebView, new LoginWebFragment()); //регистрация на сайте

    }

    //запускает указанный фрагмент
    public void startFragment(int idRes, Fragment newFragment){

        Fragment fragment = fm.findFragmentById(idRes);
        if(fragment == null){
            fragment = newFragment;
            fm.beginTransaction()
                    .add(R.id.entrance_activity_fragment_container, fragment)
                    .commit();
        } else {
            fragment = newFragment;
            fm.beginTransaction()
                    .replace(R.id.entrance_activity_fragment_container, fragment)
                    .commit();
        }
    }
}
