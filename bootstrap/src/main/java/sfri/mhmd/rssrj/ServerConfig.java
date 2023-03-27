package sfri.mhmd.rssrj;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.function.UnaryOperator;

public class ServerConfig {
    private static Properties properties;
    private static Properties defaultProp;

    private ServerConfig() {
        super();
    }

    public static <K, V> V get(final K key) {
        if (properties == null) {
            properties = load("server.properties");
        }
        if (properties.containsKey(key)) {
            return typeCheckedCall(properties::get, key);
        }
        return typeCheckedCall(defaultProp::get, key);
    }

    @SuppressWarnings("unchecked")
    protected static <V, K> V typeCheckedCall(final UnaryOperator<Object> getter, final K key) {
        if (key instanceof K) {
            final var val = ((V) getter.apply(key));
            if (val instanceof V) {
                return val;
            }
        }
        throw new RuntimeException();
    }

    protected static Properties load(final String propertiesFileName) {
        Properties properties = new Properties();
        try (InputStream inputStream = Class.forName(ServerConfig.class.getName())
                .getClassLoader()
                .getResourceAsStream(propertiesFileName)) {
            properties.load(inputStream);
        } catch (IOException | ClassNotFoundException e) {
            properties = defaultProp;
        }
        return properties;
    }
}
