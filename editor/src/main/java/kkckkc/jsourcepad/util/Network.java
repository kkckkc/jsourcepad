package kkckkc.jsourcepad.util;

import kkckkc.utils.Pair;

import java.io.IOException;
import java.net.*;
import java.util.List;

public class Network {
    public static boolean checkConnectivity() {
        try {
            URL url = new URL("http://www.github.com");
            URLConnection connection = url.openConnection();
            connection.connect();

            return connection.getContentType().startsWith("text/html");

        } catch (MalformedURLException e) {
            return false;
        } catch (IOException e) {
            return false;
        }
    }


    public static Pair<String, Integer> getProxy(String url) throws URISyntaxException {
        List<Proxy> proxies = ProxySelector.getDefault().select(new URI(url));
        if (proxies.isEmpty()) return null;

        Proxy proxy = proxies.get(0);
        InetSocketAddress addr = (InetSocketAddress) proxy.address();
        if (addr == null) {
            return null;
        } else {
            return new Pair<String, Integer>(addr.getHostName(), addr.getPort());
        }
    }
}
