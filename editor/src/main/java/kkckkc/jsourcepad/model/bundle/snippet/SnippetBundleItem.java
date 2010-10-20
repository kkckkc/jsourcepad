package kkckkc.jsourcepad.model.bundle.snippet;

import kkckkc.jsourcepad.model.Window;
import kkckkc.jsourcepad.model.bundle.BundleItem;
import kkckkc.jsourcepad.model.bundle.BundleItemSupplier;
import kkckkc.jsourcepad.model.bundle.BundleStructure;

import java.util.Map;

public class SnippetBundleItem implements BundleItem<Void> {
	private String content;

	private BundleItemSupplier bundleItemSupplier;

	public SnippetBundleItem(BundleItemSupplier bundleItemSupplier, String content) {
	    this.content = content;
	    this.bundleItemSupplier = bundleItemSupplier;
    }
	
	public static BundleItem create(BundleItemSupplier bundleItemSupplier, Map<?, ?> m) {
	    return new SnippetBundleItem(bundleItemSupplier, (String) m.get("content"));
    }

	@Override
    public void execute(Window window, Void context) throws Exception {
	    Snippet snippet = new Snippet(content, bundleItemSupplier);
	    snippet.insert(window, window.getDocList().getActiveDoc().getActiveBuffer());
    }

    @Override
    public BundleStructure.Type getType() {
        return BundleStructure.Type.SNIPPET;
    }
}
