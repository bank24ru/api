/**
 * 
 */
package ru.bank24.tests;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ru.bank24.tests.commons.ApiJSON;
import ru.bank24.tests.commons.Utils;

/**
 * Пример использования API Банка24.Ру для создания нового заказа оплату для
 * интернет-эквайринга. Система регистрирует заказ, присваивая ему уникальный
 * номер (поле OrderNumber) и возвращает список полей, которые нужно вставить в
 * форму оплаты. В дальшейшем submit этой формы приведет к перехода на сайт
 * системы Яндекс.Деньги, где собственно и происходит оплата.
 * 
 * <p>
 * Среди возвращаемых данных есть поле action, которое и содержит url платежной
 * системы Яндекс.Деньги и оно должно быть вставлено в соответствующий атрибут
 * элемента form.
 * 
 * 
 * @author Alexey Romanchuk
 * @created 31 Jan 2014 г.
 * 
 */

public class InternetAcquiringOrderTest extends Utils {

    /**
     * Логгер
     */
    private static final Logger logger = LoggerFactory
            .getLogger(InternetAcquiringOrderTest.class);

    /**
     * Сервис для выполнения запросов к банку
     */
    private ApiJSON api;

    /**
     * Инициализация сервисов перед выполнением тестов
     */
    @Before
    public void setUp() {

        api = new ApiJSON("demo", "demo", false); // тестовые логи/пароль
    }

    /**
     * Создание тестовой заявки на оплату
     */
    @Test
    public void test() {

        String r = api.post("AQ0100", ",\"sum\":\"1\",\"demo\":\"true\"");

        Assert.assertTrue(r.contains("processed"));

        /*
         * Ниже выводим все полученные в ответе поля, которые и нужно вставить в
         * форму (<form>). Среди них:
         * 
         * 1. Поле action из ответа пишем в атрибут action формы
         * 
         * 
         * 2. Поле OrderNumber пишем в hidden input формы, также сохраняем его у
         * себя, поскольку по нему будет полняться вся идентификация заказа.
         * 
         * 
         * 3. Остальные поля также сохраняем в hidden inpur'ах внутри формы
         * 
         * 
         * 4. Поле с суммой, конечно же, может быть простым input'ом.
         */
        logger.info("Form fields: {}", r);
    }
}
