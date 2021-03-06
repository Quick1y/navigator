package com.geo.navigator.UI.Home;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.geo.navigator.R;
import com.geo.navigator.UI.BrowserActivity;
import com.geo.navigator.UI.RouteActivity;

/**
 * Created by nikita on 02.07.17.
 */

public class HomeAbitFragment extends Fragment {
    private static final String TAG = "HomeAbitFragment";

    private FrameLayout mFindWayFL;
    private FrameLayout mAbitFL;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home_abit, container, false);

        mFindWayFL = (FrameLayout) view.findViewById(R.id.a_activity_home_navigate_button_fl);
        mFindWayFL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = RouteActivity.newIntent(getContext());
                startActivity(intent);
            }
        });

        mAbitFL = (FrameLayout) view.findViewById(R.id.a_activity_home_abit_button_fl);
        mAbitFL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = BrowserActivity.newIntent(getContext(),
                        BrowserActivity.NPI_ABIT_URL);
                startActivity(intent);
            }
        });


        return view;
    }
}
