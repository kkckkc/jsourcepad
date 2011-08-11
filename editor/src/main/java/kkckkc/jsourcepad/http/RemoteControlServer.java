package kkckkc.jsourcepad.http;

import org.mortbay.jetty.servlet.Context;
import org.mortbay.jetty.servlet.ServletHolder;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PostConstruct;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class RemoteControlServer extends AbstractServer {
    private Context context;

    @Autowired
    public void setHttpServer(Context context) {
        this.context = context;
    }

    @PostConstruct
    public void init() {
        final String path = "/cmd/*";

        context.addServlet(new ServletHolder(new HttpServlet() {
            @Override
            protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
                doGet(req, resp);
            }

            @Override
            protected void doGet(HttpServletRequest req, final HttpServletResponse resp) throws ServletException, IOException {
                String cmd = req.getPathInfo().substring(1);

                RemoteControl.Command command = RemoteControl.getCommand(cmd);
                if (command != null) {
                    command.execute(req, resp);
                } else {
                    throw new RuntimeException("Unsupport cmd");
                }
            }
        }), path);
    }

}
