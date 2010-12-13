package kkckkc.jsourcepad.ui;

import com.google.common.base.Objects;
import com.google.common.collect.Lists;
import com.google.common.collect.MapMaker;
import kkckkc.utils.Os;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.EventListenerList;
import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;
import java.awt.*;
import java.io.File;
import java.io.FileFilter;
import java.util.*;
import java.util.List;

public class FileTreeModel implements TreeModel {
    public static final Comparator<File> COMPARATOR = new Comparator<File>() {
        public int compare(File f1, File f2) {
            if (f1.isDirectory() && ! f2.isDirectory()) return -1;
            if (! f1.isDirectory() && f2.isDirectory()) return 1;
            return f1.getName().compareTo(f2.getName());
        }
    };
    private final EventListenerList listeners = new EventListenerList();

    private final Node root;
    private final FileFilter filter;
    private List<? extends Decorator> decorators = Lists.newArrayList();

    private Map<Node, Node[]> expandedNodes =
            new MapMaker().concurrencyLevel(2).makeMap();

    public FileTreeModel(final File root, final FileFilter filter, List<? extends Decorator> decorators) {
        this.root = new Node(root);
        this.filter = filter;

        this.decorators = Objects.firstNonNull(decorators, Collections.<Decorator>emptyList());
    }

    public Object getRoot() {
        return root;
    }

    public Object getChild(Object parent, int index) {
        Node[] children = getChildren((Node) parent);
        if (children == null) return null;
        return children[index];
    }

    public int getChildCount(Object parent) {
        Node[] children = getChildren((Node) parent);
        if (children == null) return 0;
        return children.length;
    }

    public boolean isLeaf(Object node) {
        return !((Node) node).getFile().isDirectory();
    }

    public void valueForPathChanged(TreePath path, Object newValue) {}

    public int getIndexOfChild(Object parent, Object child) {
        if (parent == null || child == null) return -1;

        Node[] children = getChildren((Node) parent);
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

    public void refresh() {
        refresh(root.getFile());
    }

    public void refresh(File node) {
        expandedNodes.clear();
        fireTreeStructureChanged(createTreePath(node));
    }

    private Node[] getChildren(final Node parent) {
        if (expandedNodes.containsKey(parent))
            return expandedNodes.get(parent);

        File[] children = parent.getFile().listFiles(filter);
        if (children != null) Arrays.sort(children, COMPARATOR);

        final Node[] nodes = new Node[children.length];
        for (int i = 0; i < children.length; i++) {
            nodes[i] = new Node(children[i]);
        }

        for (Decorator d : decorators) {
            d.getDecoration(parent, nodes, new Runnable() {
                @Override
                public void run() {
                    fireNodesChanged(parent, nodes);
                }
            });
        }

        expandedNodes.put(parent, nodes);

        return nodes;
    }

    private TreePath createTreePath(File file) {
        Node[] elements = createPath(file, 1);
        if (elements == null) return null;
        return new TreePath(elements);
    }

    private Node[] createPath(final File file, int level) {
        Node[] path;
        if (root.getFile().equals(file)) {
            path = new Node[level];
            path[0] = root;
        } else if (file != null) {
            path = createPath(file.getParentFile(), level + 1);
            if (path != null) path[path.length - level] = new Node(file);
        } else {
            path = null;
        }
        return path;
    }

    private void fireTreeStructureChanged(TreePath path) {
        TreeModelEvent evt = new TreeModelEvent(this, path, null, null);
        for (TreeModelListener l : listeners.getListeners(TreeModelListener.class)) {
            l.treeStructureChanged(evt);
        }
    }

    private void fireNodesChanged(Node parent, Node[] children) {
        int[] indices = new int[children.length];
        for (int i = 0; i < indices.length; i++) indices[i] = i;
        TreeModelEvent evt = new TreeModelEvent(this, createTreePath(parent.getFile()), indices, children);
        for (TreeModelListener l : listeners.getListeners(TreeModelListener.class)) {
            l.treeNodesChanged(evt);
        }
    }

    public void onCollapse(Node node) {
        synchronized (this) {
            expandedNodes.remove(node);
        }
    }

    public void onExpand(Node node) {
    }


    public static class Node {
        private File file;
        private Map properties;

        Node(File file) {
            this.file = file;
        }

        public void putProperty(String key, Object value) {
            if (properties == null) properties = new HashMap(10);
            properties.put(key, value);
        }

        public Object getProperty(String key) {
            if (properties == null) return null;
            return properties.get(key);
        }

        public File getFile() {
            return file;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            Node node = (Node) o;

            return file.equals(node.file);
        }

        @Override
        public int hashCode() {
            return file.hashCode();
        }

        public String toString() {
            return file.toString();
        }
    }

    public interface Decorator {
        public void getDecoration(Node parent, Node[] children, Runnable notifyChange);
        public void renderDecoration(Node node, CellRenderer renderer);
    }


    public static class CellRenderer extends DefaultTreeCellRenderer {
        public CellRenderer() {
			if (Os.isMac()) {
				setBackgroundNonSelectionColor(null);
				setBackgroundSelectionColor(null);
				setBorderSelectionColor(null);
			}
		}

	    public Component getTreeCellRendererComponent(
	            final JTree tree,
	            final Object value,
	            final boolean selected,
	            final boolean expanded,
	            final boolean leaf,
	            final int row,
	            final boolean hasFocus) {
            FileTreeModel model = (FileTreeModel) tree.getModel();

	        super.getTreeCellRendererComponent(
	                tree, ((FileTreeModel.Node) value).getFile().getName(), selected, expanded, leaf, row, hasFocus);
	        setOpaque(false);
	        setBorder(new EmptyBorder(1, 0, 1, 0));
	        setIcon(getNodeIcon((Node) value));

            List<? extends Decorator> decorators = model.decorators;
            for (Decorator d : decorators) {
                d.renderDecoration((Node) value, this);
            }

	        return this;
	    }

		public Color getBackground() {
	    	return null;
	    }

		private Icon getNodeIcon(Node node) {
            File file = node.getFile();
		    if (file.isDirectory()) {
				if (Os.isMac()) {
					return UIManager.getDefaults().getIcon("FileChooser.newFolderIcon");
                } else if (Os.isWindows()) {
                    return UIManager.getDefaults().getIcon("FileChooser.newFolderIcon");
				} else {
					return new ImageIcon("/usr/share/icons/Human/16x16/places/folder.png");
				}
			} else {
				if (Os.isMac()) {
					return UIManager.getDefaults().getIcon("FileView.fileIcon");
                } else if (Os.isWindows()) {
                    return UIManager.getDefaults().getIcon("FileView.fileIcon");
				} else {
					return new ImageIcon("/usr/share/icons/gnome/16x16/mimetypes/text-x-generic.png");
				}
			}
	    }
	}
}
