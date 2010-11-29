package kkckkc.jsourcepad.model.bundle;

import com.google.common.collect.Maps;
import org.mortbay.jetty.servlet.Context;
import org.mortbay.jetty.servlet.ServletHolder;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PostConstruct;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;

public class CommandBundleServer {

    private Map<Long, Handler> handlers = Maps.newHashMap();
    private Context context;

    @Autowired
    public void setContext(Context context) {
        this.context = context;
    }

    @PostConstruct
    public void init() {
        context.addServlet(new ServletHolder(new HttpServlet() {
            @Override
            protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
                String path = req.getPathInfo().substring(1);

                long key = Long.parseLong(path);

                Handler handler = handlers.get(key);
                handler.handle(resp);
                handlers.remove(key);
            }
        }), "/command/*");
    }

    public long register(Handler handler) {
        long id = System.currentTimeMillis();
        handlers.put(id, handler);
        return id;
    }


    public interface Handler {
        public void handle(HttpServletResponse resp) throws IOException;
    }
}
