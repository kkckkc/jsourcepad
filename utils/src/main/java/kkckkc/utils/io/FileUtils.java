package kkckkc.utils.io;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class FileUtils {

    /**
     * Creates a temporary file that is automatically deleted on exit
     */
	public static @NotNull File createDeleteOnExitTempFile(@NotNull String prefix, @NotNull String suffix) throws IOException {
		File tempFile = File.createTempFile(prefix, suffix);
		tempFile.deleteOnExit();
		return tempFile;
	}

    /**
     * Checks if a file is an ancestor of another file. Being an ancestor of another file means
     * appearing somewhere in the "path" of the descendant
     */
    public static boolean isAncestorOf(@Nullable File descendant, @Nullable File ancestor) {
        if (descendant == null || ancestor == null) return false;
        if (ancestor.equals(descendant)) {
            return false;
        }

        while (descendant != null && ! ancestor.equals(descendant)) {
            descendant = descendant.getParentFile();
        }

        return descendant != null;
    }

    /**
     * Removes the extension from a filename, i.e. the last dot and everything after it
     */
    public static @NotNull String getBaseName(@NotNull File file) {
        String fileName = file.getName();
        if (fileName.indexOf('.') < 0) return fileName;
        return fileName.substring(0, fileName.lastIndexOf('.'));
    }

    /**
     * Replaces any occurrences of the users home folder in the path with a tilde, effectively
     * shorting the path of a file for display purposes
     */
    public static @NotNull String shortenWithTildeNotation(@NotNull String path) {
        String home = System.getProperty("user.home");
        if (path.startsWith(home + File.separator)) {
            return "~" + path.substring(home.length());
        }
        return path;
    }

    /**
     * Opposite of shortenWithTildeNotation, i.e. replacing any occurrences of ~ with
     * the full path of the users home directory
     */
    public static @NotNull String expandTildeNotation(@NotNull String text) {
        if (text.startsWith("~")) return System.getProperty("user.home").replace('\\', '/') + text.substring(1);
        return text;
    }

    public static @NotNull List<File> findAllFiles(@NotNull File root) {
        List<File> fileList = new ArrayList<File>();
        findAllFilesWorker(root, fileList);
        return fileList;
    }

    private static void findAllFilesWorker(File root, List<File> fileList) {
        fileList.add(root);
        File[] children = root.listFiles();
        if (children == null) return;

        for (File f : children) findAllFilesWorker(f, fileList);
    }

    public static byte[] readBytes(File file) throws IOException {
		FileInputStream fis = new FileInputStream(file);
        try {
            byte[] bytes = new byte[fis.available()];
            fis.read(bytes);
            fis.close();

            return bytes;
        } finally {
            fis.close();
        }
    }
}
