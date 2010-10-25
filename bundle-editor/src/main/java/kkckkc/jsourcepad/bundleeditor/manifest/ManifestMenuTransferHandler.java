package kkckkc.jsourcepad.bundleeditor.manifest;

import kkckkc.jsourcepad.model.bundle.Bundle;
import kkckkc.jsourcepad.model.bundle.BundleItemSupplier;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;
import java.util.UUID;

class ManifestMenuTransferHandler extends ManifestTransferHandler {
    private final DefaultTreeModel menuModel;
    private final Bundle bundle;

    public ManifestMenuTransferHandler(DefaultTreeModel menuModel, Bundle bundle) {
        this.menuModel = menuModel;
        this.bundle = bundle;
    }

    private DefaultMutableTreeNode findNodeByUuid(String uuid, DefaultMutableTreeNode parent) {
        Object o = parent.getUserObject();
        if (o instanceof TreeEntry && uuid.equals(((TreeEntry) o).getKey())) return parent;

        for (int i = 0; i < parent.getChildCount(); i++) {
            DefaultMutableTreeNode obj = findNodeByUuid(uuid, (DefaultMutableTreeNode) parent.getChildAt(i));
            if (obj != null) return obj;
        }

        return null;
    }

    @Override
    public boolean importData(TransferSupport transferSupport) {
        try {
            DefaultMutableTreeNode node = (DefaultMutableTreeNode) ((JTree.DropLocation)transferSupport.getDropLocation()).getPath().getLastPathComponent();

            Object data = transferSupport.getTransferable().getTransferData(BundleTransferable.DATAFLOVOR);

            DefaultMutableTreeNode newNode;
            if ("".equals(data)) {
                newNode = new DefaultMutableTreeNode(
                        new TreeEntry(UUID.randomUUID().toString().toUpperCase(), "New Submenu", true),
                        true);

            } else if (! ((String) data).startsWith("---")) {
                String uuid = (String) data;

                newNode = findNodeByUuid(uuid, (DefaultMutableTreeNode) menuModel.getRoot());

                if (newNode != null) {

                    DefaultMutableTreeNode parent = (DefaultMutableTreeNode) newNode.getParent();
                    int idx = parent.getIndex(newNode);
                    parent.remove(newNode);
                    menuModel.nodesWereRemoved(parent, new int[] { idx }, new Object[] { newNode });

                } else {
                    BundleItemSupplier bis = bundle.getItemsByUuid().get(uuid);
                    newNode = new DefaultMutableTreeNode(
                            new TreeEntry((String) data, bis.getName(), false),
                            true);
                }

            } else {
                newNode = new DefaultMutableTreeNode(
                        new TreeEntry((String) data, (String) data, false), true);

            }

            node.insert(
                    newNode,
                    Math.max(0, ((JTree.DropLocation)transferSupport.getDropLocation()).getChildIndex()));

            menuModel.nodesWereInserted(node, new int[]{Math.max(0, ((JTree.DropLocation)transferSupport.getDropLocation()).getChildIndex())});

            return true;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected void exportDone(JComponent source, Transferable data, int action) {
        if (action == MOVE) {
            try {
                String s = (String) data.getTransferData(BundleTransferable.DATAFLOVOR);
                if (((JTree) source).getSelectionPath() == null) return;

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
        }

        return MOVE;
    }
}
