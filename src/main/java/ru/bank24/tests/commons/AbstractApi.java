/**
 * 
 */
package ru.bank24.tests.commons;

import java.util.Map;
import java.util.UUID;

import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.codec.Base64;
import org.springframework.security.oauth2.client.OAuth2RestTemplate;
import org.springframework.security.oauth2.client.token.grant.password.ResourceOwnerPasswordResourceDetails;
import org.springframework.util.Assert;
import org.springframework.web.client.RestTemplate;

/**
 * Сервис, формирующий запросы в банк в соответствии со спецификацией API
 * (http://wiki.bank24.ru/wiki/SaaS-Bank ).
 * 
 * 
 * @author Alexey Romanchuk
 * @created 31 янв. 2014 г.
 * 
 */

public abstract class AbstractApi extends Utils {

    /**
     * Сервис для http-транспорта
     */
    private final HttpTool http;

    /**
     * Имя пользователя
     */
    private final String userName;

    /**
     * Пароль
     */
    private final String password;

    /**
     * true, если используется OAuth2 токен
     */
    private final boolean oauth;

    /**
     * Конструктор сервиса
     * 
     * @param userName
     *            Имя пользователя
     * @param password
     *            Пароль
     * 
     * @param oauth
     *            true, если требуется получить OAuth2 токен. Если false, то
     *            используем Basic Authorization
     */
    public AbstractApi(String userName, String password, boolean oauth) {

        this.userName = userName;
        this.password = password;
        this.oauth = oauth;

        this.http = new HttpToolImpl("ib.bank24.ru", 443, "https");

        RestTemplate template = new RestTemplate();
        if (oauth) {

            Assert.notNull(userName);
            Assert.notNull(password);

            template = new OAuth2RestTemplate(createOAuthData());
        }

        ((HttpToolImpl) http).setRestTemplate(template);
    }

    /**
     * Создание данных для OAUTH2 - авторизации. В примерах используем
     * grant_type = password, чтобы токен получать сразу в одном запросе.
     * 
     * 
     * @return Объект с данными, достаточными для получения токена
     */
    private ResourceOwnerPasswordResourceDetails createOAuthData() {

        ResourceOwnerPasswordResourceDetails r =
                new ResourceOwnerPasswordResourceDetails();

        r.setAccessTokenUri(http.getUrl("/auth/oauth/token"));

        r.setClientId("iphoneapp"); // Тестовый clientId
        r.setClientSecret("secret"); // Тестовый secret

        r.setId("ws"); // Это ресурс так называется

        r.setUsername(userName);
        r.setPassword(password);

        return r;
    }

    /**
     * Создание заголовка с данными Basic - авторизации. Если логин/пароль
     * изначально отсутствовали, то предполагаем, что сервис доступен и без
     * авторизации (например, некоторые справочные сервисы).
     * 
     * @param headers
     *            Набор заголовков
     */
    protected void addBasicAuth(HttpHeaders headers) {

        if (userName != null && password != null) {

            String s = utf8(Base64.encode(utf8(userName + ":" + password)));
            s = "Basic " + s;

            headers.add("Authorization", s);
        }
    }

    /**
     * Выполнение API - функции как POST-запрос.
     * 
     * @param trnCode
     *            Код транзакции
     * @param data
     *            Данные (содердимое тэга data в виде подстроки xml, например,
     *            "<sum>20.33<sum>" или json, например, ',"sum": "20.33"' ).
     *            Чтобы сильно не заморачиваться в случае JSON data должна
     *            начинаться с запятой ",".
     * @return Ответ
     */
    public String post(String trnCode, String data) {

        String time = formatISO(now());
        String guid = UUID.randomUUID().toString();

        String body = getBody(time, guid, trnCode, data);

        HttpHeaders headers = new HttpHeaders();
        prepareHeaders(headers);

        if (!oauth) {
            addBasicAuth(headers);
        }

        ResponseEntity<String> r =
                http.post("/ws/do/" + trnCode, headers, body.toString());
        return r.getBody();
    }

    /**
     * Получение данных через GET - вариант
     * 
     * @param trnCode
     *            Код транзакции
     * @param params
     *            Набор параметров запроса
     * @return Ответ
     */
    public String get(String trnCode, Map<String, ?> params) {

        HttpHeaders headers = new HttpHeaders();
        prepareHeaders(headers);

        if (!oauth) {
            addBasicAuth(headers);
        }

        ResponseEntity<String> r =
                http.get("/ws/message_v1/" + trnCode, headers, params);
        return r.getBody();
    }

    /**
     * Генерация "тела" запроса (для POST)
     * 
     * @param time
     *            Время в виде ISO строки
     * @param guid
     *            Уникальный идентификатор
     * @param trnCode
     *            Код транзакции
     * @param data
     *            Данные транзакции
     * @return Готовое тело запроса
     */
    protected abstract String getBody(String time, String guid,
            String trnCode, String data);

    /**
     * Дополнительная работы с заголовками
     * 
     * @param headers
     *            Набор заголовков
     */
    protected void prepareHeaders(HttpHeaders headers) {

        /*
         * Empty
         */
    }
}
