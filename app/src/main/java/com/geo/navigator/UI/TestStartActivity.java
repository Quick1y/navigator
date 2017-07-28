package com.geo.navigator.UI;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.geo.navigator.R;
import com.geo.navigator.UI.Home.HomeActivity;

public class TestStartActivity extends AppCompatActivity {

    private Button mButtonStartCamera;
    private Button mButtonStartLogin;
    private Button mButtonStartDrawing;
    private Button mButtonStartHome;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_start);

        mButtonStartCamera = (Button) findViewById(R.id.activity_test_start_button_startcamera);
        mButtonStartCamera.setText("Camera");
        mButtonStartCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               // Intent intent = CameraActivity.newIntent(getApplicationContext());
                //startActivity(intent);

                Toast.makeText(getApplicationContext(), "Фича отключена", Toast.LENGTH_SHORT).show();

            }
        });

        mButtonStartLogin = (Button) findViewById(R.id.activity_test_start_button_startlogin);
        mButtonStartLogin.setText("BrowserActivity");
        mButtonStartLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = BrowserActivity.newIntent(getApplicationContext(),
                        BrowserActivity.LOGIN_URL);
                startActivity(intent);
            }
        });

        mButtonStartDrawing = (Button) findViewById(R.id.activity_test_start_button_startdrawing);
        mButtonStartDrawing.setText("RouteActivity");
        mButtonStartDrawing.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = RouteActivity.newIntent(getApplicationContext());
                startActivity(intent);
            }
        });

        mButtonStartDrawing = (Button) findViewById(R.id.activity_test_start_button_starthome);
        mButtonStartDrawing.setText("HomeActivity");
        mButtonStartDrawing.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = HomeActivity.newIntent(getApplicationContext());
                startActivity(intent);
            }
        });

    }
}
