package di_rover;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.config.RequestConfig;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManager;
import org.apache.hc.core5.http.ContentType;
import org.apache.hc.core5.http.HttpHeaders;
import org.apache.hc.core5.util.Timeout;

import java.io.IOException;

public class HTTPClient {
    private static final String NASA_REMOTE_URI = "https://api.nasa.gov/planetary/apod?api_key=VeGwhZ4gAJsNgdgtfbFCFJhS6jg2UCALkr3TUdf2";
    private static final ObjectMapper mapper = new ObjectMapper();

    public static void start() throws IOException {
        RequestConfig config = RequestConfig.custom()
                .setConnectionRequestTimeout(Timeout.ofSeconds(5))
                .setResponseTimeout(Timeout.ofSeconds(30))
                .setRedirectsEnabled(false)
                .build();

        try (PoolingHttpClientConnectionManager poolingManager = new PoolingHttpClientConnectionManager()
        ) {
            poolingManager.setMaxTotal(1);
            poolingManager.setDefaultMaxPerRoute(1);

            try (CloseableHttpClient httpClient = HttpClients.custom()
                    .setConnectionManager(poolingManager)
                    .setUserAgent("My NASA Programm")
                    .setDefaultRequestConfig(config)
                    .build()) {

                HttpGet request = new HttpGet(NASA_REMOTE_URI);
                request.setHeader(HttpHeaders.ACCEPT, ContentType.APPLICATION_JSON.getMimeType());

                try (CloseableHttpResponse response = httpClient.execute(request)) {

                    NASAObject nasaObject = mapper.readValue(
                            response.getEntity().getContent(),
                            new TypeReference<NASAObject>() {
                            }
                    );

                    NASAPicHTTPClient nasaPicHTTPClient = new NASAPicHTTPClient(nasaObject.url);
                    nasaPicHTTPClient.start();

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

}
