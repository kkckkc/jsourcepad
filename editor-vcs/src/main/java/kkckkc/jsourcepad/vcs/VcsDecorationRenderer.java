package kkckkc.jsourcepad.vcs;

import kkckkc.jsourcepad.ui.FileTreeModel;

public class VcsDecorationRenderer implements FileTreeModel.DecorationRenderer {
    @Override
    public void renderDecoration(FileTreeModel.Node node, FileTreeModel.CellRenderer renderer) {
        VcsState state = (VcsState) node.getProperty(AbstractVcsDecorator.VCS_STATE);
        if (state != null) {
            renderer.setForeground(state.color);
        }
    }
}
