package kkckkc.jsourcepad.util;

import com.google.common.collect.Lists;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.StringTokenizer;

public class TextUtils {

    public static @NotNull String justifyLine(@NotNull String s, int targetWidth) {

        // Remove double spacings
        s = s.replaceAll(" +", " ");

        int len = s.length();

        if (len >= targetWidth) return s;

        int spaceCount = 0;
        for (int i = 0; i < len; i++) {
            if (s.charAt(i) == ' ') spaceCount++;
        }

        if (spaceCount == 0) return s;

        int newSpaceCount = spaceCount + (targetWidth - len);

        int spaceFactor = newSpaceCount / spaceCount;
        int extraSpaceCount = newSpaceCount % spaceCount;

        int spIdx = 0;
        
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < len; i++) {
            if (s.charAt(i) == ' ') {
                for (int j = 0; j < spaceFactor; j++) {
                    builder.append(" ");
                }
                if (spIdx++ < extraSpaceCount) {
                    builder.append(" ");
                }
            } else {
                builder.append(s.charAt(i));
            }
        }

        return builder.toString();
    }

    public static @NotNull String[] wrap(@NotNull String s, int wrapColumn) {
        List<String> dest = Lists.newArrayList();

        StringBuilder currentLine = new StringBuilder(wrapColumn);

        // Remove double spacings
        s = s.replaceAll("\n", " ").replaceAll(" +", " ");

        StringTokenizer tok = new StringTokenizer(s, " ");
        while (tok.hasMoreTokens()) {
            String word = tok.nextToken();
            int len = word.length();
            if (currentLine.length() > 0) {
                len++;
            }
            if (currentLine.length() + len > wrapColumn) {
                dest.add(currentLine.toString());
                currentLine = new StringBuilder();
                currentLine.append(word);
            } else {
                if (currentLine.length() > 0) {
                    currentLine.append(" ");
                }
                currentLine.append(word);
            }
        }

        if (currentLine.length() > 0)
            dest.add(currentLine.toString());

        return dest.toArray(new String[dest.size()]);
    }

}
