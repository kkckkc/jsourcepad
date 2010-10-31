package kkckkc.jsourcepad.installer.bundle;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import kkckkc.jsourcepad.Dialog;
import kkckkc.jsourcepad.model.Application;
import kkckkc.jsourcepad.model.bundle.BundleManager;
import kkckkc.utils.DomUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.InputSource;

import javax.annotation.PostConstruct;
import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URL;
import java.net.URLConnection;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

public class BundleInstallerDialog implements Dialog<BundleInstallerDialogView> {
    private BundleInstallerDialogView view;
    private List<BundleTableModel.Entry> bundles;

    @PostConstruct
    public void init() {
        view.getInstallButton().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                install();
                close();
            }
        });

        view.getCancelButton().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                close();
            }
        });

        view.getLabel().setText("Getting list of bundles...");



    }

    public void show() {
        final BundleManager bundleManager = Application.get().getBundleManager();
        bundles = Lists.newArrayList();

        try {
            new SwingWorker<Void, Object>() {

                @Override
                protected Void doInBackground() throws Exception {
                    Set<String> preSelectedBundles = Sets.newHashSet();
                    if (bundleManager.getBundles() == null || bundleManager.getBundles().isEmpty()) {
                        preSelectedBundles.addAll(Arrays.asList(
                                "bundle-development", "c", "css", "html", "java", "javascript", "markdown",
                                "php", "perl", "python", "ruby", "sql", "shellscript", "source", "text",
                                "textmate", "xml"));
                    }

                    URL url = new URL("http://github.com/api/v2/xml/repos/show/textmate");
                    final URLConnection conn = url.openConnection();
                    conn.connect();

                    Document document = DomUtil.parse(new InputSource(conn.getInputStream()));
                    for (Element e : DomUtil.getChildren(document.getDocumentElement())) {
                        Element nameE = DomUtil.getChild(e, "name");
                        String name = DomUtil.getText(nameE);

                        if (! name.endsWith(".tmbundle")) continue;

                        bundles.add(new BundleTableModel.Entry(
                                bundleManager.getBundleByDirName(name) != null ||
                                    preSelectedBundles.contains(name.substring(0, name.lastIndexOf("."))),
                                bundleManager.getBundleByDirName(name) != null, name));
                    }

                    return null;
                }

                @Override
                protected void done() {
                    view.setModel(new BundleTableModel(bundles));
                    view.getLabel().setText("Available bundles:");

                    if (bundleManager.getBundles() == null || bundleManager.getBundles().isEmpty()) {
                        JOptionPane.showMessageDialog(view.getJDialog(),
                                "As this is the first time you are installing bundles, a selection \n" +
                                "of common bundles have been pre-selected for you");
                    }
                }

            }.execute();

        } catch (Exception ioe) {
            throw new RuntimeException(ioe);
        }

        view.getJDialog().show();
    }

    @Override
    public void close() {
        view.getJDialog().dispose();
    }

    @Override
    @Autowired
    public void setView(BundleInstallerDialogView view) {
        this.view = view;
    }


    private void install() {
        for (BundleTableModel.Entry entry : bundles) {
            if (entry.isDisabled()) continue;
            if (entry.isSelected()) {
                System.out.println("entry.getName() = " + entry.getName());
            }
        }
    }
}
