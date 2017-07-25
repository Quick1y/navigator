package com.geo.navigator.Utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.Nullable;
import android.util.Log;

import com.geo.navigator.R;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by nikita on 13.06.17.
 *
 * Класс, исполняющий запросы
 */
public class QueryExecutor {
    private static final String TAG = "QueryExecutor";
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

    public static String executeGetJSON(String urlString) throws IOException{
        URL url = new URL(urlString);
        HttpURLConnection connection = (HttpURLConnection)url.openConnection();

        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            InputStream in = connection.getInputStream(); // тут залипаем при отсутствии сети

            if (connection.getResponseCode() != HttpURLConnection.HTTP_OK ){
                throw new IOException(connection.getResponseMessage() +
                        ": with " + urlString);
            }

            int bytesRead = 0;
            byte[] buffer = new byte[1024];
            while ((bytesRead = in.read(buffer)) > 0) {
                out.write(buffer, 0, bytesRead);
            }
            out.close();
            return new String(out.toByteArray());
        } finally {
            connection.disconnect();
        }
    }

    @Nullable
    public static Bitmap executeGetImage(Context context, String urlString) throws IOException{
        // тут идем в сеть и качаем картинку
        URL url_value = new URL(urlString);
        return BitmapFactory.decodeStream(url_value.openConnection().getInputStream());

    }
}
