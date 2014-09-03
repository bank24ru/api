/**
 * 
 */
package ru.bank24.tests.commons;

import java.util.Map;

import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;

/**
 * Интерфейс для доступа к http - методам
 * 
 * @author Alexey Romanchuk
 * @created 03 сент. 2014 г.
 * 
 */

public interface HttpTool {

    /**
     * POST - запрос.
     * 
     * @param path
     *            Относительный путь
     * @param headers
     *            Дополнительные заголовки
     * @param body
     *            Тело запроса
     * @param accept
     *            Тип запрашиваемого содержимого
     * @return Ответ
     */
    ResponseEntity<String> post(String path, HttpHeaders headers, String body);

    /**
     * GET - запрос
     * 
     * @param path
     *            Относительный путь
     * @param headers
     *            Дополнительные заголовки
     * @param params
     *            Набор параметров
     * @param accept
     *            Тип запрашиваемого сождержимого
     * @return Ответ
     */
    ResponseEntity<String> get(String path, final HttpHeaders headers,
            Map<String, ?> params);

    /**
     * Получение полного пути до ресурса из относительного
     * 
     * @param path
     *            Относительный ресурс
     * @return Полный ресурс
     */
    String getUrl(String path);
}
