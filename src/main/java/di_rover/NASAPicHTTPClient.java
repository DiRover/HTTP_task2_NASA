package di_rover;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.config.RequestConfig;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManager;
import org.apache.hc.core5.http.ContentType;
import org.apache.hc.core5.http.HttpEntity;
import org.apache.hc.core5.http.HttpHeaders;
import org.apache.hc.core5.util.Timeout;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class NASAPicHTTPClient {
    private static final ObjectMapper mapper = new ObjectMapper();
    private final String picUrl;

    public NASAPicHTTPClient(String picUrl) {
        this.picUrl = picUrl;
    }

    public void start() {
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
                    .setUserAgent("Get NASA Pic")
                    .setDefaultRequestConfig(config)
                    .build()) {

                HttpGet request = new HttpGet(this.picUrl);
                request.setHeader(HttpHeaders.ACCEPT, ContentType.APPLICATION_JSON.getMimeType());

                try (CloseableHttpResponse response = httpClient.execute(request)) {

                    String[] arr = this.picUrl.split("/");
                    String fileName = arr[arr.length - 1];

                    System.out.println(fileName);

                    String filePath = "./pic/";

                    File dir = new File(filePath);
                    dir.mkdir();

                    try (FileOutputStream fos = new FileOutputStream(filePath + fileName)) {

                        HttpEntity entity = response.getEntity();

                        entity.writeTo(fos);


                    } catch (IOException ex) {
                        System.out.println(ex.getMessage());
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

    }
}
