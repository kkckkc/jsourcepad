package kkckkc.utils.swing;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import java.util.Comparator;

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

}
