package kkckkc.jsourcepad.util.io;

import java.io.IOException;
import java.io.Writer;

public class TeeWriter extends Writer {
    private Writer writer1;
    private Writer writer2;

    public TeeWriter(Writer writer1, Writer writer2) {
        this.writer1 = writer1;
        this.writer2 = writer2;
    }

    @Override
    public void write(char[] cbuf, int off, int len) throws IOException {
        writer1.write(cbuf, off, len);
        writer2.write(cbuf, off, len);
    }

    @Override
    public void flush() throws IOException {
        writer1.flush();
        writer2.flush();
    }

    @Override
    public void close() throws IOException {
        writer1.close();
        writer2.close();
    }
}
