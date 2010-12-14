package kkckkc.jsourcepad.vcs;

import com.google.common.base.Function;
import com.google.common.base.Splitter;
import kkckkc.jsourcepad.model.Application;
import kkckkc.jsourcepad.model.bundle.EnvironmentProvider;
import kkckkc.jsourcepad.ui.FileTreeModel;
import kkckkc.jsourcepad.util.io.ErrorDialog;
import kkckkc.jsourcepad.util.io.ScriptExecutor;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;

public class SvnVcsDecorator extends AbstractVcsDecorator {

    @Override
    protected void getStates(final FileTreeModel.Node parent, final FileTreeModel.Node[] children, final Function<VcsState[], Void> continuation) {
        if (! new File(parent.getFile(), ".svn").exists()) {
            return;
        }

        ScriptExecutor scriptExecutor = new ScriptExecutor(
                "svn status",
                Application.get().getThreadPool());
        scriptExecutor.setDelay(0);
        scriptExecutor.setDirectory(parent.getFile());
        scriptExecutor.setShowStderr(false);

        try {
            scriptExecutor.execute(new ScriptExecutor.CallbackAdapter() {
                @Override
                public void onSuccess(ScriptExecutor.Execution execution) {
                    VcsState[] states = new VcsState[children.length];

                    for (String line : Splitter.on("\n").omitEmptyStrings().split(execution.getStdout())) {
                        char status = line.charAt(0);
                        String file = line.substring(1).trim();

                        int childIndex = findChildIndex(children, file);
                        if (childIndex >= 0) {
                            states[childIndex] = getState(status);
                        }
                    }
                    continuation.apply(states);
                }

                @Override
                public void onFailure(ScriptExecutor.Execution execution) {
                    ErrorDialog errorDialog = Application.get().getErrorDialog();
                    errorDialog.show("Script Execution Failed...", execution.getStderr(), null);
                }
            }, new StringReader(""), EnvironmentProvider.getStaticEnvironment());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private VcsState getState(char status) {
        if (status == '?') return VcsState.UNKNOWN;
        if (status == 'M') return VcsState.MODIFIED;
        if (status == 'A') return VcsState.ADDED;
        if (status == 'I') return VcsState.IGNORED;
        if (status == 'C') return VcsState.CONFLICT;
        if (status == 'D') return VcsState.DELETED;
        if (status == 'R') return VcsState.REPLACED;
        if (status == '~') return VcsState.OBSTRUCTED;
        if (status == 'X') return VcsState.EXTERNAL;
        return null;
    }

    private int findChildIndex(FileTreeModel.Node[] children, String file) {
        for (int i = 0; i < children.length; i++) {
            if (children[i].getFile().getName().equals(file)) return i;
        }
        return -1;
    }
}
