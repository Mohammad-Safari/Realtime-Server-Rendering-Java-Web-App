package sfri.mhmd.rssrj;

import java.io.IOException;
import java.net.URISyntaxException;

import org.apache.catalina.LifecycleException;

import sfri.mhmd.rssrj.tomcat.TomcatBootstrap;

public class Bootstrap {

    public static void main(final String[] args) throws LifecycleException, IOException, URISyntaxException {
        new TomcatBootstrap();
    }

}
