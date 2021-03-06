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

public class HomeGuestFragment extends Fragment {
    private static final String TAG = "HomeGuestFragment";

    private FrameLayout mFindWayFL;
    private FrameLayout mNewsFL;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home_guest, container, false);

        mFindWayFL = (FrameLayout) view.findViewById(R.id.g_activity_home_navigate_button_fl);
        mFindWayFL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = RouteActivity.newIntent(getContext());
                startActivity(intent);
            }
        });

        mNewsFL = (FrameLayout) view.findViewById(R.id.g_activity_home_news_button_fl);
        mNewsFL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = BrowserActivity.newIntent(getContext(),
                        BrowserActivity.NPI_NEWS_URL);
                startActivity(intent);
            }
        });


        return view;
    }
}
