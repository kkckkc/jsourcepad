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
    private static final int ERROR_NOT_FOUND = 54628;
    private static final int ERROR_GENERAL = 1;

    private BeanFactory beanFactory;
    private Map<Long, TmDialogDelegate> asynchronousWindows = Maps.newHashMap();

    @Override
    public int execute(final Window window, Writer out, String pwd, String stdin, String... args) throws IOException {

        /*
        System.out.println("Arrays.asList(args) = " + Arrays.asList(args));
        System.out.println("STDIN = "  + stdin);
        System.out.println("---------------------------------------------------------------");
        */

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
                    if (delegate == null) return ERROR_NOT_FOUND;

                    plist = (Map) new NIOXMLPListReader().read(stdin.getBytes());
                    delegateLoad(plist, false, delegate);
                    return 0;
                }
                if (arg.contains("x")) {
                    token = Long.parseLong(args[++i]);
                    TmDialogDelegate delegate = asynchronousWindows.get(token);
                    if (delegate == null) return ERROR_NOT_FOUND;
                    delegate.close();
                    asynchronousWindows.remove(token);
                    return 0;
                }
                if (arg.contains("w")) {
                    token = Long.parseLong(args[++i]);
                    TmDialogDelegate delegate = asynchronousWindows.get(token);
                    if (delegate == null) return ERROR_NOT_FOUND;
                    Object o = delegate.waitForData();
                    if (o == null) return ERROR_GENERAL;

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

        if (! nib.startsWith("/")) {
            nib = pwd + "/" + nib;
        }

        List<String> constituents = Lists.newArrayList();
        Iterables.addAll(constituents, Splitter.on("/").omitEmptyStrings().split(nib));
        constituents = Lists.reverse(constituents);

        TmDialogDelegate delegate = null;
        StringBuilder cur = new StringBuilder();
        for (String s : constituents) {
            cur.insert(0, "/" + s);
            if (beanFactory.containsBean("dialog/tm_dialog" + cur.toString().replace(' ', '_'))) {
                delegate = beanFactory.getBean("dialog/tm_dialog" + cur.toString().replace(' ', '_'), TmDialogDelegate.class);
                break;
            }
        }

        if (delegate == null) {
            System.out.println("Dialog not found");
            System.out.println("Arrays.asList(args) = " + Arrays.asList(args));
            System.out.println("STDIN = "  + stdin);
            System.out.println("---------------------------------------------------------------");
            return ERROR_NOT_FOUND;
        }

        delegateOpen(window, center, modal, async, delegate);
        delegateLoad(plist, true, delegate);
        delegateShow(delegate);

        if (async) {
            token = System.currentTimeMillis();
            asynchronousWindows.put(token, delegate);

            out.write(token.toString());
            out.flush();
        } else {
            Object o = delegate.waitForData();
            if (o == null) return ERROR_GENERAL;

            if (! quite) {
                XMLPListWriter w = new XMLPListWriter();
                w.setPropertyList(o);
                out.write(w.getString());
                out.flush();
            }
        }


        return 0;
    }

    private void delegateShow(final TmDialogDelegate delegate) {
        try {
            EventQueue.invokeAndWait(new Runnable() {
                @Override
                public void run() {
                    delegate.show();
                }
            });
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } catch (InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    private void delegateOpen(final Window window, final boolean center, final boolean modal, final boolean async, final TmDialogDelegate delegate) {
        try {
            EventQueue.invokeAndWait(new Runnable() {
                @Override
                public void run() {
                    delegate.open(window, center, modal, async);
                }
            });
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } catch (InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    private void delegateLoad(final Map object, final boolean isFirstTime, final TmDialogDelegate delegate) {
        try {
            EventQueue.invokeAndWait(new Runnable() {
                @Override
                public void run() {
                    delegate.load(isFirstTime, object);
                }
            });
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } catch (InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        this.beanFactory = beanFactory;
    }
}
