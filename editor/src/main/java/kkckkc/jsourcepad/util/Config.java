package kkckkc.jsourcepad.util;

import kkckkc.utils.Os;

import java.io.File;

public class Config {
    private static final String HTTP_PORT = "http.port";
    private static final String APPLICATION_FOLDER = "application.folder";

    private static boolean useCorrectMacLocations = true;

    public static int getHttpPort() {
        if (System.getProperty(HTTP_PORT) != null) {
            return Integer.parseInt(System.getProperty(HTTP_PORT));
        }
        return 8171;
    }

    public static File getApplicationFolder() {
        if (System.getProperty(APPLICATION_FOLDER) == null) {
            throw new RuntimeException(APPLICATION_FOLDER + " property is not set");
        }
        return new File(System.getProperty(APPLICATION_FOLDER));
    }

    public static File getThemesFolder() {
        if (useCorrectMacLocations && Os.isMac()) {
            return new File(System.getProperty("user.home"), "Library/Application Support/JSourcePad/Themes");
        } else {
            return new File(new File(System.getProperty("user.home"), ".jsourcepad"), "Shared/Themes");
        }
    }

    public static File getSupportFolder() {
        return new File(getApplicationFolder(), "Shared/Support");
    }

    public static File getBundlesFolder() {
        if (useCorrectMacLocations && Os.isMac()) {
            return new File(System.getProperty("user.home"), "Library/Application Support/JSourcePad/Bundles");
        } else {
            return new File(new File(System.getProperty("user.home"), ".jsourcepad"), "Shared/Bundles");
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
}
