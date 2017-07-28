package com.geo.navigator.UI;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;

import com.geo.navigator.Model.Map;
import com.geo.navigator.Model.Point;

/**
 * Created by nikita on 16.07.17.
 *
 * В этом фрагменте сохраняются данные RouteActivity при ее пересоздании
 */

public class RouteSaveFragment extends Fragment {
    public static final String TAG = "RouteSaveFragment";

    public Map currentMap;
    public Point startPoint;  // Начальная и конечная
    public Point finishPoint;
    public Bitmap wayBitmap;

    public boolean isLoadingNow; // алерт "загрузка" сейчас показан
    public int idCurrentMap;
    public int idStartPoint;

    public static RouteSaveFragment newInstance(){
        return new RouteSaveFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setRetainInstance(true);

        Log.d(TAG, "RouteSaveFragment is created");
    }
}
