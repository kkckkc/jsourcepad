package kkckkc.jsourcepad.ui.dialog.find;

import com.google.common.base.Function;
import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import kkckkc.jsourcepad.Dialog;
import kkckkc.jsourcepad.model.Buffer;
import kkckkc.jsourcepad.model.Doc;
import kkckkc.jsourcepad.model.ProjectFinder;
import kkckkc.jsourcepad.model.Window;
import kkckkc.syntaxpane.model.Interval;
import kkckkc.utils.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PostConstruct;
import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import java.awt.EventQueue;
import java.awt.event.*;
import java.io.File;
import java.io.IOException;
import java.util.List;

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

        view.getReplaceButton().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                replace();
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
                            doc.getActiveBuffer().scrollTo(entry.getOffset() - entry.getPosition(), Buffer.ScrollAlignment.MIDDLE);
                            doc.getActiveBuffer().setSelection(Interval.createWithLength(entry.getOffset(), entry.getLength()));
                            window.getContainer().requestFocus();
                        }
                    });
                }
            }
        });

        view.getResults().addTreeSelectionListener(new TreeSelectionListener() {
            @Override
            public void valueChanged(TreeSelectionEvent e) {
                if (view.getResults().getSelectionPaths() == null)
                    view.getReplaceButton().setText("Replace All");
                else
                    view.getReplaceButton().setText("Replace Selected");
            }
        });

        view.getReplaceButton().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                
            }
        });
    }

    private void replace() {
        ProjectFinder finder = window.getProject().getFinder();

        List<ProjectFinder.Entry> entriesToBeReplaced = Lists.newArrayList();

        if (view.getReplaceButton().getText().equals("Replace All"))
            entriesToBeReplaced.addAll(finder.getEntries());
        else {
            for (ProjectFinder.Entry entry : finder.getEntries()) {
                boolean tobeReplaced = false;
                for (TreePath path : view.getResults().getSelectionPaths()) {
                    Object object = ((DefaultMutableTreeNode) path.getLastPathComponent()).getUserObject();
                    if (object instanceof FolderEntry) {
                        FolderEntry folderEntry = (FolderEntry) object;
                        if (entry.getFile().equals(folderEntry.getFolder()) || FileUtils.isAncestorOf(entry.getFile(), folderEntry.getFolder())) {
                            tobeReplaced = true;
                            break;
                        }
                    } else {
                        ProjectFinder.Entry e = (ProjectFinder.Entry) object;
                        if (entry == e) {
                            tobeReplaced = true;
                            break;
                        }
                    }
                }

                if (tobeReplaced) entriesToBeReplaced.add(entry);
            }
        }

        finder.replace(entriesToBeReplaced, view.getReplaceWith().getText(), new FileContentsReplacer(), new Function<ProjectFinder.Entry, Void>() {
            @Override
            public Void apply(ProjectFinder.Entry input) {
                if (input == null) close();
                return null;
            }
        });
    }

    private void find() {
        ProjectFinder.Options options = new ProjectFinder.Options();
        options.setRegexp(view.getRegularExpression().isSelected());
        options.setCaseSensitive(! view.getIgnoreCase().isSelected());
        
        final ProjectFinder finder = window.getProject().newFinder(null, view.getSearchFor().getText(), options);

        DefaultMutableTreeNode rootNode = new DefaultMutableTreeNode(new FolderEntry("Search results", window.getProject().getProjectDir()));
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
        view.getReplaceButton().setEnabled(false);

        finder.find(new FileContentsAccessor(), new Function<ProjectFinder.Entry, Void>() {
            @Override
            public Void apply(final ProjectFinder.Entry entry) {
                EventQueue.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        if (entry == null) {
                            view.getFindButton().setEnabled(true);

                            if (! finder.getEntries().isEmpty()) view.getReplaceButton().setEnabled(true);
                            return;
                        }

                        final DefaultTreeModel treeModel = (DefaultTreeModel) view.getResults().getModel();
                        DefaultMutableTreeNode root = (DefaultMutableTreeNode) treeModel.getRoot();

                        String pathRelativeToProject = window.getProject().getProjectRelativePath(entry.getFile().toString()).substring(1);

                        DefaultMutableTreeNode fileNode = null;
                        for (int i = 0; i < root.getChildCount(); i++) {
                            DefaultMutableTreeNode child = (DefaultMutableTreeNode) root.getChildAt(i);
                            if (((FolderEntry) child.getUserObject()).getLabel().equals(pathRelativeToProject)) {
                                fileNode = child;
                                break;
                            }
                        }

                        if (fileNode == null) {
                            fileNode = new DefaultMutableTreeNode(new FolderEntry(pathRelativeToProject, entry.getFile()));
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


    private static class FolderEntry {
        private String label;
        private File folder;

        private FolderEntry(String label, File folder) {
            this.label = label;
            this.folder = folder;
        }

        public String getLabel() {
            return label;
        }

        public File getFolder() {
            return folder;
        }

        public String toString() {
            return label;
        }
    }

    private class FileContentsAccessor extends ProjectFinder.SimpleFileReader {
        @Override
        public Iterable<String> apply(File file) {
            for (Doc doc : window.getDocList().getDocs()) {
                if (doc.isBackedByFile() && doc.getFile().equals(file)) {
                    return Splitter.on("\n").split(doc.getActiveBuffer().getCompleteDocument().getText());
                }
            }
            return super.apply(file);
        }
    }

    private class FileContentsReplacer extends ProjectFinder.SimpleFileReplacer {
        @Override
        protected StringBuilder open(File file) throws IOException {
            for (Doc doc : window.getDocList().getDocs()) {
                if (doc.isBackedByFile() && file.equals(doc.getFile())) {
                    return new StringBuilder(doc.getActiveBuffer().getCompleteDocument().getText());
                }
            }
            return super.open(file);
        }

        @Override
        protected void save(File file, String contents) throws IOException {
            for (Doc doc : window.getDocList().getDocs()) {
                if (doc.isBackedByFile() && file.equals(doc.getFile())) {
                    doc.getActiveBuffer().replaceText(doc.getActiveBuffer().getCompleteDocument(), contents, null);

                    return;
                }
            }
            super.save(file, contents);
        }
    }
}
