package kkckkc.jsourcepad.action.bundle;

import kkckkc.jsourcepad.model.Application;
import kkckkc.jsourcepad.model.Window;
import kkckkc.jsourcepad.model.bundle.BundleItem;
import kkckkc.jsourcepad.model.bundle.BundleItemSupplier;
import kkckkc.jsourcepad.model.bundle.BundleMenuProvider;
import kkckkc.jsourcepad.util.action.BaseAction;

import java.awt.*;
import java.awt.event.ActionEvent;

public class BundleAction extends BaseAction {

	private BundleItemSupplier ref;

	public BundleAction(BundleItemSupplier ref) {
//		super("<html>" + ref.getName() + "&nbsp;&nbsp;&nbsp;<i>" + ref.getActivator().toString() + "</i></html>");
		super(ref.getName());
		this.ref = ref;
        BundleMenuProvider.registerActionForItem(ref.getUUID(), this);
    }

	public BundleItemSupplier getRef() {
	    return ref;
    }
	
	@Override
    public void performAction(ActionEvent e) {
		BundleItem bi = ref.get();
		try {
			Window window = Application.get().getWindowManager().getWindow((Container) e.getSource());
	        bi.execute(window, null);
        } catch (Exception e1) {
	        e1.printStackTrace();
        }
    }

}
