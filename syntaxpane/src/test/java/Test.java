import java.awt.Button;
import java.awt.Desktop;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.concurrent.Executors;

import javax.swing.KeyStroke;

import kkckkc.syntaxpane.model.FoldManager;
import kkckkc.syntaxpane.model.LineManager;
import kkckkc.syntaxpane.parse.CharProvider;
import kkckkc.syntaxpane.parse.Parser;
import kkckkc.syntaxpane.parse.Parser.ChangeEvent;
import kkckkc.syntaxpane.parse.grammar.Language;
import kkckkc.syntaxpane.parse.grammar.LanguageManager;
import kkckkc.syntaxpane.parse.grammar.textmate.TextmateLanguageProvider;
import kkckkc.syntaxpane.regex.JoniPatternFactory;

import com.sun.net.httpserver.*;

public class Test {
	public static void main(String... args) throws IOException, URISyntaxException {
		// System.out.println(System.getProperty("os.name"));

		// System.out.println(
		// new JoniPatternFactory().create("^(?!(.*[};:])?\\s*(//|/\\*.*\\*/\\s*$)).*[^\\s;:{}]\\s*$").
		// matcher("a").matches());

		// System.out.println(KeyStroke.getKeyStroke("ctrl shift P"));
		// System.out.println(KeyStroke.getKeyStroke(new Character('p'), KeyEvent.CTRL_MASK | KeyEvent.SHIFT_MASK));

		// KeyStroke ks = KeyStroke.getKeyStroke(new Character('p'), KeyEvent.CTRL_MASK | KeyEvent.SHIFT_MASK);

		// KeyEvent keyEvent = new KeyEvent(new Button(), KeyEvent.KEY_PRESSED, System.currentTimeMillis(),
		// KeyEvent.CTRL_MASK | KeyEvent.SHIFT_MASK, KeyEvent.VK_P, 'p');
		//		
		// System.out.println(KeyStroke.getKeyStrokeForEvent(keyEvent));

		// Desktop.getDesktop().browse(new URI("http://www.dn.se"));

		InetSocketAddress addr = new InetSocketAddress(8080);
		final HttpServer server = HttpServer.create(addr, 0);

		class MyHandler implements HttpHandler {
			@Override
			public void handle(HttpExchange exchange) throws IOException {
				System.out.println("*****");
				String requestMethod = exchange.getRequestMethod();
				if (requestMethod.equalsIgnoreCase("GET")) {
					Headers responseHeaders = exchange.getResponseHeaders();
					responseHeaders.set("Content-Type", "text/plain");
					exchange.sendResponseHeaders(200, 0);
					
					OutputStream responseBody = exchange.getResponseBody();
					responseBody.write(new byte[] { (byte) 'o', (byte) 'k' });
					responseBody.close();
				}
				
				System.out.println("--------");
				
				server.createContext("/test2", this);
			}
		}

		HttpContext ctx = server.createContext("/test", new MyHandler());
		server.setExecutor(Executors.newCachedThreadPool());
		server.start();

		
		//		
		// LanguageManager lm = new LanguageManager();
		// lm.setProvider(
		// new TextmateLanguageProvider(new File("/home/magnus/Dropbox/SharedSupport/Bundles")));
		//		
		// Language l = lm.getLanguage("source.java");
		// l.setLanguageManager(lm);
		// l.compile();
		//		
		// StringBuffer b = new StringBuffer("package lorem.ipsum;");
		// // 01234567890123456789012345
		// // 0 1 2
		//		
		// CharProvider pro = new CharProvider.StringBuffer(b);
		// LineManager lineManager = new LineManager(pro);
		// Parser p = new Parser(l, lineManager, new FoldManager(lineManager));
		//		
		// p.parse(0, pro.getLength(), ChangeEvent.ADD);
		//		
		// lineManager.dump();
	}
}
