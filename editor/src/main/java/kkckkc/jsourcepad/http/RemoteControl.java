package kkckkc.jsourcepad.http;

import com.google.common.io.CharStreams;
import kkckkc.jsourcepad.util.Config;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.*;
import java.util.ArrayList;
import java.util.List;

public class RemoteControl {
    private static List<Command> commands = new ArrayList<Command>();
    static {
        commands.add(new RemoteExecCommand());
        commands.add(new RemoteMateCommand());
        commands.add(new RemoteOpenCommand());
        commands.add(new RemoteRefreshCommand());
    }

    private static final int DEFAULT_BACKLOG = 50;

    public boolean isApplicationRunning() {
        boolean portTaken = false;
        ServerSocket socket = null;
        try {
            socket = new ServerSocket(Config.getHttpPort(), DEFAULT_BACKLOG, InetAddress.getByName(Config.getLocalhost()));
        } catch (IOException e) {
            portTaken = true;
        } finally {
            // Clean up
            if (socket != null) try {
                socket.close();
            } catch (IOException e) {
                // Ignore
            }
        }

        return portTaken;
    }

    public void open(String file) {
        try {
            URL url = RemoteOpenCommand.createRequestForFile(file);
            URLConnection conn = url.openConnection();
            conn.connect();

            // Get the response
            Reader rd = new InputStreamReader(conn.getInputStream());
            CharStreams.toString(rd);
            rd.close();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static Command getCommand(String id) {
        for (Command c : commands) {
            if (c.getId().equals(id)) {
                return c;
            }
        }

        return null;
    }

    static URL buildUrl(String cmd, String parameterString) throws MalformedURLException {
        return new URL("http://localhost:" + Config.getHttpPort() + "/cmd/" + cmd + "?" + parameterString);
    }


    public interface Command {
        public String getId();
        public void execute(HttpServletRequest req, HttpServletResponse resp);
    }
}
