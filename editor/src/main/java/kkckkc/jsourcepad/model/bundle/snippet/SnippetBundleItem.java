package kkckkc.jsourcepad.model.bundle.snippet;

import java.util.Map;

import kkckkc.jsourcepad.model.Window;
import kkckkc.jsourcepad.model.bundle.BundleItem;
import kkckkc.jsourcepad.model.bundle.BundleItemSupplier;

public class SnippetBundleItem implements BundleItem {
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
	public BundleItemSupplier getBundleItemRef() {
	    return bundleItemSupplier;
	}

	@Override
    public void execute(Window window) throws Exception {
	    Snippet snippet = new Snippet(content, bundleItemSupplier);
	    snippet.insert(window, window.getDocList().getActiveDoc().getActiveBuffer());
    }
}
