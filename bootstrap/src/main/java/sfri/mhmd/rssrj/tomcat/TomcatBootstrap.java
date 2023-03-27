package sfri.mhmd.rssrj.tomcat;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import org.apache.catalina.Context;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.connector.Connector;
import org.apache.catalina.startup.Tomcat;
import org.apache.catalina.webresources.DirResourceSet;
import org.apache.catalina.webresources.StandardRoot;
import org.apache.coyote.ProtocolHandler;
import org.apache.coyote.http11.Http11NioProtocol;

public class TomcatBootstrap {
    final Tomcat tomcat;
    final Connector connector;
    final ProtocolHandler protocolHandler;
    final Context context;

    public TomcatBootstrap() throws IOException, LifecycleException, URISyntaxException {
        tomcat = prepareTomcat();
        connector = prepareConnector(tomcat);
        protocolHandler = prepareProtocolHandler(connector);
        context = prepareServletContext(tomcat);
        setClassesResourceSet(context);
        startAndAwaitTomcat(tomcat);
    }

    public void stopTomcat() throws LifecycleException {
        tomcat.stop();
    }

    private static Tomcat prepareTomcat() throws IOException {
        final var tomcat = new Tomcat();
        tomcat.setBaseDir(Files.createTempDirectory("tomcat-temp-base-dir").toString());
        return tomcat;
    }

    private static Connector prepareConnector(final Tomcat tomcat) {
        final var connector = new Connector("org.apache.coyote.http11.Http11NioProtocol");
        var port = System.getenv("PORT");
        if (port == null || port.isEmpty()) {
            // todo get from appconfig
            port = "8080";
        }
        connector.setPort(Integer.valueOf(port));
        tomcat.setConnector(connector);
        return connector;
    }

    private static Http11NioProtocol prepareProtocolHandler(final Connector connector) {
        final var protocolHandler = (Http11NioProtocol) connector.getProtocolHandler();
        protocolHandler.getServer();
        return protocolHandler;
    }

    private static Context prepareServletContext(final Tomcat tomcat) throws URISyntaxException {
        // todo get from appconfig
        final var contextPath = "";
        // todo location resolution according to file seems not ok
        final var webResource = TomcatBootstrap.class.getClassLoader().getResource("WEB-INF");
        final var docBase = Paths.get(webResource.toURI()).getParent().toString();
        return tomcat.addWebapp(contextPath, docBase);
    }

    /**
     * context  requires  additional  webResources
     * for loading  servlets, filters, etc classes
     * due to jar structure of web module as a dep
     */
    private static void setClassesResourceSet(final Context context) {
        final var resourceRoot = new StandardRoot(context);
        final var basePath = Paths.get(context.getDocBase()).toString();
        final var resourceSet = new DirResourceSet(resourceRoot, "/WEB-INF/classes", basePath, "/");
        resourceRoot.addPreResources(resourceSet);
        context.setResources(resourceRoot);

    }

    private static void startAndAwaitTomcat(final Tomcat tomcat) throws LifecycleException {
        tomcat.start();
        tomcat.getServer().await();
    }
}
