package kkckkc.jsourcepad.dialog.tmdialog;

import com.google.common.base.Splitter;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import kkckkc.jsourcepad.dialog.Dialog;
import kkckkc.jsourcepad.model.Window;
import kkckkc.utils.plist.NIOXMLPListReader;
import kkckkc.utils.plist.XMLPListWriter;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;

import java.io.IOException;
import java.io.Writer;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class TmDialog implements Dialog, BeanFactoryAware {
    private BeanFactory beanFactory;

    @Override
    public int execute(Window window, Writer out, String stdin, String... args) throws IOException {
        // -p parameters
        // -a asynchronous
        // -c center
        // -m modal
        // -q quite

        // -t
        // -x


        Map plist = null;
        String nib = null;
        for (int i = 0; i < args.length; i++) {
            String arg = args[i];
            if (arg.equals("-x") || arg.equals("-t")) {
                System.err.println("Arrays.asList(args) = " + Arrays.asList(args));
                System.out.println("Unsupported arg " + arg);
                return 1;
            }

            if (arg.startsWith("-")) {
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
            System.err.println("Arrays.asList(args) = " + Arrays.asList(args));
            System.err.println("STDIN = "  + stdin);
            System.err.println("nib = " + nib);
            System.out.println("Dialog not found");
            return 1;
        }

        Object o = delegate.execute(window, plist);
        XMLPListWriter w = new XMLPListWriter();
        w.setPropertyList(o);
        out.write(w.getString());
        out.flush();

        return 0;
    }

    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        this.beanFactory = beanFactory;
    }
}
