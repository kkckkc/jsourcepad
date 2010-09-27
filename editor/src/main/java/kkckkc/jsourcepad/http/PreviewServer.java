package kkckkc.jsourcepad.http;

import com.google.common.io.Files;
import com.sun.net.httpserver.*;
import kkckkc.jsourcepad.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import sun.net.www.MimeEntry;
import sun.net.www.MimeTable;

import javax.annotation.PostConstruct;
import java.io.*;

public class PreviewServer {

    private HttpServer httpServer;

    @Autowired
    public void setHttpServer(HttpServer httpServer) {
        this.httpServer = httpServer;
    }

    @PostConstruct
    public void init() {
        final String path = "/preview";

        final HttpContext context = httpServer.createContext(path);
        context.setHandler(new HttpHandler() {
            public void handle(HttpExchange exchange) throws IOException {
                String requestMethod = exchange.getRequestMethod();
                if (requestMethod.equalsIgnoreCase("GET")) {
                    String httpPath = exchange.getRequestURI().toString();
                    httpPath = httpPath.substring(path.length() + 1);

                    int windowId = getWindowId(httpPath);
                    String path = getFilePath(httpPath);

                    Window window = Application.get().getWindowManager().getWindow(windowId);
                    Project project = window.getProject();

                    // Look through open files, these should serve the current state, not the saved
                    // state
                    int tabIdx = 0;
                    Doc docToServe = null;
                    for (Doc doc : window.getDocList().getDocs()) {
                        if (doc.isBackedByFile() && project.getProjectRelativePath(doc.getFile().getPath()).equals(path)) {
                            docToServe = doc;
                        } else if (path.equals("/tab-" + tabIdx)) {
                            docToServe = doc;
                        }
                        tabIdx++;
                    }
                    
                    OutputStream responseBody = exchange.getResponseBody();
                    Headers responseHeaders = exchange.getResponseHeaders();

                    if (docToServe != null) {
                        String mimeEncoding = "text/html";
                        if (docToServe.isBackedByFile()) {
                            mimeEncoding = getMimeEncoding(docToServe.getFile());
                        }

                        responseHeaders.set("Content-Type", mimeEncoding);
                        exchange.sendResponseHeaders(200, 0);

                        Buffer buffer = docToServe.getActiveBuffer();
                        String content = buffer.getText(buffer.getCompleteDocument());

                        Writer writer = new OutputStreamWriter(responseBody);
                        writer.append(content);
                        writer.flush();
                        writer.close();
                    } else {
                        File f = new File(project.getProjectDir(), path);

                        responseHeaders.set("Content-Type", getMimeEncoding(f));
                        exchange.sendResponseHeaders(200, 0);

                        Files.copy(f, responseBody);
                        responseBody.flush();
                        responseBody.close();
                    }
                }
            }
        });

    }

    private String getFilePath(String httpPath) {
        return httpPath.substring(httpPath.indexOf('/'));
    }

    private int getWindowId(String httpPath) {
        return Integer.parseInt(httpPath.substring(0, httpPath.indexOf('/')));
    }

    private String getMimeEncoding(File f) {
        MimeEntry me = MimeTable.getDefaultTable().findByFileName(f.getName());
        return me == null ? "text/html" : me.getType();
    }
}
