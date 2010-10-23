package kkckkc.jsourcepad.bundleeditor;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import kkckkc.jsourcepad.bundleeditor.model.BundleDocImpl;
import kkckkc.jsourcepad.model.Application;
import kkckkc.jsourcepad.model.bundle.Bundle;
import kkckkc.jsourcepad.model.bundle.BundleItemSupplier;
import kkckkc.jsourcepad.model.bundle.BundleStructure;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class ManifestBundleDocPresenter extends BasicBundleDocPresenter {

    @Override
    protected void saveCallback() {
        super.saveCallback();
    }

    @Override
    public void init() {
        super.init();

        ManifestBundleDocViewImpl mView = (ManifestBundleDocViewImpl) view;
        BundleDocImpl bDoc = (BundleDocImpl) doc;

        Bundle bundle = findBundle(bDoc.getName());

        JTree menu = mView.getMenu();

        DefaultMutableTreeNode root = new DefaultMutableTreeNode("Menu Structure");
        DefaultTreeModel menuModel = new DefaultTreeModel(root);

        menu.setModel(menuModel);

        buildMenu(root, (Map) bDoc.getPlist().get("mainMenu"), bundle.getItemsByUuid());

        TreePath tp = new TreePath(new Object[] { root });
        menu.expandPath(tp);



        JTree available = mView.getAvailable();

        root = new DefaultMutableTreeNode("Available Items");
        menuModel = new DefaultTreeModel(root);
        available.setModel(menuModel);

        buildAvailable(root, bundle);

        tp = new TreePath(new Object[] { root });
        available.expandPath(tp);
    }

    private void buildAvailable(DefaultMutableTreeNode root, Bundle bundle) {
        root.add(new DefaultMutableTreeNode("New Submenu"));
        root.add(new DefaultMutableTreeNode("------------------------------------"));

        Multimap<BundleStructure.Type, BundleItemSupplier> entries = HashMultimap.create();

        for (Map.Entry<String, BundleItemSupplier> entry : bundle.getItemsByUuid().entrySet()) {
            BundleItemSupplier bis = entry.getValue();

            entries.put(bis.getType(), bis);
        }


        for (BundleStructure.Type type : Arrays.asList(BundleStructure.Type.COMMAND, BundleStructure.Type.MACRO, BundleStructure.Type.SNIPPET)) {
            if (! entries.containsKey(type)) continue;

            List<String> l = Lists.newArrayList();
            for (BundleItemSupplier bis : entries.get(type)) {
                l.add(bis.getName());
            }

            Collections.sort(l);

            DefaultMutableTreeNode lev2 = new DefaultMutableTreeNode(type.getFolder());
            root.add(lev2);
            for (String s : l) {
                lev2.add(new DefaultMutableTreeNode(s));
            }
        }
    }

    private Bundle findBundle(String name) {
        for (Map.Entry<String, List<Bundle>> entry : Application.get().getBundleManager().getBundles().entrySet()) {
            for (Bundle b : entry.getValue()) {
                if (b.getName().equals(name)) return b;
            }
        }
        return null;
    }

    private void buildMenu(DefaultMutableTreeNode parent, Map map, Map<String, BundleItemSupplier> itemsByUuid) {
        Map<String, Map> submenus = (Map<String, Map>) map.get("submenus");
        if (submenus == null) submenus = Maps.newHashMap();

        List<String> items = (List<String>) map.get("items");
        for (String s : items) {
            if (submenus.containsKey(s)) {
                Map sm = submenus.get(s);
                DefaultMutableTreeNode newParent = new DefaultMutableTreeNode(sm.get("name"));
                parent.add(newParent);
                buildMenu(newParent, sm, itemsByUuid);
            } else if (s.startsWith("----")) {
                parent.add(new DefaultMutableTreeNode(s));
            } else {
                parent.add(new DefaultMutableTreeNode(itemsByUuid.get(s).getName()));
            }
        }
    }
}
