package kkckkc.jsourcepad.bundleeditor;

import com.google.common.collect.Maps;
import kkckkc.jsourcepad.bundleeditor.model.BundleDocImpl;
import kkckkc.jsourcepad.model.Application;
import kkckkc.jsourcepad.model.bundle.Bundle;
import kkckkc.jsourcepad.model.bundle.BundleItemSupplier;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
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

        DefaultMutableTreeNode root = new DefaultMutableTreeNode("Menu");
        DefaultTreeModel menuModel = new DefaultTreeModel(root);

        menu.setModel(menuModel);

        buildMenu(root, (Map) bDoc.getPlist().get("mainMenu"), bundle.getItemsByUuid());

        TreePath tp = new TreePath(new Object[] { root });
        menu.expandPath(tp);
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
