package kkckkc.jsourcepad.vcs;

import com.google.common.base.Function;
import kkckkc.jsourcepad.ui.FileTreeModel;

public abstract class AbstractVcsDecorator implements FileTreeModel.Decorator {

    public static final String VCS_STATE = "VCS_STATE";

    @Override
    public void decorate(FileTreeModel.Node parent, final FileTreeModel.Node[] children, final Runnable notifyChange) {
        getStates(parent, children, new Function<VcsState[], Void>() {
            @Override
            public Void apply(VcsState[] states) {
                for (int i = 0; i < children.length; i++) {
                    if (states == null || states[i] == null) {
                        children[i].putProperty(VCS_STATE, null);
                    } else {
                        children[i].putProperty(VCS_STATE, states[i]);
                    }
                }

                notifyChange.run();

                return null;
            }
        });
    }

    protected abstract void getStates(FileTreeModel.Node parent, FileTreeModel.Node[] children, Function<VcsState[], Void> continuation);
}
