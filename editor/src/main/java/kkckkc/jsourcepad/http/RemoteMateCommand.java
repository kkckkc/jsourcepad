package kkckkc.jsourcepad.http;

import com.google.common.collect.Lists;
import kkckkc.jsourcepad.command.global.OpenCommand;
import kkckkc.jsourcepad.model.Application;
import kkckkc.jsourcepad.model.Window;
import kkckkc.jsourcepad.model.WindowManager;
import kkckkc.jsourcepad.util.messagebus.DispatchStrategy;
import kkckkc.jsourcepad.util.messagebus.Subscription;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.concurrent.CountDownLatch;

public class RemoteMateCommand implements RemoteControl.Command {
    public static final String ID = "mate";

    @Override
    public String getId() {
        return ID;
    }

    @Override
    public void execute(HttpServletRequest req, HttpServletResponse resp) {
        resp.setStatus(HttpServletResponse.SC_NO_CONTENT);

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
        } else if (args.isEmpty()) {
            OpenCommand openCommand = new OpenCommand();
            openCommand.setContents(req.getParameter("__STDIN__"));
            openCommand.setOpenInSeparateWindow(true);
            Application.get().getCommandExecutor().execute(openCommand);
        } else {
            Application.get().getCommandExecutor().execute(new OpenCommand(args.get(args.size() - 1), false));
        }
    }
}
