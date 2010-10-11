package kkckkc.jsourcepad.util;

public class Config {
    private static final String HTTP_PORT = "http.port";

    public static int getHttpPort() {
        if (System.getProperty(HTTP_PORT) != null) {
            return Integer.parseInt(System.getProperty(HTTP_PORT));
        }
        return 8171;
    }
}
