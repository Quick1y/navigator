package com.geo.navigator.Utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;

/**
 * Created by nikita on 20.07.17.
 * <p>
 * ВСЕ операции здесь проводятся с директорией Picture в ExternalStorage
 */

public class FileHelper {
    private static final String TAG = "FileHelper";

    private static final String DIR = "/NavigatorNPI/maps/";
    private static final String DIR_DELETE = "/NavigatorNPI";

    private FileHelper() {
    }

    //записывает картинку на диск с сгенерированным именем
    public static String writeImage(Context context, Bitmap bitmap) {

        File path = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES).toString() + DIR); // путь до папки picture + DIR


        String filename = "img_" + System.nanoTime() + ".jpg";
        OutputStream outputStream;

        File file = new File(path, filename);

        try {

            if (path.mkdirs()) {
                File nomediaFile = new File(path, ".nomedia"); // если папка только создана,
                nomediaFile.createNewFile();                   // то создаем в ней .nomedia
            }

            outputStream = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream); //сохраняем пикчу здесь

            outputStream.flush();
            outputStream.close();

        } catch (Exception e) {
            e.printStackTrace();
        }

        return filename;
    }

    //записывает картинку на диск с заданным именем
    public static String writeImage(Context context, Bitmap bitmap, String filename) {

        File path = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES).toString() + DIR); // путь до папки picture + DIR

        filename = filename + ".jpg";

        OutputStream outputStream;
        File file = new File(path, filename);

        try {
            if (path.mkdirs()) {
                File nomediaFile = new File(path, ".nomedia"); // если папка только создана,
                nomediaFile.createNewFile();                   // то создаем в ней .nomedia
            }

            outputStream = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream); //сохраняем пикчу здесь

            outputStream.flush();
            outputStream.close();

        } catch (Exception e) {
            e.printStackTrace();
        }

        return filename;
    }

    //считывает указанную картинку
    public static Bitmap readImage(Context context, String name) {
        File path = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES).toString() + DIR); // путь до папки picture + DIR

        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = false;
        options.inPreferredConfig = Bitmap.Config.RGB_565;        // Используем конфигурацию без прозрачности
        return BitmapFactory.decodeFile(path + "/" + name, options);
    }

    //удаляет все файлы приложения из директории Picture. Передавай null, если хочешь удалить все
    public static void clearImageCache(String uri) {

        if(uri == null){
            uri = Environment.getExternalStoragePublicDirectory(
                    Environment.DIRECTORY_PICTURES).toString() + DIR_DELETE;
        }

        File currentFile = new File(uri);
        File files[] = currentFile.listFiles();

        if (files == null){
            return;
        }

        for (int i = 0; i < files.length; i++) {
            if (files[i].isDirectory()) { //если это папка, то сначала надо удалить файлы в ней
                clearImageCache(files[i].toString()); //удаляет вложенные файлы/папки
            }
            files[i].delete(); //удаляет файлы из указанной папки
        }
        currentFile.delete(); //удаляет саму папку
    }
}
