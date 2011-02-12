package kkckkc.jsourcepad.util.io;

import junit.framework.TestCase;
import kkckkc.jsourcepad.util.io.ScriptExecutor.CallbackAdapter;
import kkckkc.jsourcepad.util.io.ScriptExecutor.Execution;

import java.io.IOException;
import java.io.StringReader;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class ScriptExecutorTest extends TestCase {
	public static final Map<String, String> EMPTY_ENV = new HashMap<String, String>();
	
	public void testSimple() throws IOException {
		ExecutorService s = Executors.newCachedThreadPool();
		ScriptExecutor se = new ScriptExecutor("echo 'Hej'", s);
        se.setTextmateSetup(false);
		se.execute(new CallbackAdapter() {
            public void onDelay(Execution execution) {
            	fail("Delay not expected");
            }

			public void onSuccess(Execution execution) {
	            assertEquals("Hej\n", execution.getStdout());
            }
		}, new StringReader(""), EMPTY_ENV);
	}

	public void testDelay() throws IOException, InterruptedException {
		final CountDownLatch cdl = new CountDownLatch(1);
		
		ExecutorService s = Executors.newCachedThreadPool();
		ScriptExecutor se = new ScriptExecutor("sleep 2; echo 'Hej'", s);
        se.setTextmateSetup(false);
		se.execute(new CallbackAdapter() {
            public void onFailure(Execution execution) {
            	fail("Failure not expected");
            }

            public void onSuccess(Execution execution) {
	            assertEquals("Hej\n", execution.getStdout());
	            cdl.countDown();
            }
		}, new StringReader(""), EMPTY_ENV);
		cdl.await(10, TimeUnit.SECONDS);
	}

	public void testCancel() throws IOException, InterruptedException {
		final CountDownLatch cdl = new CountDownLatch(1);
		
		ExecutorService s = Executors.newCachedThreadPool();
		ScriptExecutor se = new ScriptExecutor("sleep 2; echo 'Hej'", s);
        se.setTextmateSetup(false);
		se.execute(new CallbackAdapter() {
            public void onDelay(Execution execution) {
            	execution.cancel();
            }

            public void onFailure(Execution execution) {
            	fail("Failure not expected");
            }

            public void onSuccess(Execution execution) {
            	fail("Success not expected");
            }

            public void onAbort(Execution execution) {
				cdl.countDown();
            }
		}, new StringReader(""), EMPTY_ENV);
		cdl.await(10, TimeUnit.SECONDS);
	}
}
