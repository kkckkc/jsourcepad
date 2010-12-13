package kkckkc.jsourcepad.vcs;

import com.google.common.base.Function;
import com.google.common.base.Splitter;
import kkckkc.jsourcepad.model.Application;
import kkckkc.jsourcepad.model.bundle.EnvironmentProvider;
import kkckkc.jsourcepad.ui.FileTreeModel;
import kkckkc.jsourcepad.util.io.ScriptExecutor;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;

public class SvnVcsDecorator extends AbstractVcsDecorator {

    @Override
    protected void getStates(final FileTreeModel.Node parent, final FileTreeModel.Node[] children, final Function<State[], Void> continuation) {
        if (! new File(parent.getFile(), ".svn").exists()) {
//            continuation.apply(null);
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
                    State[] states = new State[children.length];

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
                    System.out.println("execution = " + execution.getStderr());
                }
            }, new StringReader(""), EnvironmentProvider.getStaticEnvironment());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return;
    }

    private State getState(char status) {
        if (status == '?') return State.UNKNOWN;
        if (status == 'M') return State.MODIFIED;
        return null;
    }

    private int findChildIndex(FileTreeModel.Node[] children, String file) {
        for (int i = 0; i < children.length; i++) {
            if (children[i].getFile().getName().equals(file)) return i;
        }
        return -1;
    }
}
