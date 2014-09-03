/**
 * 
 */
package ru.bank24.tests.commons;

import java.util.Arrays;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.util.StringUtils;

/**
 * Пример использования API для JSON - формата. Тот или иной формат ответа (JSON
 * или XML) определяется заголовками 'Accept' и 'Content-Type'.
 * 
 * 
 * @author Alexey Romanchuk
 * @created 31 янв. 2014 г.
 * 
 */

public class ApiJSON extends AbstractApi {

    /**
     * Конструктор сервиса
     * 
     * @param userName
     *            Имя пользователя
     * @param password
     *            Пароль
     * 
     * @param oauth
     *            true, если требуется получить OAuth2 токен. Если false то
     *            используем Basic Authorization
     */
    public ApiJSON(String userName, String password, boolean oauth) {

        super(userName, password, oauth);
    }

    /**
     * Шаблон запроса в виде xml.
     */
    private static final String JSON_QUERY =
            "{\"message_v1\":{\"@time\":\"%s\",\"@ext_id\":\"%s\",\"@type\":\"request\",\"data\":{\"@trn_code\":\"%s\" %s}}}";

    /**
     * {@inheritDoc}
     */
    @Override
    protected String getBody(String time, String guid, String trnCode,
            String data) {

        String d = StringUtils.isEmpty(data) ? "" : "," + data;
        return String.format(JSON_QUERY, time, guid, trnCode, d);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void prepareHeaders(HttpHeaders headers) {

        headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
        headers.setContentType(MediaType.APPLICATION_JSON);
    }
}
