
package kkckkc.jsourcepad.action;

import java.io.File;
import kkckkc.jsourcepad.model.Doc;
import kkckkc.jsourcepad.util.action.ActionContext;
import kkckkc.jsourcepad.util.action.ActionStateRule;

public class ActionStateRules {
    public static ActionStateRule FILE_SELECTED = new ActionStateRule() {
        @Override
        public boolean shouldBeEnabled(ActionContext actionContext) {
            Object[] tp = actionContext.get(ActionContextKeys.SELECTION);
            return tp != null && tp.length > 0 && tp[0] instanceof File && ((File) tp[0]).isFile();
        }
    };

    public static ActionStateRule HAS_ACTIVE_DOC = new ActionStateRule() {
        @Override
        public boolean shouldBeEnabled(ActionContext actionContext) {
            return actionContext.get(ActionContextKeys.ACTIVE_DOC) != null;
        }
    };

    public static ActionStateRule DOC_IS_MODIFIED = new ActionStateRule() {
        @Override
        public boolean shouldBeEnabled(ActionContext actionContext) {
            Doc doc = actionContext.get(ActionContextKeys.ACTIVE_DOC);
            return doc != null && (! doc.isBackedByFile() || doc.isModified());
        }
    };
}
