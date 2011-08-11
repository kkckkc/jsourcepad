package kkckkc.jsourcepad.http;

import com.google.common.io.Files;
import kkckkc.jsourcepad.model.*;
import org.mortbay.jetty.servlet.Context;
import org.mortbay.jetty.servlet.ServletHolder;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PostConstruct;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;

public class PreviewServer extends AbstractServer {

    private Context context;

    @Autowired
    public void setHttpServer(Context context) {
        this.context = context;
    }

    @PostConstruct
    public void init() {
        final String path = "/preview/*";

        context.addServlet(new ServletHolder(
                new HttpServlet() {
                    @Override
                    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
                        String httpPath = req.getPathInfo().substring(1);

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

                        OutputStream responseBody = resp.getOutputStream();

                        if (docToServe != null) {
                            String mimeEncoding = "text/html";
                            if (docToServe.isBackedByFile()) {
                                mimeEncoding = getMimeEncoding(docToServe.getFile());
                            }

                            resp.setContentType(mimeEncoding);

                            Buffer buffer = docToServe.getActiveBuffer();
                            String content = buffer.getCompleteDocument().getText();

                            Writer writer = new OutputStreamWriter(responseBody);
                            writer.append(content);
                            writer.flush();
                            writer.close();
                        } else {
                            File f = new File(project.getProjectDir(), path);

                            resp.setContentType(getMimeEncoding(f));

                            Files.copy(f, responseBody);
                            responseBody.flush();
                            responseBody.close();
                        }
                    }
                }
        ), path);
    }


    private String getFilePath(String httpPath) {
        return httpPath.substring(httpPath.indexOf('/'));
    }

    private int getWindowId(String httpPath) {
        return Integer.parseInt(httpPath.substring(0, httpPath.indexOf('/')));
    }
}
