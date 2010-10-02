package kkckkc.jsourcepad.util.io;

import com.google.common.base.Function;
import com.google.common.base.Predicate;

import java.io.IOException;
import java.io.Writer;

public class TransformingWriter extends Writer {
    private Function<String, String> transformation;
    private Predicate<Character> chunkStrategy;
    private Writer delegatee;
    private StringBuilder buffer;

    public static Predicate<Character> CHUNK_BY_LINE = new Predicate<Character>() {
        @Override
        public boolean apply(Character character) {
            return character == '\n';
        }
    };

    public TransformingWriter(Writer delegatee, Predicate<Character> chunkStrategy, Function<String, String> transformation) {
        this.transformation = transformation;
        this.chunkStrategy = chunkStrategy;
        this.delegatee = delegatee;
        this.buffer = new StringBuilder();
    }

    @Override
    public void write(char[] cbuf, int off, int len) throws IOException {
        for (int i = off; i < (off + len); i++) {
            buffer.append(cbuf[i]);
            if (chunkStrategy.apply(cbuf[i])) {
                flushBuffer();
            }
        }
    }

    @Override
    public void flush() throws IOException {
        flushBuffer();
        delegatee.flush();
    }

    @Override
    public void close() throws IOException {
        flushBuffer();
        delegatee.close();
    }

    private void flushBuffer() throws IOException {
        String transformedResult = transformation.apply(buffer.toString());
        delegatee.write(transformedResult);
        buffer.setLength(0);
    }
}
