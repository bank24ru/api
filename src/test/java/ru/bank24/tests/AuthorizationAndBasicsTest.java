/**
 * 
 */
package ru.bank24.tests;

import java.util.HashMap;

import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ru.bank24.tests.commons.ApiJSON;
import ru.bank24.tests.commons.ApiXML;
import ru.bank24.tests.commons.Utils;
import ru.bank24.tests.commons.xml.ApiResult;

/**
 * Простые тесты для работы с API : авторизация, простые примеры, oauth
 * 
 * 
 * @author Alexey Romanchuk
 * @created 31 Jan 2014 г.
 * 
 */

public class AuthorizationAndBasicsTest extends Utils {

    /**
     * Логгер
     */
    private static final Logger logger = LoggerFactory
            .getLogger(AuthorizationAndBasicsTest.class);

    /**
     * Use case: успешная авторизация и пинг (XML)
     */
    @Test
    public void testXML() {

        ApiXML api = new ApiXML("demo", "demo", false); // тестовые логи/пароль

        ApiResult r = api.queryXML("0000", "");

        Assert.assertEquals("processed", r.getState());

        logger.debug("DATA: {}", r.getData());
        Assert.assertEquals("ok", r.getData().get("ping"));
    }

    /**
     * Use case: успешная авторизация и пинг (JSON)
     */
    @Test
    public void testJSON() {

        ApiJSON api = new ApiJSON("demo", "demo", false); // тестовые
                                                          // логи/пароль

        String r = api.post("0000", "");

        logger.debug("DATA: {}", r);
        Assert.assertTrue(r.contains("\"$\":\"ok\""));
    }

    /**
     * Use case: успешная авторизация и пинг (JSON) через GET-запрос
     */
    @Test
    public void testJSONGet() {

        ApiJSON api = new ApiJSON("demo", "demo", false);

        String r = api.get("0000", new HashMap<String, Object>());

        logger.debug("DATA: {}", r);
        Assert.assertTrue(r.contains("\"$\":\"ok\""));
    }

    /**
     * Use case: успешная авторизация и пинг (JSON), используем OAuth
     */
    @Test
    public void testJSONOAuth() {

        ApiJSON api = new ApiJSON("demo", "demo", true); // true = OAuth2

        String r = api.post("0000", "");

        logger.debug("DATA: {}", r);
        Assert.assertTrue(r.contains("\"$\":\"ok\""));
    }

}
