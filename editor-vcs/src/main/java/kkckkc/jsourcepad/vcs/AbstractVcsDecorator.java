package kkckkc.jsourcepad.vcs;

import com.google.common.base.Function;
import kkckkc.jsourcepad.ui.FileTreeModel;

import java.awt.*;

public abstract class AbstractVcsDecorator implements FileTreeModel.Decorator {

    private static final String VCS_STATE = "VCS_STATE";

    public static enum State {
        DELETED(Color.gray),
        MODIFIED(Color.blue),
        ADDED(Color.green),
        CONFLICT(Color.red),
        IGNORED(Color.orange),
        UNKNOWN(Color.magenta);

        Color color;

        State(Color color) {
            this.color = color;
        }
    }

    @Override
    public void getDecoration(FileTreeModel.Node parent, final FileTreeModel.Node[] children, final Runnable notifyChange) {
        getStates(parent, children, new Function<State[], Void>() {
            @Override
            public Void apply(State[] states) {
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

    protected abstract void getStates(FileTreeModel.Node parent, FileTreeModel.Node[] children, Function<State[], Void> continuation);

    @Override
    public void renderDecoration(FileTreeModel.Node node, FileTreeModel.CellRenderer renderer) {
        State state = (State) node.getProperty(VCS_STATE);
        if (state != null) {
            renderer.setForeground(state.color);
        }
    }
}
