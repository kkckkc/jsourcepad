package kkckkc.jsourcepad.ui;

import com.google.common.collect.Lists;
import kkckkc.jsourcepad.Presenter;
import kkckkc.jsourcepad.action.ActionContextKeys;
import kkckkc.jsourcepad.model.Project;
import kkckkc.jsourcepad.model.ProjectImpl;
import kkckkc.jsourcepad.model.Window;
import kkckkc.jsourcepad.model.settings.IgnorePatternProjectSettings;
import kkckkc.jsourcepad.model.settings.SettingsManager;
import kkckkc.jsourcepad.util.action.ActionContext;
import kkckkc.jsourcepad.util.messagebus.DispatchStrategy;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.ListableBeanFactory;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PostConstruct;
import javax.swing.*;
import javax.swing.event.TreeExpansionEvent;
import javax.swing.event.TreeExpansionListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.TreePath;
import java.io.File;
import java.io.FileFilter;
import java.util.List;
import java.util.Map;

public class ProjectPresenter implements Presenter<ProjectView>, ProjectImpl.RefreshListener, SettingsManager.Listener<IgnorePatternProjectSettings>, BeanFactoryAware {

	private ProjectView view;
	private Window window;
    private ActionContext actionContext;
    private Project project;
    private BeanFactory beanFactory;

    @Autowired
    public void setProject(Project project) {
        this.project = project;
    }

    @Autowired
    public void setView(ProjectView view) {
	    this.view = view;
    }

	@Autowired
	public void setWindow(Window window) {
		this.window = window;
	}
	
	@PostConstruct
	public void init() {
        if (project.getProjectDir() == null) return;
        view.setModel(new FileTreeModel(project.getProjectDir(), new FileFilter() {
            @Override
            public boolean accept(File pathname) {
                return project.getFilePredicate().apply(pathname);
            }
        }, getDecorators()));

		window.topic(Project.RefreshListener.class).subscribe(DispatchStrategy.ASYNC_EVENT, this);

		((JTree) view).getSelectionModel().addTreeSelectionListener(new TreeSelectionListener() {
			@Override
			public void valueChanged(TreeSelectionEvent e) {
				TreePath[] selectionPaths = ((JTree) view).getSelectionPaths();
				List<File> paths = Lists.newArrayList();
                if (selectionPaths != null) {
                    for (TreePath tp : selectionPaths) {
                        paths.add(((FileTreeModel.Node) tp.getLastPathComponent()).getFile());
                    }
                }
				window.getProject().setSelectedFiles(paths);

                actionContext.put(ActionContextKeys.SELECTION, paths.toArray());
                actionContext.commit();
			}
		});

        ((JTree) view).addTreeExpansionListener(new TreeExpansionListener() {
            @Override
            public void treeExpanded(TreeExpansionEvent event) {
                FileTreeModel.Node node= (FileTreeModel.Node) event.getPath().getLastPathComponent();
                File file = node.getFile();

                ((FileTreeModel) ((JTree) view).getModel()).onExpand(node);
                project.register(file);
            }

            @Override
            public void treeCollapsed(TreeExpansionEvent event) {
                FileTreeModel.Node node= (FileTreeModel.Node) event.getPath().getLastPathComponent();
                File file = node.getFile();

                ((FileTreeModel) ((JTree) view).getModel()).onCollapse(node);
                project.unregister(file);
            }
        });

        actionContext = new ActionContext();
        actionContext.put(ActionContextKeys.FOCUSED_COMPONENT, window.getProject());
        actionContext.commit();

        ActionContext.set(view.getJComponent(), actionContext);

        if (project.getSettingsManager() != null)
            project.getSettingsManager().subscribe(IgnorePatternProjectSettings.class, this, false);
	}

	@Override
    public void refreshed(File file) {
		if (file != null) {
            if (file.isDirectory())
			    view.refresh(file);
		} else {
			view.refresh();
		}
    }

	public JComponent getJComponent() {
	    return view.getJComponent();
    }

    public void revealFile(File file) {
        view.revealFile(file);
    }

    @Override
    public void settingUpdated(IgnorePatternProjectSettings settings) {
        view.setModel(new FileTreeModel(project.getProjectDir(), new FileFilter() {
            @Override
            public boolean accept(File pathname) {
                return project.getFilePredicate().apply(pathname);
            }
        }, getDecorators()));
    }

    public List<FileTreeModel.Decorator> getDecorators() {
        Map<String, FileTreeModel.Decorator> decorators = ((ListableBeanFactory) beanFactory).getBeansOfType(FileTreeModel.Decorator.class);
        List<FileTreeModel.Decorator> dest = Lists.newArrayList();
        dest.addAll(decorators.values());
        return dest;
    }

    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        this.beanFactory = beanFactory;
    }
}
