package kkckkc.jsourcepad.ui.dialog.error;

import com.google.common.base.Throwables;
import kkckkc.jsourcepad.Dialog;
import kkckkc.jsourcepad.util.io.ErrorDialog;
import org.springframework.beans.factory.annotation.Autowired;

public class ErrorDialogImpl implements Dialog<ErrorDialogView>, ErrorDialog {

	private ErrorDialogView view;

	@Autowired
	public void setView(ErrorDialogView view) {
	    this.view = view;
	}

    @Override
    public void show(Throwable details) {
		view.setTitle("An exception has occured: " + details.getMessage());
        view.getDetails().setText(Throwables.getStackTraceAsString(details));

	    view.getJDialog().setVisible(true);
	}

    @Override
    public void show(String title, String details) {
		view.setTitle(title);
        view.getDetails().setText(details);

	    view.getJDialog().setVisible(true);
	}
	
	@Override
    public void close() {
		view.close();
	}
}
