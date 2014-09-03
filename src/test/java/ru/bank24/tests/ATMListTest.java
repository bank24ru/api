/**
 * 
 */
package ru.bank24.tests;

import java.util.HashMap;
import java.util.Map;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ru.bank24.tests.commons.ApiJSON;
import ru.bank24.tests.commons.Utils;

/**
 * Функции для работы со списками банкоматов Банка24 и ОРС.
 * 
 * 
 * @author Alexey Romanchuk
 * @created 03 сент. 2014 г.
 * 
 */

public class ATMListTest extends Utils {

    /**
     * Логгер
     */
    private static final Logger logger = LoggerFactory
            .getLogger(ATMListTest.class);

    /**
     * Сервис для выполнения запросов к банку
     */
    private ApiJSON api;

    /**
     * Инициализация сервисов перед выполнением тестов
     */
    @Before
    public void setUp() {

        api = new ApiJSON(null, null, false); // Не надо авторизации
    }

    /**
     * Use case: получение списка банкоматов по коду КЛАДР региона/города
     */
    @Test
    public void test() {

        Map<String, Object> params = new HashMap<>();
        params.put("city_code", "7300000100000");

        String r = api.get("Q0152", params);
        Assert.assertTrue(r.contains("processed"));

        /*
         * Далее идет список банкоматов с какими-то данными
         */
        logger.info("ATM List: {}", r);
    }
}
