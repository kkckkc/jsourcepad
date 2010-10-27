package kkckkc.jsourcepad.bundleeditor.manifest;

import kkckkc.jsourcepad.util.ui.JTreeUtils;

import javax.swing.tree.DefaultMutableTreeNode;

public class TreeEntryMerger implements JTreeUtils.Merger {
    @Override
    public boolean merge(DefaultMutableTreeNode destination, DefaultMutableTreeNode source) {
        Object uo1 = destination.getUserObject();
        Object uo2 = source.getUserObject();

        if (uo1 instanceof String || uo2 instanceof String) return false;

        TreeEntry te1 = (TreeEntry) uo1;
        TreeEntry te2 = (TreeEntry) uo2;

        if (te1.getValue().equals(te2.getValue())) return false;

        te1.setValue(te2.getValue());
        return true;
    }

    @Override
    public boolean equals(DefaultMutableTreeNode one, DefaultMutableTreeNode two) {
        Object uo1 = one.getUserObject();
        Object uo2 = two.getUserObject();

        if (uo1 instanceof String) return uo1.equals(uo2);

        TreeEntry te1 = (TreeEntry) uo1;
        TreeEntry te2 = (TreeEntry) uo2;

        if ("".equals(te1.getKey()) && "".equals(te2.getKey())) {
            return te1.getValue().equals(te2.getValue());
        }

        return te1.getKey().equals(te2.getKey()); 
    }
}
