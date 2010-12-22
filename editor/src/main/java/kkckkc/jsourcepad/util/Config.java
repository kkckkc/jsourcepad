package kkckkc.jsourcepad.util;

import com.google.common.base.Objects;
import kkckkc.utils.Os;

import java.io.File;

public class Config {
    public static final int MODE_PRODUCTION = 1;
    public static final int MODE_DEVELOPMENT= 2;

    private static final String HTTP_PORT = "http.port";
    private static final String HTTP_LOCALHOST = "http.localhost";
    private static final String APPLICATION_FOLDER = "application.folder";

    private static boolean useCorrectMacLocations = true;

    public static int getMode() {
        if ("development".equals(System.getProperty("jsourcepad.mode"))) return MODE_DEVELOPMENT;
        return MODE_PRODUCTION;
    }

    public static int getHttpPort() {
        if (System.getProperty(HTTP_PORT) != null) {
            return Integer.parseInt(System.getProperty(HTTP_PORT));
        }
        return 8171;
    }

    public static File getApplicationFolder() {
        if (System.getProperty(APPLICATION_FOLDER) == null) {
            return new File(".");
        }
        return new File(System.getProperty(APPLICATION_FOLDER));
    }

    public static File getThemesFolder() {
        if (useCorrectMacLocations && Os.isMac()) {
            return new File(System.getProperty("user.home"), "Library/Application Support/JSourcePad/Themes");
        } else {
            return new File(new File(System.getProperty("user.home"), ".jsourcepad"), "Themes");
        }
    }

    public static File getSupportFolder() {
        return new File(getApplicationFolder(), "Shared/Support");
    }

    public static File getBundlesFolder() {
        if (useCorrectMacLocations && Os.isMac()) {
            return new File(System.getProperty("user.home"), "Library/Application Support/JSourcePad/Bundles");
        } else {
            return new File(new File(System.getProperty("user.home"), ".jsourcepad"), "Bundles");
        }
    }

    public static File getSettingsFolder() {
        if (useCorrectMacLocations && Os.isMac()) {
            return new File(System.getProperty("user.home"), "Library/Preferences/JSourcePad");
        } else {
            return new File(System.getProperty("user.home"), ".jsourcepad");
        }
    }

    public static File getCacheFolder() {
        if (useCorrectMacLocations && Os.isMac()) {
            return new File(System.getProperty("user.home"), "Library/Caches/JSourcePad");
        } else {
            return new File(System.getProperty("user.home"), ".jsourcepad");
        }
    }

    public static File getTempFolder() {
        if (useCorrectMacLocations && Os.isMac()) {
            return new File(System.getProperty("user.home"), "Library/Caches/JSourcePad");
        } else {
            return new File(System.getProperty("user.home"), ".jsourcepad/temp");
        }
    }

    public static File getLogFolder() {
        if (useCorrectMacLocations && Os.isMac()) {
            return new File(System.getProperty("user.home"), "Library/Logs/JSourcePad");
        } else {
            return new File(System.getProperty("user.home"), ".jsourcepad");
        }
    }

    public static String getLocalhost() {
        return Objects.firstNonNull(System.getProperty(HTTP_LOCALHOST), "localhost");
    }
}
