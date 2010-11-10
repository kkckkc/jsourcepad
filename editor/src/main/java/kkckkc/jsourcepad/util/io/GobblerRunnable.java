package kkckkc.jsourcepad.util.io;

import com.google.common.io.CharStreams;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Writer;

class GobblerRunnable implements Runnable {
    private InputStream is;
    private Writer w;

    public GobblerRunnable(InputStream is, Writer w) {
        this.is = is;
        this.w = w;
    }

    public void run() {
        try {
            CharStreams.copy(new InputStreamReader(is, "utf-8"), w);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
