package kkckkc.utils;

import org.jetbrains.annotations.NotNull;

public class StringUtils {

    public static String stripPrefix(@NotNull String s, @NotNull String prefix) {
        return (s.startsWith(prefix)) ? s.substring(prefix.length()) : s;
    }

}
