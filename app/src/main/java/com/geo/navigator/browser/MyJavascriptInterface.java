package com.geo.navigator.browser;

import android.content.Context;
import android.content.SharedPreferences;
import android.webkit.JavascriptInterface;

import com.geo.navigator.R;


/**
 * Created by nikita on 13.06.17.
 *
 * Метод processHTML вызывается при обработке js на странице логина. Там же в код
 * производится инъекция js-скрипта, из-за которой в processHTML приходит только логин пользователя.
 * После получения логина пользователя он записывается в Shared Preferences.
 */

public class MyJavascriptInterface {
    private static final String TAG = "MyJavascriptInterface";
    private Context mContext;

    public MyJavascriptInterface(Context context){
        mContext = context;
    }

    @JavascriptInterface
    @SuppressWarnings("unused")
    public void processHTML(String userLogin)
    {

        //получая логин пишет его в SharedPreferences
        SharedPreferences sp = mContext.getSharedPreferences(mContext.getString(R.string.preference_file_key),
                Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString(mContext.getString(R.string.preference_user_login), userLogin);
        editor.commit();

    }

}
