package sfri.mhmd;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.ProtocolException;
import java.net.URISyntaxException;
import java.net.URL;

import org.apache.catalina.LifecycleException;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import jakarta.servlet.ServletException;
import sfri.mhmd.rssrj.tomcat.TomcatBootstrap;

@Disabled
public class ServerBootstrapIntegrationTest {

    private static TomcatBootstrap serverHolder;

    @BeforeAll
    public static void setUp() throws ServletException, LifecycleException {
        try {
            serverHolder = new TomcatBootstrap();
        } catch (IOException | URISyntaxException e) {
            Assumptions.assumeTrue(false);
        }
    }

    @AfterAll
    public static void tearDown() throws LifecycleException {
        serverHolder.stopTomcat();
    }

    @ParameterizedTest
    @ValueSource(strings = { "http://localhost:8080/", "http://localhost:8080/subscriber" })
    public void testServing(String urlAddress) throws IOException {
        URL url = new URL(urlAddress);
        HttpURLConnection connection = prepareGetRequest(url);
        assertEquals(200, connection.getResponseCode());
    }

    private static HttpURLConnection prepareGetRequest(URL url) throws IOException, ProtocolException {
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        StringBuilder content = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                content.append(line);
            }
        } catch (Exception e) {
            fail();
        } finally {
            connection.disconnect();
        }
        return connection;
    }
}
