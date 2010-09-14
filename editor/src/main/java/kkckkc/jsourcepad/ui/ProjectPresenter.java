package kkckkc.jsourcepad.ui;

import java.io.File;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.swing.JComponent;
import javax.swing.JTree;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.TreePath;

import kkckkc.jsourcepad.Presenter;
import kkckkc.jsourcepad.model.Project;
import kkckkc.jsourcepad.model.Window;
import kkckkc.jsourcepad.util.messagebus.DispatchStrategy;

import org.springframework.beans.factory.annotation.Autowired;

import com.google.common.collect.Lists;
import kkckkc.jsourcepad.action.ActionContextKeys;
import kkckkc.jsourcepad.util.action.ActionContext;

public class ProjectPresenter implements Presenter<ProjectView>, Project.FileChangeListener {

	private ProjectView view;
	private Window window;
    private ActionContext actionContext;

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
		window.topic(Project.FileChangeListener.class).subscribe(DispatchStrategy.ASYNC_EVENT, this);
		
		((JTree) view).getSelectionModel().addTreeSelectionListener(new TreeSelectionListener() {
			@Override
			public void valueChanged(TreeSelectionEvent e) {
				TreePath[] selectionPaths = ((JTree) view).getSelectionPaths();
				List<File> paths = Lists.newArrayList();
                if (selectionPaths != null) {
                    for (TreePath tp : selectionPaths) {
                        paths.add((File) tp.getLastPathComponent());
                    }
                }
				window.getProject().setSelectedFiles(paths);

                actionContext.put(ActionContextKeys.SELECTION, paths.toArray());
                actionContext.commit();
			}
		});

        actionContext = new ActionContext();
        actionContext.put(ActionContextKeys.FOCUSED_COMPONENT, this);
        actionContext.commit();

        ActionContext.set(view.getJComponent(), actionContext);
	}

	@Override
    public void created(File file) {
		view.insertFile(file);
    }

	@Override
    public void refresh(File file) {
		if (file != null) {
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
}
