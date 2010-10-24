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
import javax.swing.tree.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;
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

        final Bundle bundle = findBundle(bDoc.getName());

        final JTree menu = mView.getMenu();

        DefaultMutableTreeNode root = new DefaultMutableTreeNode("Menu Structure");
        final DefaultTreeModel menuModel = new MenuTreeModel(root);

        menu.setModel(menuModel);

        buildMenu(root, (Map) bDoc.getPlist().get("mainMenu"), bundle.getItemsByUuid());

        TreePath tp = new TreePath(new Object[] { root });
        menu.expandPath(tp);

        menu.setDragEnabled(true);

        menu.setDropMode(DropMode.INSERT);
        menu.setTransferHandler(new TransferHandler() {
            @Override
            public boolean importData(TransferSupport transferSupport) {
                try {
                    DefaultMutableTreeNode node = (DefaultMutableTreeNode) ((JTree.DropLocation)transferSupport.getDropLocation()).getPath().getLastPathComponent();

                    Object data = transferSupport.getTransferable().getTransferData(DataFlavor.stringFlavor);

                    boolean isFolder = false;
                    if ("".equals(data)) {
                        data = new TreeEntry(UUID.randomUUID().toString().toUpperCase(), "New Submenu", true);
                        isFolder = true;
                    } else if (! ((String) data).startsWith("---")) {
                        data = new TreeEntry((String) data, (String) bundle.getItemsByUuid().get(data).getName(), false);
                    }
                    
                    node.insert(
                            new DefaultMutableTreeNode(data, ! isFolder),
                            Math.max(0, ((JTree.DropLocation)transferSupport.getDropLocation()).getChildIndex()));

                    menuModel.nodesWereInserted(node, new int[]{Math.max(0, ((JTree.DropLocation)transferSupport.getDropLocation()).getChildIndex())});

                    return true;
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }

            @Override
            public boolean canImport(TransferSupport transferSupport) {
                return true;
            }


            @Override
            protected void exportDone(JComponent source, Transferable data, int action) {
                if (action == MOVE) {
                    try {
                        String s = (String) data.getTransferData(DataFlavor.stringFlavor);
                        DefaultMutableTreeNode node = (DefaultMutableTreeNode) ((JTree) source).getSelectionPath().getLastPathComponent();
                        DefaultMutableTreeNode parent = (DefaultMutableTreeNode) node.getParent();
                        int idx = parent.getIndex(node);
                        parent.remove(node);
                        menuModel.nodesWereRemoved(parent, new int[] { idx }, new Object[] { node });
                    } catch (UnsupportedFlavorException e) {
                        throw new RuntimeException(e);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }

                super.exportDone(source, data, action);
            }

            public int getSourceActions(JComponent comp) {
                TreePath selection = ((JTree) comp).getSelectionPath();
                if (selection != null) {
                    DefaultMutableTreeNode treeNode = (DefaultMutableTreeNode) selection.getLastPathComponent();
                    if (treeNode.getUserObject() instanceof String) return NONE;

                    return MOVE;
                }

                return MOVE;
            }

            public Transferable createTransferable(JComponent comp) {
                TreePath selection = ((JTree) comp).getSelectionPath();
                DefaultMutableTreeNode treeNode = (DefaultMutableTreeNode) selection.getLastPathComponent();
                return new StringSelection(((TreeEntry) treeNode.getUserObject()).getKey());
            }
        });



        JTree available = mView.getAvailable();

        root = new DefaultMutableTreeNode("Available Items");
        final DefaultTreeModel availableModel = new MenuTreeModel(root);
        available.setModel(availableModel);

        buildAvailable(root, bundle);

        tp = new TreePath(new Object[] { root });
        available.expandPath(tp);

        available.setDragEnabled(true);
        available.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);        
        available.setTransferHandler(new TransferHandler() {
           
            @Override
            protected void exportDone(JComponent source, Transferable data, int action) {
                if (action == MOVE) {
                    try {
                        String s = (String) data.getTransferData(DataFlavor.stringFlavor);
                        DefaultMutableTreeNode node = (DefaultMutableTreeNode) ((JTree) source).getSelectionPath().getLastPathComponent();
                        DefaultMutableTreeNode parent = (DefaultMutableTreeNode) node.getParent();
                        int idx = parent.getIndex(node);
                        parent.remove(node);
                        availableModel.nodesWereRemoved(parent, new int[] { idx }, new Object[] { node });
                    } catch (UnsupportedFlavorException e) {
                        throw new RuntimeException(e);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }

                super.exportDone(source, data, action);
            }

            public int getSourceActions(JComponent comp) {
                TreePath selection = ((JTree) comp).getSelectionPath();
                if (selection != null) {
                    DefaultMutableTreeNode treeNode = (DefaultMutableTreeNode) selection.getLastPathComponent();
                    if (treeNode.getUserObject() instanceof String) return NONE;

                    TreeEntry te = (TreeEntry) treeNode.getUserObject();
                    if ("".equals(te.getKey()) || te.getKey().startsWith("---")) return COPY;
                }

                return MOVE;
            }

            public Transferable createTransferable(JComponent comp) {
                TreePath selection = ((JTree) comp).getSelectionPath();
                DefaultMutableTreeNode treeNode = (DefaultMutableTreeNode) selection.getLastPathComponent();
                return new StringSelection(((TreeEntry) treeNode.getUserObject()).getKey());
            }
        });
    }

    private void buildAvailable(DefaultMutableTreeNode root, Bundle bundle) {
        root.add(new DefaultMutableTreeNode(new TreeEntry("", "New Submenu", true)));
        root.add(new DefaultMutableTreeNode(new TreeEntry("------------------------------------", "------------------------------------", false)));

        Multimap<BundleStructure.Type, BundleItemSupplier> entries = HashMultimap.create();

        for (Map.Entry<String, BundleItemSupplier> entry : bundle.getItemsByUuid().entrySet()) {
            BundleItemSupplier bis = entry.getValue();

            entries.put(bis.getType(), bis);
        }


        for (BundleStructure.Type type : Arrays.asList(BundleStructure.Type.COMMAND, BundleStructure.Type.MACRO, BundleStructure.Type.SNIPPET)) {
            if (! entries.containsKey(type)) continue;

            List<TreeEntry> l = Lists.newArrayList();
            for (BundleItemSupplier bis : entries.get(type)) {
                l.add(new TreeEntry(bis.getUUID(), bis.getName(), false));
            }

            Collections.sort(l, new Comparator<TreeEntry>() {
                @Override
                public int compare(TreeEntry o1, TreeEntry o2) {
                    return o1.getValue().compareTo(o2.getValue());
                }
            });

            DefaultMutableTreeNode lev2 = new DefaultMutableTreeNode(type.getFolder());
            root.add(lev2);
            for (TreeEntry s : l) {
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

    private static class MenuTreeModel extends DefaultTreeModel {
        private MenuTreeModel(TreeNode root) {
            super(root);
        }

        @Override
        public boolean isLeaf(Object node) {
            DefaultMutableTreeNode dmtn = (DefaultMutableTreeNode) node;
            if (dmtn.getUserObject() instanceof TreeEntry) {
                TreeEntry te = (TreeEntry) dmtn.getUserObject();
                return ! te.isFolder();
            }
            return super.isLeaf(node);
        }
    }

    private static class TreeEntry {
        private String key;
        private String value;
        private boolean folder;

        private TreeEntry(String key, String value, boolean folder) {
            this.key = key;
            this.value = value;
            this.folder = folder;
        }

        public boolean isFolder() {
            return folder;
        }

        public String getKey() {
            return key;
        }

        public String getValue() {
            return value;
        }

        public String toString() {
            return value;
        }
    }
}
