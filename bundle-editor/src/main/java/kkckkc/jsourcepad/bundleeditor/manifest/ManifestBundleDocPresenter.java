package kkckkc.jsourcepad.bundleeditor.manifest;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import com.google.common.collect.Sets;
import kkckkc.jsourcepad.bundleeditor.BasicBundleDocPresenter;
import kkckkc.jsourcepad.bundleeditor.model.BundleDocImpl;
import kkckkc.jsourcepad.model.Application;
import kkckkc.jsourcepad.model.bundle.Bundle;
import kkckkc.jsourcepad.model.bundle.BundleItemSupplier;
import kkckkc.jsourcepad.model.bundle.BundleStructure;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.*;

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

        final Bundle bundle = Application.get().getBundleManager().getBundle(bDoc.getName());

        DefaultTreeModel menuModel = initMenu(mView, bDoc, bundle);
        initAvailable(mView, menuModel, bundle);
    }

    private void initAvailable(ManifestBundleDocViewImpl mView, DefaultTreeModel menuModel, Bundle bundle) {
        JTree available = mView.getAvailable();

        DefaultMutableTreeNode root = new DefaultMutableTreeNode("Available Items");
        DefaultTreeModel availableModel = new TreeEntryTreeModel(root);
        available.setModel(availableModel);
        Set<String> usedUuids = Sets.newHashSet();
        addAllUuids(usedUuids, (DefaultMutableTreeNode) menuModel.getRoot());
        buildAvailable(root, bundle, usedUuids);
        available.expandPath(new TreePath(new Object[] { root }));
        available.setDragEnabled(true);
        available.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
        available.setTransferHandler(new ManifestAvailableTransferHandler(availableModel, bundle));
    }

    private DefaultTreeModel initMenu(ManifestBundleDocViewImpl mView, BundleDocImpl bDoc, Bundle bundle) {
        final JTree menu = mView.getMenu();
        menu.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2 && menu.getSelectionPath() != null) {
                    // TODO: Implement renaming
                    JOptionPane.showMessageDialog(menu, "Lorem ipsum");
                } else {
                    super.mouseClicked(e);
                }
            }
        });
        DefaultMutableTreeNode root = new DefaultMutableTreeNode("Menu Structure");
        DefaultTreeModel menuModel = new TreeEntryTreeModel(root);
        menu.setModel(menuModel);
        menu.setDragEnabled(true);
        menu.setDropMode(DropMode.INSERT);
        menu.setTransferHandler(new ManifestMenuTransferHandler(menuModel, bundle));

        buildMenu(root, (Map) bDoc.getPlist().get("mainMenu"), bundle.getItemsByUuid());
        menu.expandPath(new TreePath(new Object[] { root }));
        return menuModel;
    }


    private void addAllUuids(Set<String> dest, DefaultMutableTreeNode parent) {
        Object o = parent.getUserObject();
        if (o instanceof TreeEntry) {
            dest.add(((TreeEntry) o).getKey());
        }

        for (int i = 0; i < parent.getChildCount(); i++) {
            addAllUuids(dest, (DefaultMutableTreeNode) parent.getChildAt(i));
        }
    }

    private void buildAvailable(DefaultMutableTreeNode root, Bundle bundle, Set<String> usedUuids) {
        root.add(new DefaultMutableTreeNode(new TreeEntry("", "New Submenu", true)));
        root.add(new DefaultMutableTreeNode(new TreeEntry("------------------------------------", "------------------------------------", false)));

        Multimap<BundleStructure.Type, TreeEntry> entries = HashMultimap.create();
        for (Map.Entry<String, BundleItemSupplier> entry : bundle.getItemsByUuid().entrySet()) {
            BundleItemSupplier bis = entry.getValue();
            if (! usedUuids.contains(bis.getUUID()))
                entries.put(bis.getType(), new TreeEntry(bis.getUUID(), bis.getName(), false));
        }

        for (BundleStructure.Type type : Arrays.asList(BundleStructure.Type.COMMAND, BundleStructure.Type.MACRO, BundleStructure.Type.SNIPPET)) {
            if (! entries.containsKey(type)) continue;

            List<TreeEntry> l = new ArrayList<TreeEntry>(entries.get(type));
            Collections.sort(l, new TreeEntry.TreeEntryComparator());

            DefaultMutableTreeNode typeNode = new DefaultMutableTreeNode(new TreeEntry("", type.getFolder(), true));
            root.add(typeNode);
            for (TreeEntry s : l) {
                typeNode.add(new DefaultMutableTreeNode(s));
            }
        }
    }

    private void buildMenu(DefaultMutableTreeNode parent, Map map, Map<String, BundleItemSupplier> itemsByUuid) {
        Map<String, Map> submenus = (Map<String, Map>) map.get("submenus");
        if (submenus == null) submenus = Maps.newHashMap();

        List<String> items = (List<String>) map.get("items");
        for (String s : items) {
            if (submenus.containsKey(s)) {
                Map sm = submenus.get(s);
                DefaultMutableTreeNode newParent = new DefaultMutableTreeNode(new TreeEntry(s, (String) sm.get("name"), true), true);
                parent.add(newParent);
                buildMenu(newParent, sm, itemsByUuid);
            } else if (s.startsWith("----")) {
                parent.add(new DefaultMutableTreeNode(new TreeEntry(s, s, false), false));
            } else {
                parent.add(new DefaultMutableTreeNode(new TreeEntry(s, itemsByUuid.get(s).getName(), false), false));
            }
        }
    }


}
