package kkckkc.jsourcepad.dialog;

import com.google.common.collect.Lists;
import kkckkc.jsourcepad.model.Application;
import kkckkc.jsourcepad.model.Window;
import org.mortbay.jetty.servlet.Context;
import org.mortbay.jetty.servlet.ServletHolder;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PostConstruct;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.logging.Logger;

public class DialogServer implements BeanFactoryAware {

    private Logger logger = Logger.getLogger(DialogServer.class.toString());
    private Context context;
    private BeanFactory beanFactory;

    @Autowired
    public void setContext(Context context) {
        this.context = context;
    }

    @PostConstruct
    public void init() {
        final String path = "/dialog/*";

        context.addServlet(new ServletHolder(new HttpServlet() {
            @Override
            protected void doPost(final HttpServletRequest req, final HttpServletResponse resp) throws ServletException, IOException {
                final PrintWriter writer = resp.getWriter();

                final String dialogName = req.getPathInfo().substring(1);

                if (! beanFactory.containsBean("dialog/" + dialogName)) {
                    logger.warning("Dialog " + dialogName + " not found");
                    writer.append("Dialog " + dialogName + " not found");

                    resp.addHeader("X-ResponseCode", "1");
                    resp.setStatus(HttpServletResponse.SC_OK);

                    writer.close();

                    return;
                }

                final List<String> args = Lists.newArrayList();
                int i = 0;
                while (true) {
                    String s = req.getParameter("arg" + i);
                    if (s == null) break;
                    args.add(s);
                    i++;
                }

                Dialog dialog = beanFactory.getBean("dialog/" + dialogName, Dialog.class);
                Window window = Application.get().getWindowManager().getWindow(Integer.parseInt(req.getParameter("__WINDOW__")));
                try {
                    int returnCode = dialog.execute(
                            window,
                            writer,
                            req.getParameter("__PWD__"),
                            req.getParameter("__STDIN__"),
                            args.toArray(new String[args.size()])
                    );

                    resp.addHeader("X-ResponseCode", Integer.toString(returnCode));
                    resp.setStatus(HttpServletResponse.SC_OK);

                    writer.close();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }), path);
    }

    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        this.beanFactory = beanFactory;
    }
}
