package kkckkc.jsourcepad.model.bundle;

import com.google.common.collect.Maps;
import kkckkc.jsourcepad.action.bundle.BundleAction;
import kkckkc.jsourcepad.model.Application;
import kkckkc.jsourcepad.util.action.ActionGroup;
import kkckkc.utils.Pair;

import javax.swing.*;
import java.util.List;
import java.util.Map;

public class BundleMenuProvider {

    private static Map<String, Action> itemActions = Maps.newHashMap();

    public static Action getActionForItem(String uuid) {
        return itemActions.get(uuid);
    }

    public static void registerActionForItem(String uuid, Action action) {
        itemActions.put(uuid, action);
    }


    public static ActionGroup getBundleActionGroup() {
        BundleManager bm = Application.get().getBundleManager();
        List<Bundle> bundles = bm.getBundles();

		ActionGroup ag = new ActionGroup();
        buildMenu(ag, bundles);
		return ag;
    }

	private static void buildMenu(ActionGroup ag, List<Bundle> list) {
	    for (Bundle b : list) {
            ActionGroup bm = new ActionGroup(b.getName());
            ag.add(bm);
            createMenu(bm, b.getMenu());
	    }
    }

    private static void createMenu(ActionGroup ag, List<Object> items) {
        for (Object o : items) {
            if (o == null) {
                ag.add(null);
            } else if (o instanceof BundleItemSupplier) {
                ag.add(new BundleAction((BundleItemSupplier) o));
            } else {
                Pair<String, List<Object>> pair = (Pair<String, List<Object>>) o;
                ActionGroup sub = new ActionGroup(pair.getFirst());
                createMenu(sub, pair.getSecond());
                ag.add(sub);
            }
        }
    }

}
