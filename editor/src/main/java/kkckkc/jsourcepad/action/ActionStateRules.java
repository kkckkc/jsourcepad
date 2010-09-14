
package kkckkc.jsourcepad.action;

import java.io.File;
import kkckkc.jsourcepad.model.Doc;
import kkckkc.jsourcepad.ui.DocPresenter;
import kkckkc.jsourcepad.util.action.ActionContext;
import kkckkc.jsourcepad.util.action.ActionStateRule;
import kkckkc.syntaxpane.model.Interval;

public class ActionStateRules {
    public static ActionStateRule FILE_SELECTED = new ActionStateRule() {
        @Override
        public boolean shouldBeEnabled(ActionContext actionContext) {
            Object[] tp = actionContext.get(ActionContextKeys.SELECTION);
            return tp != null && tp.length > 0 && tp[0] instanceof File && ((File) tp[0]).isFile();
        }
    };

    public static ActionStateRule TEXT_SELECTED = new ActionStateRule() {
        @Override
        public boolean shouldBeEnabled(ActionContext actionContext) {
            Object[] tp = actionContext.get(ActionContextKeys.SELECTION);
            return tp != null && tp.length > 0 && tp[0] instanceof Interval;
        }
    };

    public static ActionStateRule HAS_ACTIVE_DOC = new ActionStateRule() {
        @Override
        public boolean shouldBeEnabled(ActionContext actionContext) {
            return actionContext.get(ActionContextKeys.ACTIVE_DOC) != null;
        }
    };

    public static ActionStateRule DOC_BACKED_BY_FILE = new ActionStateRule() {
        @Override
        public boolean shouldBeEnabled(ActionContext actionContext) {
            return actionContext.get(ActionContextKeys.ACTIVE_DOC).isBackedByFile();
        }
    };

    public static ActionStateRule HAS_ACTIVE_FIND = new ActionStateRule() {
        @Override
        public boolean shouldBeEnabled(ActionContext actionContext) {
            Doc doc = actionContext.get(ActionContextKeys.ACTIVE_DOC);
            return doc.getActiveBuffer().getFinder() != null;
        }
    };

    public static ActionStateRule DOC_IS_MODIFIED = new ActionStateRule() {
        @Override
        public boolean shouldBeEnabled(ActionContext actionContext) {
            Doc doc = actionContext.get(ActionContextKeys.ACTIVE_DOC);
            return doc != null && (! doc.isBackedByFile() || doc.isModified());
        }
    };

    public static ActionStateRule CAN_UNDO = new ActionStateRule() {
        @Override
        public boolean shouldBeEnabled(ActionContext actionContext) {
            Doc d = actionContext.get(ActionContextKeys.ACTIVE_DOC);
            DocPresenter dp = d.getPresenter(DocPresenter.class);
    		return dp.canUndo();
        }
    };

    public static ActionStateRule CAN_REDO = new ActionStateRule() {
        @Override
        public boolean shouldBeEnabled(ActionContext actionContext) {
            Doc d = actionContext.get(ActionContextKeys.ACTIVE_DOC);
            DocPresenter dp = d.getPresenter(DocPresenter.class);
    		return dp.canRedo();
        }
    };

}
