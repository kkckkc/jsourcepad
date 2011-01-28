package kkckkc.jsourcepad.ui.dialog.find;

import com.google.common.base.Function;
import kkckkc.jsourcepad.Dialog;
import kkckkc.jsourcepad.model.Buffer;
import kkckkc.jsourcepad.model.Doc;
import kkckkc.jsourcepad.model.ProjectFinder;
import kkckkc.jsourcepad.model.Window;
import kkckkc.syntaxpane.model.Interval;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PostConstruct;
import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import java.awt.*;
import java.awt.event.*;

public class FindInProjectDialog implements Dialog<FindInProjectDialogView> {
    private FindInProjectDialogView view;
    private Window window;

    @PostConstruct
    public void init() {
        view.getJDialog().setModalityType(java.awt.Dialog.ModalityType.MODELESS);

        view.getSearchFor().addKeyListener(new KeyListener() {
            public void keyTyped(KeyEvent e) { }
            public void keyReleased(KeyEvent e) { }

            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    find();
                }
            }
        });

        view.getFindButton().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                find();
            }
        });

        view.getResults().addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() != 2) return;

                int selRow = view.getResults().getRowForLocation(e.getX(), e.getY());
                if (selRow == -1) return;
                TreePath selPath = view.getResults().getPathForLocation(e.getX(), e.getY());

                if (((DefaultMutableTreeNode) selPath.getLastPathComponent()).getUserObject() instanceof ProjectFinder.Entry) {
                    final ProjectFinder.Entry entry = (ProjectFinder.Entry) ((DefaultMutableTreeNode) selPath.getLastPathComponent()).getUserObject();
                    final Doc doc = window.getDocList().open(entry.getFile());
                    EventQueue.invokeLater(new Runnable() {
                        @Override
                        public void run() {
                            doc.getActiveBuffer().scrollTo(entry.getOffset(), Buffer.ScrollAlignment.MIDDLE);
                            doc.getActiveBuffer().setSelection(Interval.createWithLength(entry.getOffset(), entry.getLength()));
                            window.getContainer().requestFocus();
                        }
                    });
                }
            }
        });

        view.getReplaceButton().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                
            }
        });
    }

    private void find() {
        ProjectFinder.Options options = new ProjectFinder.Options();
        options.setRegexp(view.getRegularExpression().isSelected());
        options.setCaseSensitive(! view.getIgnoreCase().isSelected());
        
        ProjectFinder finder = window.getProject().newFinder(null, view.getSearchFor().getText(), options);

        DefaultMutableTreeNode rootNode = new DefaultMutableTreeNode("Search results");
        view.getResults().setModel(new DefaultTreeModel(rootNode));

        view.getResults().getModel().addTreeModelListener(new TreeModelListener() {
            @Override
            public void treeNodesInserted(final TreeModelEvent e) {
                if (e.getTreePath() != null) {
                    EventQueue.invokeLater(new Runnable() {
                        @Override
                        public void run() {
                            view.getResults().expandPath(e.getTreePath());
                        }
                    });
                }
            }

            @Override public void treeNodesChanged(TreeModelEvent e) { }
            @Override public void treeNodesRemoved(TreeModelEvent e) { }
            @Override public void treeStructureChanged(TreeModelEvent e) { }
        });

        view.getFindButton().setEnabled(false);

        finder.find(new ProjectFinder.SimpleFileReader(), new Function<ProjectFinder.Entry, Void>() {
            @Override
            public Void apply(final ProjectFinder.Entry entry) {
                EventQueue.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        if (entry == null) {
                            view.getFindButton().setEnabled(true);
                            return;
                        }

                        final DefaultTreeModel treeModel = (DefaultTreeModel) view.getResults().getModel();
                        DefaultMutableTreeNode root = (DefaultMutableTreeNode) treeModel.getRoot();

                        String pathRelativeToProject = window.getProject().getProjectRelativePath(entry.getFile().toString()).substring(1);

                        DefaultMutableTreeNode fileNode = null;
                        for (int i = 0; i < root.getChildCount(); i++) {
                            DefaultMutableTreeNode child = (DefaultMutableTreeNode) root.getChildAt(i);
                            if (child.getUserObject().equals(pathRelativeToProject)) {
                                fileNode = child;
                                break;
                            }
                        }

                        if (fileNode == null) {
                            fileNode = new DefaultMutableTreeNode(pathRelativeToProject);
                            treeModel.insertNodeInto(fileNode, root, root.getChildCount());
                        }

                        final DefaultMutableTreeNode lineNode = new DefaultMutableTreeNode(entry);
                        treeModel.insertNodeInto(lineNode, fileNode, fileNode.getChildCount());
                    }
                });

                return null;
            }
        });
    }

    public void show() {
        view.getSearchFor().requestFocusInWindow();

        view.getJDialog().setVisible(true);
    }

    @Override
    public void close() {
        view.getJDialog().dispose();
    }

    @Override
    @Autowired
    public void setView(FindInProjectDialogView view) {
        this.view = view;
    }

    @Autowired
    public void setWindow(Window window) {
        this.window = window;
    }
}
