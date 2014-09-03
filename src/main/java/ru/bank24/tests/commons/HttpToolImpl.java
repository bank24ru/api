package ru.bank24.tests.commons;

import java.io.IOException;
import java.net.URI;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.web.client.DefaultResponseErrorHandler;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

/**
 * Реализация http - клиента для выполнения тестов.
 * 
 * @author Alexey Romanchuk
 * @created 03 сент. 2014 г.
 * 
 */
public class HttpToolImpl extends Utils implements HttpTool {

    /**
     * Logger
     */
    private static Logger logger = LoggerFactory.getLogger(HttpToolImpl.class);

    /**
     * Порт
     */
    private int port;

    /**
     * Хост
     */
    private String hostName;

    /**
     * Схема (http, https)
     */
    private final String schema;

    /**
     * Базовая часть пути, относительно которой будет формироваться запрос
     * (точка монтирования сервиса)
     */
    private final String basePath = "";

    /**
     * Ссылка на клиента для REST
     */
    private RestTemplate client;

    /**
     * Базовые заголовки, которые добавляются к каждому запросу.<br/>
     * Например заголовки авторизации.
     */
    private final Map<String, String> baseHeaders = new HashMap<>();

    /**
     * Конструктор
     * 
     * @param host
     *            Хост
     * @param port
     *            Порт
     * @param schema
     *            Схема http, https
     */
    public HttpToolImpl(String host, int port, String schema) {

        super();
        setPort(port);
        setHostName(host);
        this.schema = schema;
    }

    /**
     * Конструктор со схемой http по умолчанию
     * 
     * @param host
     *            Хост
     * @param port
     *            Порт
     */
    public HttpToolImpl(String host, int port) {

        this(host, port, "https");
    }

    /**
     * @param port
     *            the port to set
     */
    public void setPort(int port) {

        this.port = port;
    }

    /**
     * @param hostName
     *            the hostName to set
     */
    public void setHostName(String hostName) {

        this.hostName = hostName;
    }

    /**
     * @param template
     */
    public void setRestTemplate(RestTemplate template) {

        this.client = applySettings(template);
    }

    // //////////////////////////// methods //////////////////////////////////

    /**
     * @return Рассчитанный baseUrl
     */
    private String getBaseUrl() {

        StringBuilder sb =
                new StringBuilder(schema).append("://").append(hostName);
        if (!(port == 80 || port == 443)) {
            sb.append(":").append(port);
        }
        sb.append(basePath);

        if (sb.charAt(sb.length() - 1) == '/') {
            sb.deleteCharAt(sb.length() - 1);
        }

        return sb.toString();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getUrl(String path) {

        String p = path;
        if (p.startsWith("http")) {
            return p;
        }
        if (!p.startsWith("/")) {
            p = "/" + p;
        }
        return getBaseUrl() + p;
    }

    /**
     * Формирует базовые заголовки
     * 
     * @param additionalHeaders
     *            дополнительные заголовки, может быть null
     * @return базовые заголовки
     */
    protected HttpHeaders buildHeaders(HttpHeaders additionalHeaders) {

        HttpHeaders h = new HttpHeaders();
        for (Entry<String, String> bh : baseHeaders.entrySet()) {
            h.add(bh.getKey(), bh.getValue());
        }

        if (additionalHeaders != null) {
            h.putAll(additionalHeaders);
        }

        return h;
    }

    /**
     * Логгирование http-ответа
     * 
     * @param r
     *            http-Ответ
     */
    private void logResponse(ResponseEntity<?> r) {

        logger.info("\nhttp response: {}, content: {}, headers: {}",
                r.getStatusCode(), r.getBody(), r.getHeaders());
    }

    /**
     * Логгирование запроса
     * 
     * @param url
     *            Адрес ресурса
     * @param body
     *            Тело запроса
     */
    private void logRequest(String url, String body) {

        logger.info("\nhttp request {}, content: {}", url, body);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ResponseEntity<String> post(String path, HttpHeaders headers,
            String body) {

        HttpHeaders actualHeaders = buildHeaders(headers);
        logRequest(path, body);

        ResponseEntity<String> r =
                client.exchange(getUrl(path), HttpMethod.POST,
                        new HttpEntity<String>(body, actualHeaders),
                        String.class);
        logResponse(r);
        return r;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ResponseEntity<String> get(String path, final HttpHeaders headers,
            Map<String, ?> params) {

        HttpHeaders actualHeaders = buildHeaders(headers);

        logger.debug("HTTP GET {} with headers: {}", path + "?" + params,
                actualHeaders.toSingleValueMap());

        UriComponentsBuilder b =
                UriComponentsBuilder.fromUriString(getUrl(path));

        if (params != null) {
            for (Entry<String, ?> e : params.entrySet()) {
                b = b.queryParam(e.getKey(), e.getValue());
            }
        }
        URI url = b.build().toUri();

        logRequest(url.toString(), (params != null) ? params.toString() : "");
        ResponseEntity<String> r =
                client.exchange(url.toString(), HttpMethod.GET,
                        new HttpEntity<Void>((Void) null, actualHeaders),
                        String.class);
        logResponse(r);
        return r;
    }

    /**
     * Выставляем настройки {@link RestTemplate}, нужные нам
     * 
     * @param t
     *            Исходный шаблон
     * @return Исходный шаблон. Сделано для удобства
     */
    public RestTemplate applySettings(RestTemplate t) {

        List<HttpMessageConverter<?>> convertors = new ArrayList<>();
        for (HttpMessageConverter<?> cv : t.getMessageConverters()) {

            if (cv instanceof StringHttpMessageConverter) {
                convertors.add(new StringHttpMessageConverter(Charset
                        .forName("UTF-8")));
            } else {
                convertors.add(cv);
            }

        }

        t.setMessageConverters(convertors);
        /*
         * Apache Http Client все очень подробно пищет в лог
         */
        t.setRequestFactory(new HttpComponentsClientHttpRequestFactory());

        t.setErrorHandler(new DefaultResponseErrorHandler() {

            /**
             * {@inheritDoc}
             */
            @Override
            public void handleError(ClientHttpResponse r) throws IOException {

                List<String> lines = IOUtils.readLines(r.getBody());

                logger.error("Error: {} / {}, body: {}, headers: {}",
                        r.getStatusCode(), r.getStatusText(), lines,
                        r.getHeaders());

                super.handleError(r);
            }
        });

        return t;
    }

    /**
     * @param baseHeaders
     *            the baseHeaders to set
     */
    public void setBaseHeaders(Map<String, String> baseHeaders) {

        this.baseHeaders.clear();
        this.baseHeaders.putAll(baseHeaders);
    }
}
