package kkckkc.jsourcepad.model.bundle;

import kkckkc.jsourcepad.model.bundle.snippet.SnippetBundleItem;
import kkckkc.utils.plist.GeneralPListReader;

import java.io.File;
import java.io.IOException;
import java.util.Map;


public class BundleItemFactory {

	public static BundleItem getItem(BundleItemSupplier bundleItemSupplier, File file) {
		try {
            switch (bundleItemSupplier.getType()) {
                case COMMAND:
                    return getCommand(bundleItemSupplier, file);

                case SNIPPET:
                    return getSnippet(bundleItemSupplier, file);

                case TEMPLATE:
                    return getTemplate(bundleItemSupplier, file);

                default:
                    throw new RuntimeException("Unsupported bundle item " + file);
            }
		} catch (IOException ioe) {
			throw new RuntimeException(ioe);
		}
    }


    private static BundleItem getTemplate(BundleItemSupplier bundleItemSupplier, File file) throws IOException {
        GeneralPListReader gpl = new GeneralPListReader();
        Map map = (Map) gpl.read(file);

        return TemplateBundleItem.create(bundleItemSupplier, map);
    }

    private static BundleItem getCommand(BundleItemSupplier bundleItemSupplier, File file) throws IOException {
		GeneralPListReader gpl = new GeneralPListReader();
		Map map = (Map) gpl.read(file);

	    return CommandBundleItem.create(bundleItemSupplier, map);
    }


	private static BundleItem getSnippet(BundleItemSupplier bundleItemSupplier, File file) throws IOException {
		GeneralPListReader gpl = new GeneralPListReader();
		Map map = (Map) gpl.read(file);

	    return SnippetBundleItem.create(bundleItemSupplier, map);
    }
	
}
