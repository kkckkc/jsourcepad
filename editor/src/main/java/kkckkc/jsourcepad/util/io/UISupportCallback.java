package kkckkc.jsourcepad.util.io;

import com.google.common.base.Strings;
import kkckkc.jsourcepad.model.Application;
import kkckkc.jsourcepad.model.Window;
import kkckkc.jsourcepad.util.io.ScriptExecutor.Callback;
import kkckkc.jsourcepad.util.io.ScriptExecutor.Execution;

import java.awt.*;

public class UISupportCallback implements Callback {
	private String title;
	private Container parent;
    private Window window;

    public UISupportCallback(Window window, String title) {
	    this.window = window;
        this.parent = window.getContainer();
	    this.title = title;
        window.beginWait(false, null);
    }

	public UISupportCallback(Window window) {
	    this(window, "Executing Script...");
    }

	public final void onAbort(final Execution execution) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				onAfterAbort(execution);
				onAfterDone(execution);
			}
		});
	}

	public final void onDelay(final Execution execution) {
        window.beginWait(true, new Runnable() {
            public void run() {
                execution.cancel();
            }
        });
        onAfterDelay(execution);
	}

	public final void onFailure(final Execution execution) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
                window.endWait();

				onAfterFailure(execution);
				onAfterDone(execution);

                ErrorDialog errorDialog = Application.get().getErrorDialog();
                errorDialog.show("Script Execution Failed...",
                    execution.getStderr() +
                    "\n" + "Script Follows:\n" + Strings.repeat("-", 120) + "\n" +
                    execution.getScript(), parent);
            }
		});
	}

	public final void onSuccess(final Execution execution) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
                window.endWait();
				onAfterSuccess(execution);
				onAfterDone(execution);
			}
		});
	}

	public void onAfterDone(Execution execution) {
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
