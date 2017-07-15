package com.geo.navigator.network;


import android.util.Log;

import com.geo.navigator.data.UserStatus;
import com.geo.navigator.utils.MyJSONParser;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

/**
 * Created by nikita on 13.06.17.
 *
 * Этот класс содержит все доступные методы API сервера.
 */

public class ServerAPI {
    private static final String TAG = "ServerAPI";

    private static final String SET_USER_STATUS_URL = "http://eyesnpi.ru/API/APIMOBILE/api.php?action=setstatus&status=%s&login=%s";
    private static final String GET_USER_ROLE_URL = "http://eyesnpi.ru/API/APIMOBILE/api.php?action=getrol&login=%s";

    private ServerAPI(){}

    /**
     * Устанавливает статус пользвоателя offline/online на сервере.
     * Возвращает true, если успешно, и false в противном случае.
     */
    public static boolean setUserStatus(@NotNull String userLogin, @NotNull UserStatus status){

        String query = String.format(SET_USER_STATUS_URL, status, userLogin);
        try {
            return QueryExecutor.executePost(query);
        } catch (IOException ioe){
            ioe.printStackTrace();
            return false;
        }
    }


    public static String getUserRole(@NotNull String userLogin){
        String query = String.format(GET_USER_ROLE_URL, userLogin);
        String json;

        Log.d(TAG, "Запрос: " + query);
        try {
            json = QueryExecutor.executeGetJSON(query);
        } catch (IOException ioe){
            ioe.printStackTrace();
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


}
