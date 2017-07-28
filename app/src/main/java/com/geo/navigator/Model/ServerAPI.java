package com.geo.navigator.Model;


import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.geo.navigator.Database.MyDatabaseProvider;
import com.geo.navigator.Utils.FileHelper;
import com.geo.navigator.Utils.MyJSONParser;
import com.geo.navigator.Utils.QueryExecutor;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.Locale;

/**
 * Created by nikita on 13.06.17.
 *
 * Этот класс обращается к серверу, возвращая информацию в виде
 * объектов модели приложения
 */

public class ServerAPI {
    private static final String TAG = "ServerAPI";
    private static final String HANDLER_RESULT = "ServerAPI.HANDLER_RESULT";

    private static final String SET_USER_STATUS_URL = "http://eyesnpi.ru/API/APIMOBILE/api2.php?action=setstatus&status=%s&login=%s";
    private static final String SET_POINT_INFO = "http://eyesnpi.ru/API/APIMOBILE/api2.php?action=setpointinfo&idPoint=%s&login=%s";
    private static final String GET_USER_ROLE_URL = "http://eyesnpi.ru/API/APIMOBILE/api.php?action=getrol&login=%s";
    private static final String DOWNLOAD_MAP = "http://eyesnpi.ru/API/APIMOBILE/api.php?action=getmap&Id=%s";
    private static final String GET_OBJECTS = "http://eyesnpi.ru/API/APIMOBILE/api.php?action=getobj";
    private static final String GET_BUILDINGS = "http://eyesnpi.ru/API/APIMOBILE/api.php?action=getbuild&object_id=%s";
    private static final String GET_POINTS = "http://eyesnpi.ru/API/APIMOBILE/api.php?action=getpoint&building_id=%s";

    private static ArrayList<IMapDownloaded> mapDLListeners = new ArrayList<>();

    private static Thread mDownloadThread;

    private ServerAPI() {/*приватный конструктор*/}

    /**
     * Устанавливает статус пользвоателя offline/online на сервере.
     * Возвращает true, если успешно, и false в противном случае.
     */
    public static boolean setUserStatus(@NotNull String userLogin, @NotNull UserStatus status) {

        String query = String.format(SET_USER_STATUS_URL, status, userLogin);

        int i = 0;
        boolean success = false;
        while (i < 5 && !success) { // делаем 5 попыток
            try {
                success = QueryExecutor.executePost(query);
                i++;
                Thread.sleep(100);
            } catch (Exception ex) {
                ex.printStackTrace();
                return false;
            }
        }

        return success;
    }


    /**
     *Отправляет статистику посещения точки пользователем
     */
    public static boolean setPointStat(int idPoint, @NotNull String userLogin){
        String query = String.format(SET_POINT_INFO, idPoint, userLogin);

        int i = 0;
        boolean success = false;
        while (i < 5 && !success) { // делаем 5 попыток
            try {
                success = QueryExecutor.executePost(query);
                i++;
                Thread.sleep(100);
            } catch (Exception ex) {
                ex.printStackTrace();
                return false;
            }
        }

        Log.d(TAG, "setPointStat отправлена статистика для точки " + idPoint + " и логина " + userLogin);
        return success;
    }


    /**
     * Возвращает роль зарегистрированного пользователя
     */
    public static String getUserRole(@NotNull String userLogin) {
        String query = String.format(GET_USER_ROLE_URL, userLogin);
        String json = null;

        Log.d(TAG, "getUserRole, Запрос: " + query);

        int i = 0;
        boolean success = false;
        while (i < 5 && !success) { // делаем 5 попыток
            try {
                json = QueryExecutor.executeGetJSON(query);
                if (json != null)
                    success = true;
                i++;
                Thread.sleep(100);
            } catch (Exception ex) {
                ex.printStackTrace();
                return null;
            }
        }

        if (!success) {
            return null;
        }

        Log.d(TAG, "Ответ: " + json);

        try {
            return MyJSONParser.getUsrRole(json);
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }



    /**
     * Возвращает список всех доступных объектов
     */
    public static ArrayList<ISpinnerItem> getObjects(){
        String query = GET_OBJECTS;
        String json = null;

        Log.d(TAG, "getObjects, Запрос: " + query);

        int i = 0;
        boolean success = false;
        while (i < 5 && !success) { // делаем 5 попыток
            try {
                json = QueryExecutor.executeGetJSON(query);
                if (json != null)
                    success = true;
                i++;
                Thread.sleep(100);
            } catch (Exception ex) {
                ex.printStackTrace();
                return null;
            }
        }

        if (!success) {
            return null;
        }

        Log.d(TAG, "Ответ: " + json);

        try {
            return MyJSONParser.getObjects(json);
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Возвращает список всех доступных зданий для указанного объекта
     */
    public static ArrayList<ISpinnerItem> getBuildings(long objectId){
        String query = String.format(Locale.US, GET_BUILDINGS, objectId);
        String json = null;

        Log.d(TAG, "getBuildings, Запрос: " + query);

        int i = 0;
        boolean success = false;
        while (i < 5 && !success) { // делаем 5 попыток
            try {
                json = QueryExecutor.executeGetJSON(query);
                if (json != null)
                    success = true;
                i++;
                Thread.sleep(100);
            } catch (Exception ex) {
                ex.printStackTrace();
                return null;
            }
        }

        if (!success) {
            return null;
        }

        Log.d(TAG, "Ответ: " + json);

        try {
            return MyJSONParser.getBuildings(json);
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Возвращает список всех доступных точек для указанного здания (на всех картах)
     */
    public static ArrayList<ISpinnerItem> getPointsForBuilding(long buildingId){
        String query = String.format(Locale.US, GET_POINTS, buildingId);
        String json = null;

        Log.d(TAG, "getPointsForBuilding, Запрос: " + query);

        int i = 0;
        boolean success = false;
        while (i < 5 && !success) { // делаем 5 попыток
            try {
                json = QueryExecutor.executeGetJSON(query);
                if (json != null)
                    success = true;
                i++;
                Thread.sleep(100);
            } catch (Exception ex) {
                ex.printStackTrace();
                return null;
            }
        }

        if (!success) {
            return null;
        }

        Log.d(TAG, "Ответ: " + json);

        try {
            return MyJSONParser.getPointsForBuilding(json);
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }




    /**
     * Загружает и записывает в БД карту, ее точки и дуги. Этот метод выполняет в отдельном потоке
     */
    public static void downloadMap(final Context context, @NotNull final int map_id, final int which_map){

        final Handler handler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                Bundle bundle = msg.getData();
                boolean mes = bundle.getBoolean(HANDLER_RESULT);

                sendCallToDLListeners(mes, which_map);
            }
        };

        mDownloadThread = new Thread(new Runnable() {
            @Override
            public void run() {
                String query = String.format(DOWNLOAD_MAP, map_id);
                String json = null;

                Bundle bundle = new Bundle();
                Message message = handler.obtainMessage();


                if(mDownloadThread.isInterrupted()){
                    Log.d(TAG, "Thread is interrupted");
                    return; // завершаем тред безопасно, если юзер нажал на "завершить"
                }

                Log.d(TAG, "downloadMap, Запрос: " + query);

                int i = 0;
                boolean success = false;
                while (i < 5 && !success) { // делаем 5 попыток

                    if(mDownloadThread.isInterrupted()) return; // завершаем тред безопасно, если юзер нажал на "завершить"

                    try {
                        json = QueryExecutor.executeGetJSON(query);
                        if (json != null)
                            success = true;
                        i++;
                        Thread.sleep(100);
                    } catch (Exception ex) {
                        ex.printStackTrace();

                        bundle.putBoolean(HANDLER_RESULT, false);
                        message.setData(bundle);
                        handler.sendMessage(message);

                        return;
                    }
                }

                if (!success) {
                    bundle.putBoolean(HANDLER_RESULT, false);
                    message.setData(bundle);
                    handler.sendMessage(message);
                    return;
                }

                Log.d(TAG, "Ответ: " + json);

                if(mDownloadThread.isInterrupted()) return; // завершаем тред безопасно, если юзер нажал на "завершить"


                Map map;
                Point[] points;
                Edge[] edges;
                String mapImgUrl;

                try {
                    map = MyJSONParser.getDownloadMap(json);
                    points = MyJSONParser.getDownloadPoints(json);
                    edges = MyJSONParser.getDownloadEdges(json);
                    mapImgUrl = MyJSONParser.getImageUrl(json);

                } catch (JSONException e) {
                    e.printStackTrace();

                    bundle.putBoolean(HANDLER_RESULT, false);
                    message.setData(bundle);
                    handler.sendMessage(message);

                    return;
                }

                if(mDownloadThread.isInterrupted()) return; // завершаем тред безопасно, если юзер нажал на "завершить"

                // загружаем картинку из сети
                Bitmap mapImage = downloadMapImage(context, mapImgUrl);
                if(mapImage == null){
                    bundle.putBoolean(HANDLER_RESULT, false);
                    message.setData(bundle);
                    handler.sendMessage(message);
                    return;
                }

                //кладем здесь картинку на диск типа
                String imgPath = FileHelper.writeImage(context, mapImage);

                Log.d(TAG, "downloadMap: path = " + imgPath);

                if(mDownloadThread.isInterrupted()) return; // завершаем тред безопасно, если юзер нажал на "завершить"

                if(MyDatabaseProvider.getMap(context, map_id) == null){
                    //пишем в бд
                    map.setImagePath(imgPath);
                    MyDatabaseProvider.setMaps(context, new Map[]{map});
                    MyDatabaseProvider.setPoints(context, points);
                    MyDatabaseProvider.setEdges(context, edges);

                } else {
                    Log.d(TAG, "Такая карта уже есть.");
                }

                bundle.putBoolean(HANDLER_RESULT, true);
                message.setData(bundle);
                handler.sendMessage(message);


            }
        });
        mDownloadThread.start();
    }

    /**
     * Загружает и возвращает картинку - фон карты
     */
    private static Bitmap downloadMapImage(Context context, @NotNull String url){

        int i = 0;
        boolean success = false;
        Bitmap bitmap = null;
        while (i < 5 && !success) { // делаем 5 попыток
            try {
                bitmap = QueryExecutor.executeGetImage(context, url);
                if (bitmap != null)
                    success = true;
                i++;
                Thread.sleep(100);
            } catch (Exception ex) {
                ex.printStackTrace();
                return null;
            }
        }

        if(!success) {
            return null;
        }

        Log.d(TAG, "downloadMapImage(): image downloaded");
        return bitmap;
    }





    //добавляет листенера загрузки карты
    public static void setOnMapDLListener(IMapDownloaded listener){
        mapDLListeners.add(listener);
    }

    //удаляет листенера загрузки карты
    public static void deleteOnMapDLListener(IMapDownloaded listener){
        int index = mapDLListeners.indexOf(listener);
        if(index != -1){
            mapDLListeners.remove(index);
        }
    }

    //оповещает слушателей о том, что карта загружена
    private static void sendCallToDLListeners(boolean result, int which_map){
        for (IMapDownloaded listener : mapDLListeners){
            listener.mapDownloaded(result, which_map);
        }
    }

    public static Thread getDownloadThread(){
        return mDownloadThread;
    }

}