package kkckkc.jsourcepad.http;

import com.google.common.collect.Lists;
import com.google.common.io.Files;
import kkckkc.jsourcepad.command.global.OpenCommand;
import kkckkc.jsourcepad.command.window.FileOpenCommand;
import kkckkc.jsourcepad.model.*;
import kkckkc.jsourcepad.util.Config;
import kkckkc.jsourcepad.util.Cygwin;
import kkckkc.jsourcepad.util.messagebus.DispatchStrategy;
import kkckkc.jsourcepad.util.messagebus.Subscription;
import kkckkc.syntaxpane.model.Interval;
import kkckkc.syntaxpane.model.LineManager;
import kkckkc.utils.StringUtils;
import org.mortbay.jetty.servlet.Context;
import org.mortbay.jetty.servlet.ServletHolder;
import org.springframework.beans.factory.annotation.Autowired;
import sun.net.www.MimeEntry;
import sun.net.www.MimeTable;

import javax.annotation.PostConstruct;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URLDecoder;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CountDownLatch;

public class PreviewServer {

    private Context context;

    @Autowired
    public void setHttpServer(Context context) {
        this.context = context;
    }

    @PostConstruct
    public void init() {
        initPreview();
        initFile();
        initCmd();
    }

    private void initPreview() {
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

    private void initCmd() {
        final String path = "/cmd/*";

        context.addServlet(new ServletHolder(new HttpServlet() {
            @Override
            protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
                doGet(req, resp);
            }

            @Override
            protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
                String cmd = req.getPathInfo().substring(1);

                resp.setStatus(204);

                if ("open".equals(cmd)) {
                    String url = req.getParameter("url");
                    url = StringUtils.removePrefix(url, "http://localhost:" + Config.getHttpPort() + "/files");

                    Window window;
                    if (req.getParameter("windowId") != null) {
                        int windowId = Integer.parseInt(req.getParameter("windowId"));

                        window = Application.get().getWindowManager().getWindow(windowId);
                        window.getCommandExecutor().execute(new FileOpenCommand(url));
                    } else {
                        OpenCommand openCommand = new OpenCommand(url, false);
                        Application.get().getCommandExecutor().executeSync(openCommand);
                        window = openCommand.getWindow();
                    }

                    String line = req.getParameter("line");
                    if (line != null) {
                        int lineIdx = Integer.parseInt(line);

                        Buffer buffer = window.getDocList().getActiveDoc().getActiveBuffer();
                        LineManager lm = buffer.getLineManager();

                        Iterator<LineManager.Line> it = lm.iterator();
                        while (it.hasNext()) {
                            LineManager.Line l = it.next();
                            if (l.getIdx() == (lineIdx - 1)) {
                                buffer.setSelection(Interval.createEmpty(l.getStart()));
                                break;
                            }
                        }
                    }
                } else if ("mate".equals(cmd)) {
                    final List<String> args = Lists.newArrayList();
                    int i = 0;
                    while (true) {
                        String s = req.getParameter("arg" + i);
                        if (s == null) break;
                        args.add(s);
                        i++;
                    }

                    boolean wait = false;
                    for (String s : args) {
                        if (s.startsWith("-") && s.contains("w")) {
                            wait = true;
                        }
                    }

                    if (wait) {
                        OpenCommand openCommand = new OpenCommand(args.get(args.size() - 1), true);
                        Application.get().getCommandExecutor().executeSync(openCommand);
                        final Window window = openCommand.getWindow();

                        final CountDownLatch latch = new CountDownLatch(1);

                        Subscription subscription = Application.get().topic(WindowManager.Listener.class).subscribe(DispatchStrategy.SYNC, new WindowManager.Listener() {
                            @Override
                            public void created(Window w) {
                            }

                            @Override
                            public void destroyed(Window w) {
                                if (w == window) latch.countDown();
                            }
                        });

                        try {
                            latch.await();
                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        } finally {
                            subscription.unsubscribe();
                        }
                    } else {
                        Application.get().getCommandExecutor().execute(new OpenCommand(args.get(args.size() - 1), false));
                    }
                } else {
                    throw new RuntimeException("Unsupport cmd");
                }
            }
        }), path);
    }

    private void initFile() {
        final String path = "/files/*";

        context.addServlet(new ServletHolder(new HttpServlet() {
            @Override
            protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
                String httpPath = req.getPathInfo();
                httpPath = URLDecoder.decode(httpPath, "utf-8");

                OutputStream responseBody = resp.getOutputStream();

                File f = new File(Cygwin.toFile(httpPath));

                if (! f.exists()) {
                    resp.setStatus(404);
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

    private String getFilePath(String httpPath) {
        return httpPath.substring(httpPath.indexOf('/'));
    }

    private int getWindowId(String httpPath) {
        return Integer.parseInt(httpPath.substring(0, httpPath.indexOf('/')));
    }

    private String getMimeEncoding(File file) {
        MimeEntry me = MimeTable.getDefaultTable().findByFileName(file.getName());
        if (me == null) {
            if (file.getName().endsWith(".css")) return "text/css";
            if (file.getName().endsWith(".js")) return "text/javascript";
            return "text/html";
        }
        return me.getType();
    }
}
