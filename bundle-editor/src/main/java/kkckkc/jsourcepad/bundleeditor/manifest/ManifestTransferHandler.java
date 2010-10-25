package kkckkc.jsourcepad.bundleeditor.manifest;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;
import java.awt.datatransfer.Transferable;

public abstract class ManifestTransferHandler extends TransferHandler {


            @Override
            public boolean canImport(TransferSupport transferSupport) {
                return transferSupport.isDataFlavorSupported(BundleTransferable.DATAFLOVOR);
            }


    public Transferable createTransferable(JComponent comp) {
        TreePath selection = ((JTree) comp).getSelectionPath();
        DefaultMutableTreeNode treeNode = (DefaultMutableTreeNode) selection.getLastPathComponent();
        return new BundleTransferable(((TreeEntry) treeNode.getUserObject()).getKey());
    }
    
}
