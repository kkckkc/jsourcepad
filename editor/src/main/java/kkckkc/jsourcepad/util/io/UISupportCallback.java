package kkckkc.jsourcepad.util.io;

import java.awt.Container;
import java.awt.EventQueue;

import javax.swing.JOptionPane;

import kkckkc.jsourcepad.model.Application;
import kkckkc.jsourcepad.ui.dialog.ProgressDialog;
import kkckkc.jsourcepad.util.io.ScriptExecutor.Callback;
import kkckkc.jsourcepad.util.io.ScriptExecutor.Execution;

public class UISupportCallback implements Callback {
	private ProgressDialog dialog;
	private String title;
	private Container parent;
	
	public UISupportCallback(Container parent, String title) {
	    this.parent = parent;
	    this.title = title;
    }

	public UISupportCallback(Container parent) {
	    this(parent, "Executing Script...");
    }

	public final void onAbort(final Execution execution) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				if (dialog != null)
					dialog.close();

				onAfterAbort(execution);
				onAfterDone();
			}
		});
	}

	public final void onDelay(final Execution execution) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				dialog = Application.get().getWindowManager().getWindow(parent).getPresenter(ProgressDialog.class);
				dialog.show(title, execution, parent);
				
				onAfterDelay(execution);
				onAfterDone();
			}
		});
	}

	public final void onFailure(final Execution execution) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				if (dialog != null)
					dialog.close();
				
				onAfterFailure(execution);
				onAfterDone();

				// TODO: Add details
				JOptionPane.showMessageDialog(parent, "Execution Failed...");
			}
		});
	}

	public final void onSuccess(final Execution execution) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				if (dialog != null)
					dialog.close();
				onAfterSuccess(execution);
				onAfterDone();
			}
		});
	}

	public void onAfterDone() {
	}

	public void onAfterAbort(Execution execution) {
    }

	public void onAfterDelay(Execution execution) {
    }

	public void onAfterFailure(Execution execution) {
    }

	public void onAfterSuccess(Execution execution) {
    }
}
