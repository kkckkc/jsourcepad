package kkckkc.jsourcepad.bundleeditor.manifest;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;

class TreeEntryTreeModel extends DefaultTreeModel {
    TreeEntryTreeModel(TreeNode root) {
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
