package kkckkc.jsourcepad.bundleeditor;

import com.google.common.base.Strings;
import kkckkc.jsourcepad.bundleeditor.model.BundleDocImpl;
import kkckkc.jsourcepad.model.bundle.BundleStructure;
import kkckkc.jsourcepad.ui.DocPresenter;

public class BasicBundleDocPresenter extends DocPresenter {
    @Override
    public void init() {
        super.init();

        BundleDocImpl bDoc = (BundleDocImpl) doc;
        BasicBundleDocViewImpl bView = (BasicBundleDocViewImpl) view;

        bView.getScope().setText(Strings.nullToEmpty(bDoc.getScope()));
        bView.getKeyEquivalent().setText(bDoc.getKeyEquivalent());
        bView.getTabTrigger().setText(bDoc.getTabTrigger());
        
        if (! Strings.isNullOrEmpty(bDoc.getKeyEquivalent())) bView.getActivation().setSelectedItem(BasicBundleDocViewImpl.KEY_EQUIVALENT);
        if (! Strings.isNullOrEmpty(bDoc.getTabTrigger())) bView.getActivation().setSelectedItem(BasicBundleDocViewImpl.TAB_TRIGGER);

        if (bDoc.getType() == BundleStructure.Type.SYNTAX) {
            bView.getScope().setEnabled(false);
        }
    }
}
