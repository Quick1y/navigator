package com.geo.navigator.UI.Home;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.view.menu.MenuBuilder;
import android.support.v7.view.menu.MenuWrapperFactory;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.geo.navigator.Model.Map;
import com.geo.navigator.R;
import com.geo.navigator.Database.MyDatabaseProvider;
import com.geo.navigator.Database.TempDatabase;
import com.geo.navigator.Model.UserStatus;
import com.geo.navigator.Model.ServerAPI;
import com.geo.navigator.Model.Edge;
import com.geo.navigator.Model.Point;
import com.geo.navigator.UI.BrowserActivity;
import com.geo.navigator.Utils.FileHelper;

public class HomeActivity extends AppCompatActivity {
    private static final String TAG = "HomeActivity";
    private static final String FRAGMENT_TAG = "HomeFragment";

    private static final int REQUEST_LOGIN = 100;
    private static final String USER_DEFAULT_LOGIN = "Default_Login";

    private static final String USER_DEFAULT_ROLE = "Default_Role";
    private static final String USER_STUDENT_ROLE = "Студент";

    private static final int PERMISSION_REQUEST = 111;

    private LinearLayout mLoginErrLayout;
    private LinearLayout mNeedLoginLayout;
    private LinearLayout mLoginingLayout;
    private LinearLayout mNeedPermLayout;

    private Menu mMenu;

    private String mUserLogin;
    private String mUserRole;


    //вызывать для запуска интентом
    public static Intent newIntent(Context context) {
        Intent intent = new Intent(context, HomeActivity.class);
        return intent;
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.route_menu, menu);

        mMenu = menu;

        if (mUserLogin.equals(USER_DEFAULT_LOGIN)) {
            mMenu.getItem(0).setVisible(false);
            mMenu.getItem(1).setVisible(true);
        } else {
            mMenu.getItem(1).setVisible(false);
            mMenu.getItem(0).setVisible(true);
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.route_menu_logout:
                logout();
                return true;

            case R.id.route_menu_login:
                Intent intent = BrowserActivity.newIntent(getBaseContext(),
                        BrowserActivity.LOGIN_URL);
                startActivityForResult(intent, REQUEST_LOGIN);
                return true;

            case R.id.route_menu_fil_db_points:
                Point[] points = TempDatabase.getPointsForMapArrFull();

                try {
                    MyDatabaseProvider.setPoints(this, points);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }

                return true;

            case R.id.route_menu_fil_db_edges:
                Edge[] edges = TempDatabase.getEdgesInLineFull();

                try {
                    MyDatabaseProvider.setEdges(this, edges);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }

            case R.id.route_menu_fil_db_maps:
                Map[] maps = TempDatabase.getMapsFull().toArray(new Map[]{});

                Bitmap korpus1 = BitmapFactory.decodeResource(getResources(),
                        R.drawable.korpus1);
                Bitmap background = BitmapFactory.decodeResource(getResources(),
                        R.drawable.route_background);

                FileHelper.writeImage(this, korpus1, "img_korpus1");
                FileHelper.writeImage(this, background, "background");

                try {
                    MyDatabaseProvider.setMaps(this, maps);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
                return true;


            case R.id.route_menu_download_map:
                try {
                    new AsyncTask<Void, Void, Void>(){

                        @Override
                        protected Void doInBackground(Void... voids) {
                            ServerAPI.getObjects();
                            ServerAPI.getBuildings(1);
                            ServerAPI.getPointsForBuilding(1);
                            return null;
                        }

                    }.execute();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
                return true;

            case R.id.route_menu_clear_db:
                MyDatabaseProvider.clearDatabase(this);
                FileHelper.clearImageCache(null);
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        mLoginErrLayout = (LinearLayout) findViewById(R.id.activity_home_login_problem_lay);
        mLoginErrLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               requestUserRole();
            }
        });

        mLoginingLayout = (LinearLayout) findViewById(R.id.activity_home_loading);

        mNeedLoginLayout = (LinearLayout) findViewById(R.id.activity_home_login_ask_lay);
        mNeedLoginLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = BrowserActivity.newIntent(getBaseContext(),
                        BrowserActivity.LOGIN_URL);
                startActivityForResult(intent, REQUEST_LOGIN);
            }
        });

        mNeedPermLayout = (LinearLayout) findViewById(R.id.activity_home_need_permission);
        mNeedPermLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showPermissionRequest();
            }
        });


        //достаем из SharedPreferences логин и статус пользователя
        SharedPreferences sharedPref = getSharedPreferences(
                getString(R.string.preference_file_key), MODE_PRIVATE);

        mUserLogin = sharedPref.getString(getString(R.string.preference_user_login),
                USER_DEFAULT_LOGIN);
        mUserRole = sharedPref.getString(getString(R.string.preference_user_role),
                USER_DEFAULT_ROLE);

        if (mUserLogin.equals(USER_DEFAULT_LOGIN)) {
            showNeedLogin(); // показать диалог о необходимости входа
        } else {
            // Показываем фрагмент, соответсвующий роли пользователя
            hideAll();
            showFragment();
        }
    }


    @Override
    protected void onDestroy(){
        super.onDestroy();
        //устанавливаем статус пользователя на сервере Offline
        if(!mUserLogin.equals(USER_DEFAULT_LOGIN)
                && !mUserRole.equals(USER_DEFAULT_ROLE)){
            setUserStatus(UserStatus.offline);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_LOGIN: { //Вызывается после попытки входа
                if (resultCode == RESULT_OK) { // если залогинился успешно
                    showLogining();
                    requestUserRole();
                } else {
                    showLoginingError();
                }
            }
        }
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState){
        // тут НЕ вызываем super().onSaveInstanceState(bundle), потому что если вызвать,
        // то словим баг на добавлении фрагмента после запроса пермишенов
    }

    @Override //обработка запроса прав на использование камеры
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if(requestCode == PERMISSION_REQUEST){
            if (grantResults[0] != PackageManager.PERMISSION_GRANTED){
                showPermissionNeed();
            } else {
                hideAll();
                showFragment();
            }
        }
    }

    private void showPermissionRequest(){
        //проверка разрешения на использование камеры
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            int permission_write = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
            int permission_read = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE);

            if(permission_write != PackageManager.PERMISSION_GRANTED
                    || permission_read != PackageManager.PERMISSION_GRANTED){
                requestPermissions(new String[] {Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.READ_EXTERNAL_STORAGE}, PERMISSION_REQUEST); //запрос разрешений
            }
        }
    }

    //Показывает нужный фрагмент
    private void showFragment() {

        //сначала запрашиваем разрешения на чтение/запись
        int permission_write = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int permission_read = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE);

        if(permission_write != PackageManager.PERMISSION_GRANTED
                || permission_read != PackageManager.PERMISSION_GRANTED) {
            showPermissionRequest();
            return;
        }


        FragmentManager fm = getSupportFragmentManager();

        //устанавливаем статус пользователя на сервере Online
        setUserStatus(UserStatus.online);

        switch (mUserRole) {
            case USER_STUDENT_ROLE: // пока так, потом добавить для всех ролей
                Fragment fragment = new HomeStudentFragment();
                fm.beginTransaction()
                        .add(R.id.activity_home_root_layout, fragment, FRAGMENT_TAG)
                        .commit();
                break;

            case USER_DEFAULT_ROLE:
                //если не удалось получить роль пользователя
                showLoginingError();
                break;

            default: Log.d(TAG, "Да, мы сюда не попали. Роль: " + mUserRole);

        }

        Toast.makeText(this, "Добро пожаловать, " + mUserLogin, Toast.LENGTH_SHORT).show();

    }

    private void  hideFragment(){
        setUserStatus(UserStatus.offline);

        FragmentManager fm = getSupportFragmentManager();
        Fragment fragment = fm.findFragmentByTag(FRAGMENT_TAG);
        if (fragment != null){
            fm.beginTransaction()
                    .detach(fragment)
                    .commit();
        }

        showNeedLogin();
    }

    //запросить роль пользователя
    private void requestUserRole() {
        final Context context = getBaseContext();
        final SharedPreferences sp = context.getSharedPreferences(
                context.getString(R.string.preference_file_key),
                Context.MODE_PRIVATE);

        final SharedPreferences.Editor editor = sp.edit();

        //если пользователь даже не получил логин, то отправляем получать
        if(sp.getString(context.getString(R.string.preference_user_login), "").equals("")){
            Intent intent = BrowserActivity.newIntent(getBaseContext(),
                    BrowserActivity.LOGIN_URL);
            startActivityForResult(intent, REQUEST_LOGIN);
            return;
        }

        //если получил, то запрашиваем его роль еще раз
        new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... objects) {
                // тут делаем искуственную задержку, чтобы логин успел записаться в SP в MyJavascriptInterface

                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                //считываем полученный логин
                mUserLogin = sp.getString(getString(R.string.preference_user_login),
                        USER_DEFAULT_LOGIN);

                //тут идем в сеть и просим пользовательскую роль по логину mUserLogin
                return ServerAPI.getUserRole(mUserLogin);
            }

            @Override
            protected void onPostExecute(String role) {
                //тут закрываем Алерт загрузка и показываем фрагмент, или закрываем из-за недоступности

                if(role == null){
                    // показать экран "проблемы с соединением"
                    showLoginingError();
                    return;
                }

                editor.putString(context.getString(R.string.preference_user_role), role);
                editor.commit();

                mUserRole = role;
                Log.d(TAG, "Получил роль: " + role);

                hideAll();
                showFragment();
            }
        }.execute();
    }

    //установить статус пользовалтеля
    private void setUserStatus(final UserStatus status){
        new Thread(new Runnable() {
            @Override
            public void run() {
                boolean success = ServerAPI.setUserStatus(mUserLogin, status);

                if (!success){
                    Log.d(TAG, "Не удалость изменить статус на " + status );
                } else {
                    Log.d(TAG, "Статус изменен на " + status );
                }

            }
        }).start();
    }


    private void logout(){
        String message = String.format(getString(R.string.activity_home_alert_message), mUserLogin);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.activity_home_alert_title)
                .setMessage(message)
                .setPositiveButton(R.string.activity_home_alert_positive, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        SharedPreferences sp = getSharedPreferences(getString(R.string.preference_file_key),
                                Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = sp.edit();
                        editor.remove(getString(R.string.preference_user_login));
                        editor.remove(getString(R.string.preference_user_role));
                        editor.commit();

                        hideFragment();
                    }
                })
                .setNegativeButton(R.string.activity_home_alert_negative, null)
                .show();
    }





    private void hideAll(){
        mNeedLoginLayout.setVisibility(View.GONE);
        mLoginingLayout.setVisibility(View.GONE);
        mLoginErrLayout.setVisibility(View.GONE);
        mNeedPermLayout.setVisibility(View.GONE);

        if(mMenu != null) {
            mMenu.getItem(0).setVisible(true);   // меняем видимость кнопок меню
            mMenu.getItem(1).setVisible(false);  // войти и выйти
        }
    }

    private void showPermissionNeed(){
        mNeedLoginLayout.setVisibility(View.GONE);
        mLoginingLayout.setVisibility(View.GONE);
        mLoginErrLayout.setVisibility(View.GONE);
        mNeedPermLayout.setVisibility(View.VISIBLE);
    }

    private void showLoginingError(){
        mNeedLoginLayout.setVisibility(View.GONE);
        mLoginingLayout.setVisibility(View.GONE);
        mNeedPermLayout.setVisibility(View.GONE);
        mLoginErrLayout.setVisibility(View.VISIBLE);

        if(mMenu != null) {
            mMenu.getItem(0).setVisible(false);
            mMenu.getItem(1).setVisible(true);
        }
    }

    private void showLogining(){
        mNeedLoginLayout.setVisibility(View.GONE);
        mLoginErrLayout.setVisibility(View.GONE);
        mNeedPermLayout.setVisibility(View.GONE);
        mLoginingLayout.setVisibility(View.VISIBLE);

    }

    private void showNeedLogin(){
        mLoginingLayout.setVisibility(View.GONE);
        mLoginErrLayout.setVisibility(View.GONE);
        mNeedPermLayout.setVisibility(View.GONE);
        mNeedLoginLayout.setVisibility(View.VISIBLE);

        if(mMenu != null){
            mMenu.getItem(0).setVisible(false);
            mMenu.getItem(1).setVisible(true);
        }

    }

}
