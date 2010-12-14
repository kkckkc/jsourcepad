package kkckkc.jsourcepad.bundleeditor.manifest;

import kkckkc.jsourcepad.model.bundle.Bundle;
import kkckkc.jsourcepad.model.bundle.BundleItemSupplier;
import kkckkc.jsourcepad.util.ui.JTreeUtils;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import java.awt.datatransfer.Transferable;
import java.util.Comparator;

class ManifestAvailableTransferHandler extends ManifestTransferHandler {
    private final DefaultTreeModel availableModel;
    private final Bundle bundle;

    public ManifestAvailableTransferHandler(DefaultTreeModel availableModel, Bundle bundle) {
        this.availableModel = availableModel;
        this.bundle = bundle;
    }

    @Override
    protected void exportDone(JComponent source, Transferable data, int action) {
        if (action == MOVE) {
            DefaultMutableTreeNode node = (DefaultMutableTreeNode) ((JTree) source).getSelectionPath().getLastPathComponent();
            DefaultMutableTreeNode parent = (DefaultMutableTreeNode) node.getParent();
            int idx = parent.getIndex(node);
            parent.remove(node);
            availableModel.nodesWereRemoved(parent, new int[] { idx }, new Object[] { node });
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


    @Override
    public boolean importData(TransferSupport support) {
        try {
            String data = (String) support.getTransferable().getTransferData(BundleTransferable.DATAFLOVOR);

            if (data.startsWith("---")) return true;

            BundleItemSupplier bis = bundle.getItemsByUuid().get(data);
            if (bis == null) return true;

            String folder = bis.getType().getFolder();
            DefaultMutableTreeNode destination = null;
            DefaultMutableTreeNode root = (DefaultMutableTreeNode) availableModel.getRoot();
            for (int i = 0; i < root.getChildCount(); i++) {
                DefaultMutableTreeNode node = (DefaultMutableTreeNode) root.getChildAt(i);
                if (node.getUserObject().toString().equals(folder)) {
                    destination = node;
                    break;
                }
            }

            if (destination == null) {
                destination = new DefaultMutableTreeNode(new TreeEntry("", folder, true));
                final TreeEntry.TreeEntryComparator comparator = new TreeEntry.TreeEntryComparator();
                JTreeUtils.insertIntoSortedTree(availableModel, destination, root, new Comparator<TreeEntry>() {
                    @Override
                    public int compare(TreeEntry o1, TreeEntry o2) {
                        if (o2.getKey().startsWith("----")) return 1;
                        if (o2.getValue().equals("New Submenu")) return 1;
                        return comparator.compare(o1, o2);  
                    }
                });
            }

            DefaultMutableTreeNode newNode = new DefaultMutableTreeNode(new TreeEntry(data, bis.getName(), false));
            JTreeUtils.insertIntoSortedTree(availableModel, newNode, destination, new TreeEntry.TreeEntryComparator());

            return true;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
