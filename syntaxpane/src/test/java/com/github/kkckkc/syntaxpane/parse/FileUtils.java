package com.github.kkckkc.syntaxpane.parse;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

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

	public static String readFile(String file) throws FileNotFoundException, IOException {
		return readFile(new FileInputStream(file));
	}
}
