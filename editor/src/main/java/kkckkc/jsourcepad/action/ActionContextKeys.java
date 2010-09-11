
package kkckkc.jsourcepad.action;

import kkckkc.jsourcepad.Presenter;
import kkckkc.jsourcepad.model.Doc;
import kkckkc.jsourcepad.util.action.ActionContext;

public class ActionContextKeys {
    public static ActionContext.Key<Doc> ACTIVE_DOC = new ActionContext.Key<Doc>();
    public static ActionContext.Key<Object[]> SELECTION = new ActionContext.Key<Object[]>();
    public static ActionContext.Key<Presenter> FOCUSED_COMPONENT = new ActionContext.Key<Presenter>();
}
