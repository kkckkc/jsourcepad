package kkckkc.jsourcepad;

import java.io.IOException;

import junit.framework.TestCase;
import kkckkc.jsourcepad.model.Application;
import kkckkc.jsourcepad.model.WindowManager;

public class EditorTest extends TestCase {

	public void testSimple() throws IOException, InterruptedException {
		Bootstrap.main(null);
		
		Application app = Application.get();
		WindowManager wm = app.getWindowManager();
		while (wm.getWindows().isEmpty()) {
			Thread.sleep(200);
		}
		
		System.out.println(Application.get());
	}
	
}
