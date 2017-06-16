package com.geo.navigator.useless_class;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.geo.navigator.R;
import com.geo.navigator.camera.ui.CameraActivity;
import com.geo.navigator.route.ui.RouteActivity;


/**
 * Created by nikita on 09.04.17.
 */

public class LoginFragment extends Fragment {

    private static final String TAG = "LoginFragment";

    private Button mLoginButton;
    private Button mRegistrationButton;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState){
        View view = inflater.inflate(R.layout.fragment_login, container, false);

        //Устанавливает заголовок Activity
        getActivity().setTitle(R.string.activity_entrance_login_title);

        /*
        //кнопка войти
        mLoginButton = (Button) view.findViewById(R.id.fragment_login_login_button);
        mLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //вызов RouteActivity по нажатию кнопки
                Intent intent = RouteActivity.newIntent(getContext());
                startActivity(intent);

            }
        });

        //кнопка зарегистрироваться
        mRegistrationButton = (Button) view.findViewById(R.id.fragment_login_registration_button);
        mRegistrationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startFragmentRegistration();
            }
        });
        mRegistrationButton.setClickable(false);  // пока так

*/

        return view;
    }


    //Запуск фрагмента "Регистрация"
    private void startFragmentRegistration(){
        Log.i(TAG, "startFragmentRegistration() called");

        FragmentManager fm = getActivity().getSupportFragmentManager();
        Fragment fragment = new RegistrationFragment();
        fm.beginTransaction()
                .replace(R.id.entrance_activity_fragment_container, fragment)
                .commit();
    }


}
