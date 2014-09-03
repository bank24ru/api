/**
 * 
 */
package ru.bank24.tests.commons;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Arrays;

import javax.xml.parsers.SAXParserFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

import ru.bank24.tests.commons.xml.ApiResult;
import ru.bank24.tests.commons.xml.ApiXmlParser;

/**
 * Пример использования API для XML - формата. Тот или иной формат ответа (JSON
 * или XML) определяется заголовками 'Accept' и 'Content-Type'.
 * 
 * 
 * @author Alexey Romanchuk
 * @created 03 сент. 2014 г.
 * 
 */

public class ApiXML extends AbstractApi {

    /**
     * Логгер
     */
    private static final Logger logger = LoggerFactory.getLogger(ApiXML.class);

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
    public ApiXML(String userName, String password, boolean oauth) {

        super(userName, password, oauth);
    }

    /**
     * Шаблон запроса в виде xml.
     */
    private static final String XML_QUERY =
            "<?xml version=\"1.0\" encoding=\"UTF-8\" ?>"
                    + "<message_v1 xmlns=\"http://www.anr.ru/types\""
                    + " type=\"request\" time=\"%s\"" + " ext_id = \"%s\" >"
                    + "<data trn_code=\"%s\">%s</data>"
                    + "<terminal id=\"testTerminal\">"
                    + "<name>Yekaterinburg</name>" + "</terminal>"
                    + "</message_v1>";

    /**
     * {@inheritDoc}
     */
    @Override
    protected String getBody(String time, String guid, String trnCode,
            String data) {

        return String.format(XML_QUERY, time, guid, trnCode, data);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void prepareHeaders(HttpHeaders headers) {

        headers.setAccept(Arrays.asList(MediaType.APPLICATION_XML));
        headers.setContentType(MediaType.APPLICATION_XML);
    }

    /**
     * Выполнение api - запроса с простым парсингом результата, чтобы удобнее
     * было работать с полученными ответами.
     * 
     * @param trnCode
     *            Код транзакции
     * @param data
     *            Данные транзакции
     * @return Результат выполнения транзакции
     */
    public ApiResult queryXML(String trnCode, String data) {

        String xml = post(trnCode, data);

        SAXParserFactory f = SAXParserFactory.newInstance();
        InputStream stream = new ByteArrayInputStream(utf8(xml));

        /*
         * TODO: парсер не умеет пока работать с атрибутами элементов в ответах
         */
        ApiXmlParser p = new ApiXmlParser();

        try {
            f.newSAXParser().parse(stream, p);

            logger.debug("state: {} / {}, maps: {}", p.getState(),
                    p.getStateInfo(), p.getMap());
            return new ApiResult(p.getState(), p.getStateInfo(), p.getMap());
        } catch (Exception ex) {
            throw new ApiException("Api exception", ex);
        }
    }
}
