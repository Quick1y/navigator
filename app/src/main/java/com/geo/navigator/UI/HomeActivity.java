package com.geo.navigator.UI;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.geo.navigator.R;
import com.geo.navigator.Database.MyDatabaseProvider;
import com.geo.navigator.Database.TempDatabase;
import com.geo.navigator.Model.UserStatus;
import com.geo.navigator.Model.ServerAPI;
import com.geo.navigator.Model.Edge;
import com.geo.navigator.Model.Point;

public class HomeActivity extends AppCompatActivity {
    private static final String TAG = "HomeActivity";

    private static final int REQUEST_LOGIN = 100;
    private static final String USER_DEFAULT_LOGIN = "Default_Login";

    private static final String USER_DEFAULT_ROLE = "Default_Role";
    private static final String USER_STUDENT_ROLE = "Студент";


    private String mUserLogin;
    private String mUserRole;


    //вызывать для запуска интентом
    public static Intent newIntent(Context context) {
        Intent intent = new Intent(context, HomeActivity.class);
        return intent;
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.route_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.route_menu_fil_db_points:
                Point[] points = TempDatabase.getPointsForMapArr(101);

                try {
                    MyDatabaseProvider.setPoints(this, points);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }

                return true;

            case R.id.route_menu_fil_db_edges:
                Edge[] edges = TempDatabase.getEdgesInLine();

                try {
                    MyDatabaseProvider.setEdges(this, edges);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }

                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        //достаем из SharedPreferences логин и статус пользователя
        SharedPreferences sharedPref = getSharedPreferences(
                getString(R.string.preference_file_key), MODE_PRIVATE);

        mUserLogin = sharedPref.getString(getString(R.string.preference_user_login),
                USER_DEFAULT_LOGIN);
        mUserRole = sharedPref.getString(getString(R.string.preference_user_role),
                USER_DEFAULT_ROLE);

        if (mUserLogin.equals(USER_DEFAULT_LOGIN)) {
            showLoginAlert(); // показать диалог о необходимости входа
        } else {
            // Показываем фрагмент, соответсвующий роли пользователя
            showFragment();
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_LOGIN: { //Вызывается после попытки входа
                if (resultCode == RESULT_OK) { // если залогинился успешно
                    // пока мы получаем роль нужно показывать какой-нибудь алерт "Вход, подождите"
                    requestUserRole();
                } else {
                    showLoginAlert();
                }
            }
        }
    }

    private void showLoginAlert() {
        //предложение войти
        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(this);
        alertBuilder.setMessage(R.string.activity_home_alert_message)
                .setNegativeButton(R.string.activity_home_alert_negative,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                finish();
                            }
                        })
                .setPositiveButton(R.string.activity_home_alert_positive,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                Intent intent = BrowserActivity.newIntent(getBaseContext(),
                                        BrowserActivity.LOGIN_URL);
                                startActivityForResult(intent, REQUEST_LOGIN);
                            }
                        })
                .setOnCancelListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialogInterface) {
                        finish();
                    }
                })
                .show();
    }

    //Показывает нужный фрагмент
    private void showFragment() {
        FragmentManager fm = getSupportFragmentManager();

        //устанавливаем статус пользователя на сервере Online
        setUserStatus(UserStatus.online);

        switch (mUserRole) {
            case USER_STUDENT_ROLE: // пока так, потом добавить для всех ролей
                Fragment fragment = new HomeStudentFragment();
                fm.beginTransaction()
                        .add(R.id.activity_home_root_layout, fragment)
                        .commit();
                break;

            case USER_DEFAULT_ROLE:
                //если не удалось получить роль пользователя
                AlertDialog.Builder alertBuilder = new AlertDialog.Builder(this);
                alertBuilder.setMessage(R.string.activity_home_alert_network_message)
                        .setPositiveButton(R.string.activity_home_alert_network_positive,
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        Intent intent = BrowserActivity.newIntent(getBaseContext(),
                                                BrowserActivity.LOGIN_URL);
                                        startActivityForResult(intent, REQUEST_LOGIN);
                                    }
                                })
                        .setNegativeButton(R.string.activity_home_alert_network_negative,
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        finish();
                                    }
                                })
                        .setOnCancelListener(new DialogInterface.OnCancelListener() {
                            @Override
                            public void onCancel(DialogInterface dialogInterface) {
                                finish();
                            }
                        })
                        .show();
                break;

            default: Log.d(TAG, "Да, мы сюда не попали. Роль: " + mUserRole);

        }

        Toast.makeText(this, "Добро пожаловать, " + mUserLogin, Toast.LENGTH_SHORT).show();

    }

    //запросить роль пользователя
    private void requestUserRole() {
        final Context context = getBaseContext();
        final SharedPreferences sp = context.getSharedPreferences(
                context.getString(R.string.preference_file_key),
                Context.MODE_PRIVATE);

        final SharedPreferences.Editor editor = sp.edit();

        // Надо алерт загрузка показать


        new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... objects) {
                // тут делаем искуственную задержку, чтобы логин успел записаться в SP в MyJavascriptInterface
                try {
                    Thread.sleep(500);
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
                    return;
                }

                editor.putString(context.getString(R.string.preference_user_role), role);
                editor.commit();

                mUserRole = role;
                Log.d(TAG, "Получил роль: " + role);

                showFragment();
            }
        }.execute();
    }

    //установить статус пользовалтеля
    private void setUserStatus(final UserStatus status){
        new Thread(new Runnable() {
            @Override
            public void run() {
                ServerAPI.setUserStatus(mUserLogin, status);
                Log.d(TAG, "Статус изменен");
            }
        }).start();
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
}
