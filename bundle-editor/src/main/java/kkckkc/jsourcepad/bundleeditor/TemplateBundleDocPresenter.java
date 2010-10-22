package kkckkc.jsourcepad.bundleeditor;

import kkckkc.jsourcepad.bundleeditor.model.BundleDocImpl;
import kkckkc.jsourcepad.model.bundle.CommandBundleItem;

import java.util.Map;

public class TemplateBundleDocPresenter extends BasicBundleDocPresenter {

    @Override
    protected void saveCallback() {
        super.saveCallback();

        BundleDocImpl bDoc = (BundleDocImpl) doc;
        CommandBundleDocViewImpl cView = (CommandBundleDocViewImpl) view;
        Map plist = bDoc.getPlist();

        plist.put("output", CommandBundleItem.OUTPUT_AFTER_SELECTED_TEXT);
    }
}
