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
        bView.getName().setText(bDoc.getName());

        if (! Strings.isNullOrEmpty(bDoc.getKeyEquivalent())) bView.getActivation().setSelectedItem(BasicBundleDocViewImpl.KEY_EQUIVALENT);
        if (! Strings.isNullOrEmpty(bDoc.getTabTrigger())) bView.getActivation().setSelectedItem(BasicBundleDocViewImpl.TAB_TRIGGER);

        if (bDoc.getType() == BundleStructure.Type.SYNTAX) {
            bView.getScope().setEnabled(false);
        }

        bDoc.setSaveCallback(new Runnable() {
            @Override
            public void run() {
                saveCallback();
            }
        });
    }

    protected void saveCallback() {
        BundleDocImpl bDoc = (BundleDocImpl) doc;
        BasicBundleDocViewImpl bView = (BasicBundleDocViewImpl) view;

        bDoc.setScope(bView.getScope().getText());
        bDoc.setName(bView.getName().getText());
        if (bView.getActivation().getSelectedItem().equals(BasicBundleDocViewImpl.KEY_EQUIVALENT)) {
            bDoc.setKeyEquivalent(bView.getKeyEquivalent().getText());
            bDoc.setTabTrigger(null);
        } else {
            bDoc.setKeyEquivalent(null);
            bDoc.setTabTrigger(bView.getTabTrigger().getText());
        }
    }
}
