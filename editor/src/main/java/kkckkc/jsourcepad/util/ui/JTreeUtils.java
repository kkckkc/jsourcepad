package kkckkc.jsourcepad.util.ui;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

public class JTreeUtils {

    public static void insertIntoSortedTree(DefaultTreeModel model, DefaultMutableTreeNode child, DefaultMutableTreeNode parent, Comparator comparator) {
        Object userObject = child.getUserObject();
        int i = 0;
        for (i = 0; i < parent.getChildCount(); i++) {
            Object userObjectToCompareWith = ((DefaultMutableTreeNode) parent.getChildAt(i)).getUserObject();
            if (comparator.compare(userObject, userObjectToCompareWith) < 0) {
                break;
            }
        }

        model.insertNodeInto(child, parent, i);
    }


    public static void mergeModels(DefaultTreeModel destination, DefaultTreeModel source, Merger merger) {
        DefaultMutableTreeNode rootDestination = (DefaultMutableTreeNode) destination.getRoot();
        DefaultMutableTreeNode rootSource = (DefaultMutableTreeNode) source.getRoot();

        if (! merger.equals(rootDestination, rootSource)) {
            destination.setRoot(rootSource.getRoot());
        } else {
            merger.merge(rootDestination, rootSource);
            mergeNodes(destination, rootDestination, rootSource, merger);
        }
    }

    private static void mergeNodes(DefaultTreeModel model, DefaultMutableTreeNode dest, DefaultMutableTreeNode source, Merger merger) {
        Map<DefaultMutableTreeNode, Boolean> nodesToRemain = Maps.newIdentityHashMap();

        // Merge in equal nodes
        List<DefaultMutableTreeNode> sourceNodes = Lists.newArrayList();
        for (int i = 0; i < source.getChildCount(); i++) {
            DefaultMutableTreeNode sn = (DefaultMutableTreeNode) source.getChildAt(i);
            sourceNodes.add(sn);
        }

        int i = 0;
        for (DefaultMutableTreeNode sn : sourceNodes) {
            boolean found = false;
            for (int j = 0; j < dest.getChildCount(); j++) {
                DefaultMutableTreeNode dn = (DefaultMutableTreeNode) dest.getChildAt(j);
                if (merger.equals(dn, sn)) {
                    boolean change = merger.merge(dn, sn);
                    nodesToRemain.put(dn, Boolean.TRUE);
                    found = true;

                    if (change) {
                        model.nodeChanged(dn);
                    }

                    mergeNodes(model, dn, sn, merger);

                    break;
                }
            }

            if (! found) {
                nodesToRemain.put(sn, Boolean.TRUE);
                model.insertNodeInto(sn, dest, i);
            }

            i++;
        }

        // Remove nodes
        List<DefaultMutableTreeNode> nodesToBeRemoved = Lists.newArrayList();
        for (int j = 0; j < dest.getChildCount(); j++) {
            DefaultMutableTreeNode dn = (DefaultMutableTreeNode) dest.getChildAt(j);
            if (! nodesToRemain.containsKey(dn)) {
                nodesToBeRemoved.add(dn);
            }
        }

        for (DefaultMutableTreeNode n : nodesToBeRemoved) {
            model.removeNodeFromParent(n);
        }
    }


    public interface Merger {
        public boolean merge(DefaultMutableTreeNode destination, DefaultMutableTreeNode source);
        public boolean equals(DefaultMutableTreeNode one, DefaultMutableTreeNode two);
    }

}
