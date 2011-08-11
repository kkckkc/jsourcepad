package kkckkc.jsourcepad.http;

import com.google.common.io.Files;
import kkckkc.jsourcepad.util.Cygwin;
import org.mortbay.jetty.servlet.Context;
import org.mortbay.jetty.servlet.ServletHolder;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PostConstruct;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URLDecoder;

public class FileServer extends AbstractServer {
    private Context context;

    @Autowired
    public void setHttpServer(Context context) {
        this.context = context;
    }

    @PostConstruct
    public void init() {
        final String path = "/files/*";

        context.addServlet(new ServletHolder(new HttpServlet() {
            @Override
            protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
                String httpPath = req.getPathInfo();
                httpPath = URLDecoder.decode(httpPath, "utf-8");

                OutputStream responseBody = resp.getOutputStream();

                File f = new File(Cygwin.toFile(httpPath));

                if (! f.exists()) {
                    resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                    responseBody.flush();
                    responseBody.close();
                    return;
                }

                resp.setContentType(getMimeEncoding(f));

                Files.copy(f, responseBody);
                responseBody.flush();
                responseBody.close();
            }
        }), path);

    }

}
