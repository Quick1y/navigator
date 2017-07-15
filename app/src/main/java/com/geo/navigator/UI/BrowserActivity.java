package com.geo.navigator.UI;

/**
 * Created by nikita on 12.04.17.
 */

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.geo.navigator.R;
import com.geo.navigator.Utils.MyJavascriptInterface;

import java.util.regex.Pattern;

public class BrowserActivity extends AppCompatActivity {
    private static final String TAG = "BrowserActivity";
    private static final String EXTRA_URL = "BrowserActivity.EXTRA_URL";

    public static final String LOGIN_URL = "http://eyesnpi.ru/wp-login.php";
    public static final String REGISTRATION_URL = "http://eyesnpi.ru/wp-login.php?action=register";
    public static final String LOGIN_SUCCESS_URL = "http://eyesnpi.ru/wp-admin/";
    public static final String NPI_NEWS_URL = "https://www.npi-tu.ru/";
    public static final String NPI_PROGRESS_URL = "http://progress.npi-tu.ru/application/index";
    public static final String NPI_SCHEDULE_URL = "http://schedule.npi-tu.ru/fac";

    private static final String INTERFACE_NAME = "HTMLOUT";
    private static final String GET_URL_SCRIPT ="javascript:window."+INTERFACE_NAME+".processHTML(document.getElementById('user_login').value);";

    private WebView mWebView;
    private ProgressBar mProgressBar;
    private String mCurrentUrl;
    private String mRegex = "^(.)*(npi-tu\\.ru)(.)*$";  //Регулярное выражение, пускает только на сайт НПИ
    private Pattern mPattern;


    //вызывать для запуска интентом
    public static Intent newIntent(Context context, String url){
        Intent intent = new Intent(context, BrowserActivity.class);
        intent.putExtra(EXTRA_URL, url);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_browser);

        //для версий 6.0+ надо спросить разрешения явно
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.INTERNET)
                    != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[] {"android.permission.INTERNET"}, 1);
            }
        }


        //Достаем URL
        Intent intent = getIntent();
        if(intent != null){
            mCurrentUrl = intent.getStringExtra(EXTRA_URL);
        } else {
            mCurrentUrl = NPI_NEWS_URL; //дефолтный URL, на всякий случай
        }

        mPattern = Pattern.compile(mRegex);
        final MyJavascriptInterface myjsinterface = new MyJavascriptInterface(this);

        //отображает прогресс на странице
        mProgressBar = (ProgressBar) findViewById(R.id.activity_browser_progress_bar);
        mProgressBar.setMax(100);
        mProgressBar.setVisibility(View.GONE);


        mWebView = (WebView) findViewById(R.id.activity_browser_WebView);
        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.addJavascriptInterface(myjsinterface, INTERFACE_NAME);

        //отслеживание изменения URL у WebView
        mWebView.setWebViewClient(new WebViewClient(){
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                // return true; //Indicates WebView to NOT load the url;
                Log.d(TAG, "Current URL: " + view.getUrl() +"\nNew URL: " + url);
                Log.d(TAG, "Title is '"+ view.getTitle() + "'");

                if(url.equals(LOGIN_URL)){
                    updateTitle(url);
                    return false; //страница входа

                } else if(url.equals(REGISTRATION_URL)) {
                    updateTitle(url);
                    return false; //страница регистрации

                } else if(mPattern.matcher(url).find()) {
                    updateTitle(url);
                    return false; //Страница npi-tu.ru

                } else if(url.equals(LOGIN_SUCCESS_URL)){
                    mWebView.loadUrl(GET_URL_SCRIPT); // инъекция скрипта
                    setResult(RESULT_OK);
                    finish();
                    return true;

                } else {
                    Toast.makeText(getBaseContext(), //Сообщение "Переход запрещен"
                            getString(R.string.activity_browser_run_ban),
                            Toast.LENGTH_SHORT).show();
                    return true;
                }
            }
        });

        //прогрессбар загрузки страницы
        mWebView.setWebChromeClient(new WebChromeClient(){
            @Override
            public void onProgressChanged(WebView webView, int newProgress) {
                if(newProgress == 100) {
                    mProgressBar.setVisibility(View.GONE);
                } else {
                    mProgressBar.setVisibility(View.VISIBLE);
                    mProgressBar.setProgress(newProgress);
                }
            }
        });

        mWebView.loadUrl(mCurrentUrl);
        updateTitle(mCurrentUrl);

    }

    //Обновляет заголовок приложения
    private void updateTitle(String url){
        if(url.equals(LOGIN_URL)){
            setTitle(getString(R.string.activity_browser_login_title));
        } else if(url.equals(REGISTRATION_URL)) {
            setTitle(getString(R.string.activity_browser_registration_title));
        } else if(mPattern.matcher(url).find()) {
            setTitle(getString(R.string.activity_browser_npi_title));
        }
    }

}
