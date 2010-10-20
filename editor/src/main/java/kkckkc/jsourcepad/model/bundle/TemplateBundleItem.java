package kkckkc.jsourcepad.model.bundle;

import kkckkc.jsourcepad.model.Application;
import kkckkc.jsourcepad.model.Window;
import kkckkc.jsourcepad.model.WindowManager;
import kkckkc.jsourcepad.util.io.ScriptExecutor;
import kkckkc.jsourcepad.util.io.UISupportCallback;
import kkckkc.utils.io.FileUtils;

import java.io.File;
import java.io.StringReader;
import java.util.Map;

public class TemplateBundleItem implements BundleItem<File> {

    private String extension;
    private String command;
    private BundleItemSupplier bundleItemSupplier;

    public TemplateBundleItem(BundleItemSupplier bundleItemSupplier, String command, String extension) {
        this.bundleItemSupplier = bundleItemSupplier;
        this.command = command;
        this.extension = extension;
    }

    public String getExtension() {
        return extension;
    }

    @Override
    public void execute(final Window window, final File file) throws Exception {
		ScriptExecutor scriptExecutor = new ScriptExecutor(command, Application.get().getThreadPool());
        scriptExecutor.setDirectory(bundleItemSupplier.getFile().getParentFile());

		WindowManager wm = Application.get().getWindowManager();

        Map<String, String> environment = EnvironmentProvider.getEnvironment(window, bundleItemSupplier);
        environment.put("TM_NEW_FILE", file.getCanonicalPath());
        environment.put("TM_NEW_FILE_BASENAME", FileUtils.getBaseName(file));
        environment.put("TM_NEW_FILE_DIRECTORY", file.getParentFile().getName());

        scriptExecutor.execute(new UISupportCallback(window.getContainer()) {
            public void onAfterSuccess(final ScriptExecutor.Execution execution) {
                String s = execution.getStdout();
                if (s == null) s = "";
            }
        }, new StringReader(""), environment);
    }

    @Override
    public BundleStructure.Type getType() {
        return BundleStructure.Type.TEMPLATE;
    }

    public static BundleItem create(BundleItemSupplier bundleItemSupplier, Map m) {
        return new TemplateBundleItem(bundleItemSupplier,
	    		(String) m.get("command"),
	    		(String) m.get("extension"));
    }
}
