package kkckkc.jsourcepad.model.bundle;

import java.awt.Desktop;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.StringReader;
import java.io.Writer;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

import javax.swing.JOptionPane;
import javax.swing.text.BadLocationException;

import kkckkc.jsourcepad.model.Application;
import kkckkc.jsourcepad.model.Buffer;
import kkckkc.jsourcepad.model.Window;
import kkckkc.jsourcepad.util.io.UISupportCallback;
import kkckkc.jsourcepad.util.io.ScriptExecutor;
import kkckkc.jsourcepad.util.io.ScriptExecutor.Execution;
import kkckkc.syntaxpane.model.Interval;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpContext;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

@SuppressWarnings("restriction")
public class CommandBundleItem implements BundleItem {
	
	private static final String OUTPUT_SHOW_AS_HTML = "showAsHTML";
	private static final String OUTPUT_DISCARD = "discard";
	private static final String OUTPUT_REPLACE_SELECTED_TEXT = "replaceSelectedText";
	private static final String OUTPUT_SHOW_AS_TOOLTIP = "showAsTooltip";

	private static final String INPUT_NONE = "none";
	private static final String INPUT_DOCUMENT = "document";
	private static final String INPUT_SELECTION = "selection";
	
	private String output;
	private String command;
	private String input;
	private String fallbackInput;
	private BundleItemSupplier bundleItemSupplier;
	
	public CommandBundleItem(BundleItemSupplier bundleItemSupplier, String command, String input, String fallbackInput, String output) {
		this.bundleItemSupplier = bundleItemSupplier;
		this.command = command;
		this.input = input;
		this.fallbackInput = fallbackInput;
		this.output = output;
	}
	
	public static CommandBundleItem create(BundleItemSupplier bundleItemSupplier, Map<?, ?> m) {
	    return new CommandBundleItem(bundleItemSupplier,
	    		(String) m.get("command"),
	    		(String) m.get("input"),
	    		(String) m.get("fallbackInput"),
	    		(String) m.get("output"));
    }

	
	public interface ExecutionMethod {
		public void start(ScriptExecutor scriptExecutor, String input, Map<String, String> environment) throws IOException, URISyntaxException;
	}
	
	
	public void execute(Window window) throws Exception {
		ScriptExecutor scriptExecutor = new ScriptExecutor(command, Application.get().getThreadPool());

		ExecutionMethod executionMethod = createExecutionMethod(window);
		executionMethod.start(scriptExecutor, getInput(window), EnvironmentProvider.getEnvironment(window, bundleItemSupplier));
	}

	private ExecutionMethod createExecutionMethod(Window window) {
	    ExecutionMethod outputMethod;
	    if (OUTPUT_SHOW_AS_HTML.equals(output)) {
	    	outputMethod = new HtmlExectuionMethod(window);
	    } else {
	    	outputMethod = new DefaultExecutionMethod(output, window);
	    }
	    return outputMethod;
    }
	
	private String getInput(Window window) throws IOException, BadLocationException {
		String text;
		if (! INPUT_NONE.equals(input)) {
			text = getTextForInput(input, window);
			if (text == null) {
				text = getTextForInput(fallbackInput == null ? INPUT_DOCUMENT : fallbackInput, window);
			}
			
			if (text == null) {
				throw new RuntimeException("No input");
			}
		} else {
			text = "";
		}
		
		return text;
    }

	private String getTextForInput(String type, Window window) throws BadLocationException {
		Buffer buffer = window.getDocList().getActiveDoc().getActiveBuffer();
		if (INPUT_SELECTION.equals(type)) {
			return buffer.getText(buffer.getSelection());
		} else if (INPUT_DOCUMENT.equals(type)) {
			return buffer.getText(buffer.getCompleteDocument());
		} else if (type == null) {
			return null;
		} else {
			throw new RuntimeException("Unsupported input type " + type);
		}
	}
	

	
	

	public static class HtmlExectuionMethod implements ExecutionMethod {
		private Window window;

		public HtmlExectuionMethod(Window window) {
			this.window = window;
		}

		@Override
        public void start(final ScriptExecutor scriptExecutor, final String input, final Map<String, String> environment)
                throws IOException, URISyntaxException {
			String path = "/" + System.currentTimeMillis(); 
			
			final HttpServer server = Application.get().getHttpServer();
			final HttpContext context = server.createContext(path);
			context.setHandler(new HttpHandler() {
                public void handle(HttpExchange exchange) throws IOException {
    				String requestMethod = exchange.getRequestMethod();
    				if (requestMethod.equalsIgnoreCase("GET")) {
    					Headers responseHeaders = exchange.getResponseHeaders();
    					responseHeaders.set("Content-Type", "text/html");
    					exchange.sendResponseHeaders(200, 0);
    					
    					final OutputStream responseBody = exchange.getResponseBody();
    					final Writer writer = new OutputStreamWriter(responseBody);
    					
    					final CountDownLatch cdl = new CountDownLatch(1);
    					
    					scriptExecutor.execute(new UISupportCallback(window.getJFrame()) {
                            public void onAfterDone() {
	            				cdl.countDown();
	        					try {
	                                writer.close();
                                } catch (IOException e) {
	                                throw new RuntimeException(e);
                                }
                            }
    					}, new StringReader(input), writer, environment);
    					
    					
    					try {
	                        cdl.await();
                        } catch (InterruptedException e) {
                        	throw new RuntimeException(e);
                        }
        				server.removeContext(context);
    				}
                }
			});
			
			Desktop.getDesktop().browse(new URI("http://localhost:" + server.getAddress().getPort() + path));
        }
	}
	
	public static class DefaultExecutionMethod implements ExecutionMethod {
		private String output;
		private Window window;
		
		public DefaultExecutionMethod(String output, Window window) {
			this.output = output;
			this.window = window;
		}
		
		@Override
        public void start(ScriptExecutor scriptExecutor, String input, Map<String, String> environment) throws IOException {
	        scriptExecutor.execute(new UISupportCallback(window.getJFrame()) {
                public void onAfterSuccess(final Execution execution) {
                    String s = execution.getStdout();
                	if (s == null) s = "";

        			if (OUTPUT_SHOW_AS_TOOLTIP.equals(output)) {
        				JOptionPane.showMessageDialog(window.getJFrame(), s);
        			} else if (OUTPUT_REPLACE_SELECTED_TEXT.equals(output)) {
        				Buffer buffer = window.getDocList().getActiveDoc().getActiveBuffer();
        				Interval selection = buffer.getSelection();
        				if (selection == null || selection.isEmpty()) {
        					selection = new Interval(0, buffer.getLength());
        				}
        				buffer.replaceText(selection, s, null);
        			} else if (OUTPUT_DISCARD.equals(output)) {
        				// Do nothing
        				
        			} else {
        				throw new RuntimeException("Unsupported output " + output);
        			}
                }
	        }, new StringReader(input), environment);
        }
	}


	@Override
    public BundleItemSupplier getBundleItemRef() {
	    return bundleItemSupplier;
    }
}
