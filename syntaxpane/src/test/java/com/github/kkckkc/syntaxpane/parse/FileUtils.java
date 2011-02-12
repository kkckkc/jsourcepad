package com.github.kkckkc.syntaxpane.parse;

import java.io.*;

public class FileUtils {
	public static String readFile(InputStream is) throws IOException {
		StringBuilder b = new StringBuilder();
		BufferedReader br = new BufferedReader(new InputStreamReader(is));
		String line;
		while ((line = br.readLine()) != null) {
			b.append(line).append("\n");
		}
		return b.toString();
	}

	public static String readFile(String file) throws IOException {
		return readFile(new FileInputStream(file));
	}
}
