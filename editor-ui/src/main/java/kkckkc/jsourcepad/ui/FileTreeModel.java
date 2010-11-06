package kkckkc.jsourcepad.ui;

import javax.swing.event.EventListenerList;
import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;
import java.io.File;
import java.io.FileFilter;
import java.util.Arrays;
import java.util.Comparator;

public class FileTreeModel implements TreeModel {
    public static final Comparator<File> COMPARATOR = new Comparator<File>() {
        public int compare(File f1, File f2) {
            if (f1.isDirectory() && ! f2.isDirectory()) return -1;
            if (! f1.isDirectory() && f2.isDirectory()) return 1;
            return f1.getName().compareTo(f2.getName());
        }
    };
    private final EventListenerList listeners = new EventListenerList();

    private final File root;
    private final FileFilter filter;

    public FileTreeModel(final File root, final FileFilter filter) {
        this.root = root;
        this.filter = filter;
    }

    public Object getRoot() {
        return root;
    }

    public Object getChild(Object parent, int index) {
        File[] children = getChildren((File) parent);
        if (children == null) return null;
        return children[index];
    }

    public int getChildCount(Object parent) {
        File[] children = getChildren((File) parent);
        if (children == null) return 0;
        return children.length;
    }

    public boolean isLeaf(Object node) {
        return !((File) node).isDirectory();
    }

    public void valueForPathChanged(TreePath path, Object newValue) {}

    public int getIndexOfChild(Object parent, Object child) {
        if (parent == null || child == null) return -1;

        File[] children = getChildren((File) parent);
        if (children == null) return -1;

        for (int i = 0; i < children.length; i++) {
            if (children[i].equals(child)) return i;
        }

        return -1;
    }

    public void addTreeModelListener(TreeModelListener l) {
        listeners.add(TreeModelListener.class, l);
    }

    public void removeTreeModelListener(TreeModelListener l) {
        listeners.remove(TreeModelListener.class, l);
    }


    public void insertFile(final File node) {
        File parent = node.getParentFile();
        int index = getIndexOfChild(parent, node);
        if (index == -1) return;

        TreePath p = createTreePath(parent);
        TreeModelEvent ev = new TreeModelEvent(this, p, new int[] { index }, new File[] { node });
        fireTreeNodesInserted(ev);
    }

    public void removeFile(final File node) {
        File parent = node.getParentFile();
        int index = getIndexOfChild(parent, node);
        if (index == -1) return;

        fireTreeNodesRemoved(
                new TreeModelEvent(this, createTreePath(parent), new int[] { index }, new File[] { node }));
    }

    public void refresh() {
        refresh(root);
    }

    public void refresh(File node) {
        fireTreeStructureChanged(new TreeModelEvent(this, createTreePath(node), null, null));
    }

    private void fireTreeNodesInserted(final TreeModelEvent evt) {
        for (TreeModelListener l : listeners.getListeners(TreeModelListener.class)) {
            l.treeNodesInserted(evt);
        }
    }

    private File[] cacheChildren;
    private File cacheParent;
    private long cacheAccess;

    private File[] getChildren(final File parent) {
        if (cacheChildren != null && cacheParent.equals(parent) && (System.currentTimeMillis() - cacheAccess) < 50) {
            return cacheChildren;
        }

        File[] children = parent.listFiles(filter);
        if (children != null) Arrays.sort(children, COMPARATOR);

        cacheParent = parent;
        cacheChildren = children;
        cacheAccess = System.currentTimeMillis();

        return children;
    }

    private TreePath createTreePath(File node) {
        File[] elements = createPath(node, 1);
        if (elements == null) return null;
        return new TreePath(elements);
    }

    private File[] createPath(final File node, int level) {
        File[] path;
        if (root.equals(node)) {
            path = new File[level];
            path[0] = root;
        } else if (node != null) {
            path = createPath(node.getParentFile(), level + 1);
            if (path != null) path[path.length - level] = node;
        } else {
            path = null;
        }
        return path;
    }

    private void fireTreeNodesRemoved(final TreeModelEvent evt) {
        for (TreeModelListener l : listeners.getListeners(TreeModelListener.class)) {
            l.treeNodesRemoved(evt);
        }
    }

    private void fireTreeStructureChanged(final TreeModelEvent evt) {
        for (TreeModelListener l : listeners.getListeners(TreeModelListener.class)) {
            l.treeStructureChanged(evt);
        }
    }
}
