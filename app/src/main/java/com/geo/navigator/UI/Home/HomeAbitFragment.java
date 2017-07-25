package com.geo.navigator.UI.Home;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.geo.navigator.R;

/**
 * Created by nikita on 02.07.17.
 */

public class HomeAbitFragment extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home_abit, container, false);

        return view;
    }
}
