package kkckkc.jsourcepad.ui;

import com.google.common.collect.Lists;
import kkckkc.jsourcepad.model.Application;
import kkckkc.jsourcepad.model.DocList;
import kkckkc.jsourcepad.model.Project;
import kkckkc.jsourcepad.model.Window;
import kkckkc.jsourcepad.util.action.ActionGroup;
import kkckkc.jsourcepad.util.action.MenuFactory;
import kkckkc.jsourcepad.util.ui.PopupUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.ListableBeanFactory;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PostConstruct;
import javax.swing.*;
import javax.swing.tree.TreePath;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.util.Collections;
import java.util.List;
import java.util.Map;


public class ProjectViewImpl extends JTree implements ProjectView, MouseListener, BeanFactoryAware {
	private static final long serialVersionUID = 1L;
	private DocList docList;
	private Project project;
	protected Window window;
    private BeanFactory beanFactory;

    @Autowired
    public ProjectViewImpl(Project project, Window window, DocList docList) {
        super(new FileTreeModel(project.getProjectDir(), null, null));

        this.project = project;
        this.window = window;
        this.docList = docList;

        setShowsRootHandles(true);

        addMouseListener(this);
 
		ActionGroup actionGroup = window.getActionManager().getActionGroup("project-context-menu");
		JPopupMenu jpm = new MenuFactory().buildPopup(actionGroup, null);
		PopupUtils.bind(jpm, this, false);
    }

    @PostConstruct
    public void init() {
        Map<String, FileTreeModel.DecorationRenderer> renderers = ((ListableBeanFactory) beanFactory).getBeansOfType(FileTreeModel.DecorationRenderer.class);
        List<FileTreeModel.DecorationRenderer> dest = Lists.newArrayList();
        dest.addAll(renderers.values());

        setCellRenderer(new FileTreeModel.CellRenderer(Application.get().getBeanFactory().getBean(IconProvider.class), dest));
    }

    @Override
    public FileTreeModel getModel() {
        return (FileTreeModel) super.getModel();
    }

	@Override
	public void mouseClicked(MouseEvent e) {
        if (e.getClickCount() != 2) return;

        int selRow = getRowForLocation(e.getX(), e.getY());
		if (selRow == -1) return;
		TreePath selPath = getPathForLocation(e.getX(), e.getY());

    	FileTreeModel.Node node = (FileTreeModel.Node) selPath.getLastPathComponent();
        if (node.getFile().isFile()) {
            docList.open(node.getFile());
		}
	}

	@Override
	public void mousePressed(MouseEvent e) {
        if (e.getButton() == 3) {
            int selRow = getRowForLocation(e.getX(), e.getY());
            if (selRow == -1) {
                setSelectionRows(new int[] {});
            } else {
                setSelectionRow(selRow);
            }
        }
    }

	@Override
	public void mouseEntered(MouseEvent e) { }

	@Override
	public void mouseExited(MouseEvent e) { }

	@Override
	public void mouseReleased(MouseEvent e) { }

	@Override
    public void refresh(File file) {
        if (project == null) return;
		getModel().refresh(file);
    }

	@Override
    public void refresh() {
        if (project == null) return;
		getModel().refresh();
    }

    @Override
    public void revealFile(File file) {
        File root = (File) getModel().getRoot();
        if (file.toString().startsWith(root.toString())) {
            List<File> path = Lists.newArrayList();
            while (file != null && ! file.equals(root)) {
                path.add(file);
                file = file.getParentFile();
            }

            path.add(root);

            Collections.reverse(path);
            File[] pathArray = path.toArray(new File[path.size()]);

            TreePath tp = new TreePath(pathArray);
            expandPath(tp);
            scrollPathToVisible(tp);
            setSelectionPath(tp);
        }
    }



	@Override
    public JComponent getJComponent() {
		return this;
    }

    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        this.beanFactory = beanFactory;
    }
}



	