package kkckkc.jsourcepad.dialog.tmdialog;

import com.google.common.base.Splitter;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import kkckkc.jsourcepad.dialog.Dialog;
import kkckkc.jsourcepad.model.Window;
import kkckkc.utils.plist.NIOXMLPListReader;
import kkckkc.utils.plist.XMLPListWriter;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;

import java.awt.*;
import java.io.IOException;
import java.io.Writer;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class TmDialog implements Dialog, BeanFactoryAware {
    private BeanFactory beanFactory;
    private Map<Long, TmDialogDelegate> asynchronousWindows = Maps.newHashMap();

    @Override
    public int execute(final Window window, Writer out, String stdin, String... args) throws IOException {
        // -p parameters
        // -a asynchronous
        // -c center
        // -m modal
        // -q quite

        // -t<token> update
        // -x close
        // -w wait   54528


        System.err.println("Arrays.asList(args) = " + Arrays.asList(args));
        System.err.println("STDIN = "  + stdin);
        System.err.println("---------------------------------------------------------------");

        boolean quite = false;
        boolean center = false;
        boolean modal = false;
        boolean async = false;
        Map plist = null;
        String nib = null;
        Long token = null;
        for (int i = 0; i < args.length; i++) {
            String arg = args[i];

            if (arg.startsWith("-")) {
                if (arg.contains("q")) quite = true;
                if (arg.contains("c")) center = true;
                if (arg.contains("m")) modal = true;
                if (arg.contains("a")) async = true;
                if (arg.contains("t")) {
                    try {
                        token = Long.parseLong(arg.substring("-t".length()));
                    } catch (NumberFormatException nfe) {
                        token = Long.parseLong(args[++i]);
                    }
                    final TmDialogDelegate delegate = asynchronousWindows.get(token);
                    if (delegate == null) return 54628;

                    plist = (Map) new NIOXMLPListReader().read(stdin.getBytes());
                    executeDelegate(window, false, false, async, plist, delegate);
                    return 0;
                }
                if (arg.contains("x")) {
                    token = Long.parseLong(args[++i]);
                    TmDialogDelegate delegate = asynchronousWindows.get(token);
                    if (delegate == null) return 54628;
                    delegate.close();
                    asynchronousWindows.remove(token);
                    return 0;
                }
                if (arg.contains("w")) {
                    token = Long.parseLong(args[++i]);
                    TmDialogDelegate delegate = asynchronousWindows.get(token);
                    if (delegate == null) return 54628;
                    Object o = delegate.waitForClose();
                    if (! quite) {
                        XMLPListWriter w = new XMLPListWriter();
                        w.setPropertyList(o);
                        out.write(w.getString());
                        out.flush();
                    }
                    return 0;
                }

                if (arg.endsWith("p")) {
                    plist = (Map) new NIOXMLPListReader().read(args[++i].getBytes());
                }
            } else {
                nib = arg;
            }
        }

        if (plist == null) {
            plist = (Map) new NIOXMLPListReader().read(stdin.getBytes());
        }

        List<String> constituents = Lists.newArrayList();
        Iterables.addAll(constituents, Splitter.on("/").omitEmptyStrings().split(nib));
        constituents = Lists.reverse(constituents);

        TmDialogDelegate delegate = null;
        StringBuilder cur = new StringBuilder();
        for (String s : constituents) {
            cur.insert(0, "/" + s);
            if (beanFactory.containsBean("dialog/tm_dialog" + cur.toString())) {
                delegate = beanFactory.getBean("dialog/tm_dialog" + cur.toString(), TmDialogDelegate.class);
                break;
            }
        }

        if (delegate == null) {
            return 1;
        }

        Object o = executeDelegate(window, center, modal, async, plist, delegate);
        if (! quite && ! async) {
            XMLPListWriter w = new XMLPListWriter();
            w.setPropertyList(o);
            out.write(w.getString());
            out.flush();
        }

        if (async) {
            token = System.currentTimeMillis();
            asynchronousWindows.put(token, delegate);

            out.write(token.toString());
            out.flush();
        }

        return 0;
    }

    private Object executeDelegate(final Window window, final boolean center, final boolean modal, final boolean async, final Map plist, final TmDialogDelegate delegate) {
        class EventQueueRunnable implements Runnable {
            Object ret;

            @Override
            public void run() {
                ret = delegate.execute(window, center, modal, async, plist);
            }
        }

        EventQueueRunnable eqr = new EventQueueRunnable();
        try {
            EventQueue.invokeAndWait(eqr);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } catch (InvocationTargetException e) {
            throw new RuntimeException(e);
        }
        return eqr.ret;
    }

    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        this.beanFactory = beanFactory;
    }
}
