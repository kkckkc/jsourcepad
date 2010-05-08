package kkckkc.jsourcepad.ui;

import java.io.File;

import javax.annotation.PostConstruct;
import javax.swing.JComponent;

import org.springframework.beans.factory.annotation.Autowired;

import kkckkc.jsourcepad.Presenter;
import kkckkc.jsourcepad.model.Project;
import kkckkc.jsourcepad.model.Window;
import kkckkc.jsourcepad.util.messagebus.DispatchStrategy;

public class ProjectPresenter implements Presenter<ProjectView>, Project.FileChangeListener {

	private ProjectView view;
	private Window window;

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
}
