package kkckkc.jsourcepad;

import kkckkc.jsourcepad.model.Application;

import java.io.IOException;

public class SimpleEditorTest extends EditorTest {
    @org.junit.Test
	public void testSimple() throws IOException, InterruptedException {
		System.out.println(Application.get());

        pause();
	}
}
