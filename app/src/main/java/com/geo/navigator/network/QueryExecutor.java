package com.geo.navigator.network;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by nikita on 13.06.17.
 *
 * Класс, исполняющий запросы
 */
public class QueryExecutor {

    /**
     * Выполняет Post-запрос; возвращает true, если ответ OK и
     * false в противном случае
     */
    public static boolean executePost(String urlString) throws IOException {
        URL url = new URL(urlString);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();

        if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
            connection.disconnect();
            return true;
        } else {
            connection.disconnect();
            return false;
        }
    }
}
