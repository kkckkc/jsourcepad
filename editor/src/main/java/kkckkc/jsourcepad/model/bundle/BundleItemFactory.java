package kkckkc.jsourcepad.model.bundle;

import java.io.File;
import java.io.IOException;
import java.util.Map;

import kkckkc.jsourcepad.model.bundle.snippet.SnippetBundleItem;
import kkckkc.syntaxpane.util.plist.GeneralPListReader;
import kkckkc.syntaxpane.util.plist.PListFormatter;


public class BundleItemFactory {

	public static BundleItem getItem(BundleItemSupplier bundleItemSupplier, File file) {
		try {
			File dir = file.getParentFile();
			if (dir.getName().equals("Commands")) {
				return getCommand(bundleItemSupplier, file);
			} else if (dir.getName().equals("Snippets")) {
				return getSnippet(bundleItemSupplier, file);
			} else {
				throw new RuntimeException("Unsupported bundle item " + file);
			}
		} catch (IOException ioe) {
			throw new RuntimeException(ioe);
		}
    }

	private static BundleItem getCommand(BundleItemSupplier bundleItemSupplier, File file) throws IOException {
		GeneralPListReader gpl = new GeneralPListReader();
		Map m = (Map) gpl.read(file);
		
		System.out.println(new PListFormatter().format(m));
		
	    return CommandBundleItem.create(bundleItemSupplier, m);
    }


	private static BundleItem getSnippet(BundleItemSupplier bundleItemSupplier, File file) throws IOException {
		GeneralPListReader gpl = new GeneralPListReader();
		Map m = (Map) gpl.read(file);
		
		System.out.println(new PListFormatter().format(m));
		
	    return SnippetBundleItem.create(bundleItemSupplier, m);
    }
	
}
