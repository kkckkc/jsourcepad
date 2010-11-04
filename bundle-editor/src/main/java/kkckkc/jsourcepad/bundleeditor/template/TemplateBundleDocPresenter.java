package kkckkc.jsourcepad.bundleeditor.template;

import kkckkc.jsourcepad.bundleeditor.BasicBundleDocPresenter;
import kkckkc.jsourcepad.bundleeditor.model.BundleDocImpl;
import kkckkc.jsourcepad.model.bundle.CommandBundleItem;

import java.util.Map;

public class TemplateBundleDocPresenter extends BasicBundleDocPresenter {

    @Override
    protected void saveCallback() {
        super.saveCallback();

        BundleDocImpl bDoc = (BundleDocImpl) doc;
        Map plist = bDoc.getPlist();

        plist.put("output", CommandBundleItem.OUTPUT_AFTER_SELECTED_TEXT);
    }
}
