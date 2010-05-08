package kkckkc.jsourcepad.util.io;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class ScriptExecutor {
	private static final int DELAY_BEFORE_DELAY_EVENT = 500;
	private String script;
	private ExecutorService executorService;

	public ScriptExecutor(String script, ExecutorService executorService) {
		this.script = script;
		this.executorService = executorService;
	}
	
	public Execution execute(Callback callback, Reader input, Map<String, String> environment) throws IOException {
		StringWriter sw = new StringWriter();
		return execute(new Execution(callback, input, sw, sw), environment);
	}

	public Execution execute(Callback callback, Reader input, Writer stdoutHandler, Map<String, String> environment) throws IOException {
		return execute(new Execution(callback, input, stdoutHandler, new StringWriter()), environment);
	}
	
	public Execution execute(Callback callback, Reader input, Writer stdoutHandler, Writer stderrHandler, Map<String, String> environment) throws IOException {
		return execute(new Execution(callback, input, stdoutHandler, stderrHandler), environment);
	}
	
	private Execution execute(final Execution execution, Map<String, String> environment) throws IOException { 
		final ProcessBuilder pb = getProcess(execution, environment);
		final Process p = pb.start();
		
		final Future<?> stdoutFuture = this.executorService.submit(
				new GobblerRunnable(p.getInputStream(), execution.stdout));
		final Future<?> stderrFuture = this.executorService.submit(
				new GobblerRunnable(p.getErrorStream(), execution.stderr));
		execution.stdoutFuture = stdoutFuture;
		execution.stderrFuture = stderrFuture;
		execution.processFuture = this.executorService.submit(new Runnable() {
            public void run() {
	            try {
	                p.waitFor();
	                
	                int exitCode = p.exitValue();
	                
	                stdoutFuture.get();
	                stderrFuture.get();
	                
	                if (exitCode == 0) {
	                	execution.callback.onSuccess(execution);
	                } else {
	                	execution.callback.onFailure(execution);
	                }
	                
	                execution.cleanup();
                } catch (InterruptedException e) {
	                p.destroy();
                	execution.callback.onAbort(execution);
                } catch (ExecutionException e) {
                	throw new RuntimeException(e);
                }
            }
		});
		
		Writer stdin = new OutputStreamWriter(p.getOutputStream());
		
		char[] b = new char[8192];  
		int read;  
		while ((read = execution.input.read(b)) != -1) {  
			stdin.write(b, 0, read);  
		}  
		stdin.flush();
		stdin.close();

		
		try {
	        execution.processFuture.get(DELAY_BEFORE_DELAY_EVENT, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
        	throw new RuntimeException(e);
        } catch (ExecutionException e) {
        	throw new RuntimeException(e);
        } catch (TimeoutException e) {
	        execution.callback.onDelay(execution);
        }
        
        return execution;
	}
	

	private ProcessBuilder getProcess(Execution execution, Map<String, String> environment) throws IOException {
		execution.tempScriptFile = Files.newTempFile("jsourcepad", ".sh");
		execution.tempScriptFile.setExecutable(true);

		Files.write(execution.tempScriptFile, script);
			
		ProcessBuilder pb = new ProcessBuilder("bash", "-c", execution.tempScriptFile.getPath());
		pb.environment().putAll(environment);
		
	    return pb;
    }


	
	public interface Callback {
		public void onSuccess(Execution execution);
		public void onAbort(Execution execution);
		public void onDelay(Execution execution);
		public void onFailure(Execution execution);
	}
	
	public static class CallbackAdapter implements Callback {
		public void onSuccess(Execution execution) {}
		public void onAbort(Execution execution) {}
		public void onDelay(Execution execution) {}
		public void onFailure(Execution execution) {}
	}
	
	
	public static class Execution {
		public Future<?> stderrFuture;
		public Future<?> stdoutFuture;
		private File tempScriptFile;
		private Writer stdout;
		private Writer stderr;
		private Future<?> processFuture;
		private Callback callback;
		private Reader input;
		private boolean cancelled = false;

		public Execution(Callback callback, Reader input, Writer stdout, Writer stderr) {
	        this.callback = callback;
	        this.stdout = stdout;
	        this.stderr = stderr;
	        this.input = input;
        }

		public boolean isCancelled() {
	        return cancelled;
        }

		public void cancel() {
			this.cancelled  = true;
			processFuture.cancel(true);
		}
		
		public String getStdout() {
			if (stdout instanceof StringWriter) {
				return ((StringWriter) stdout).getBuffer().toString();
			}
			return null;
		}

		public String getStderr() {
			if (stderr instanceof StringWriter) {
				return ((StringWriter) stderr).getBuffer().toString();
			}
			return null;
		}
		
		private void cleanup() {
			tempScriptFile.delete();
		}

		public void waitForCompletion() throws InterruptedException, ExecutionException {
			if (isCancelled()) throw new RuntimeException("Process has been cancelled");
	        processFuture.get();
	        stdoutFuture.get();
	        stderrFuture.get();
        }
	}
	
	static class GobblerRunnable implements Runnable {
		private InputStream is;
		private Writer w;
		
		public GobblerRunnable(InputStream is, Writer w) {
			this.is = is;
			this.w = w;
		}

		public void run() {
			try {
				InputStreamReader isr = new InputStreamReader(is);
				BufferedReader br = new BufferedReader(isr);
				String line = null;
				while ((line = br.readLine()) != null) {
					w.write(line);
					w.write("\n");
				}
			} catch (IOException ioe) {
	        	throw new RuntimeException(ioe);
			}
		}
	}

	public static Reader noInput() {
	    return new StringReader("");
    }

	public static Map<String, String> noEnvironment() {
	    return Collections.<String, String>emptyMap();
    }
}
