package kkckkc.utils.io;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class FileUtils {
	public static File newTempFile(String prefix, String suffix) throws IOException {
		File tempFile = File.createTempFile(prefix, suffix);
		tempFile.deleteOnExit();
		return tempFile;
	}

    public static boolean isAncestorOf(File descendant, File ancestor) {
        if (ancestor.equals(descendant)) {
            return false;
        }

        while (descendant != null && ! ancestor.equals(descendant)) {
            descendant = descendant.getParentFile();
        }

        return descendant != null;
    }

    public static String getBaseName(File file) {
        String s = file.getName();
        return getBaseName(s);
    }

    public static String getBaseName(String s) {
        if (! s.contains(".")) return s;
        return s.substring(0, s.lastIndexOf('.'));
    }

    public static String abbreviate(String path) {
        if (path.startsWith(System.getProperty("user.home") + File.separator)) {
            return "~" + path.substring(System.getProperty("user.home").length());
        }
        return path;
    }

    public static String expandAbbreviations(String text) {
        return text.replaceAll("~", System.getProperty("user.home").replace('\\', '/'));
    }

    public static List<File> recurse(File root) {
        List<File> dest = new ArrayList<File>();
        recurseWorker(root, dest);
        return dest;
    }

    private static void recurseWorker(File root, List<File> dest) {
        dest.add(root);
        File[] children = root.listFiles();
        if (children != null) {
            for (File f : children) {
                recurseWorker(f, dest);
            }
        }
    }

    public static byte[] readBytes(File file) throws IOException {
		FileInputStream fis = new FileInputStream(file);
        try {
            byte[] dest = new byte[fis.available()];
            fis.read(dest);
            fis.close();

            return dest;
        } finally {
            fis.close();
        }
    }
}
