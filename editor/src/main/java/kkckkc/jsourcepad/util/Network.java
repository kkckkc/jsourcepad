package kkckkc.jsourcepad.util;

import kkckkc.utils.Pair;

import java.io.IOException;
import java.net.*;
import java.util.Iterator;
import java.util.List;

public class Network {
    public static boolean checkConnectivity() {
        try {
            URL url = new URL("http://www.github.com");
            URLConnection connection = url.openConnection();
            connection.connect();

            if (! connection.getContentType().startsWith("text/html")) {
                return false;
            }

            return true;
        } catch (MalformedURLException e) {
            return false;
        } catch (IOException e) {
            return false;
        }
    }


    public static Pair<String, Integer> getProxy(String url) throws URISyntaxException {
        List l = ProxySelector.getDefault().select(new URI(url));
        for (Iterator iter = l.iterator(); iter.hasNext();) {
            java.net.Proxy proxy = (java.net.Proxy) iter.next();
            InetSocketAddress addr = (InetSocketAddress) proxy.address();
            if (addr == null) {
                return null;
            } else {
                return new Pair<String, Integer>(addr.getHostName(), addr.getPort());
            }
        }
        return null;
    }
}
