package kkckkc.jsourcepad.os.windows.registry;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;

public class Registry {

    public static boolean exits(String key) throws IOException, InterruptedException {
        Process p = Runtime.getRuntime().exec(new String[] { "reg", "query", key });
        return p.waitFor() == 0;
    }

    public static void add(String key, String value) throws IOException, InterruptedException {
        Process p = Runtime.getRuntime().exec(new String[] { "reg", "add", key, "/ve", "/f", "/t", "REG_SZ", "/d", value });

        StreamReader reader = new StreamReader(p.getInputStream());
        reader.start();
        p.waitFor();
        reader.join();

        p.waitFor();
    }

    public static void remove(String key) throws InterruptedException, IOException {
        Process p = Runtime.getRuntime().exec(new String[] { "reg", "delete", key, "/f" });
        p.waitFor();
    }


    static class StreamReader extends Thread {
        private InputStream is;
        private StringWriter sw= new StringWriter();

        public StreamReader(InputStream is) {
            this.is = is;
        }

        public void run() {
            try {
                int c;
                while ((c = is.read()) != -1)
                    sw.write(c);
            }
            catch (IOException e) {
        }
        }

        public String getResult() {
            return sw.toString();
        }
    }

}
