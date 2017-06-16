package com.geo.navigator.network;


import com.geo.navigator.data.UserStatus;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

/**
 * Created by nikita on 13.06.17.
 *
 * Этот класс содержит все доступные методы API сервера.
 */

public class ServerAPI {
    static final String USER_STATUS_URL = "http://eyesnpi.ru/API/api.php?action=setstatus&status=(&s)&login=($s)";

    /**
     * Устанавливает статус пользвоателя offline/online на сервере.
     * Возвращает true, если успешно, и false в противном случае.
     */
    public static boolean setUserStatus(@NotNull String userLogin, @NotNull UserStatus status){

        String query = String.format(USER_STATUS_URL, status, userLogin);
        try {
            return QueryExecutor.executePost(query);
        } catch (IOException ioe){
            return false;
        }
    }
}
