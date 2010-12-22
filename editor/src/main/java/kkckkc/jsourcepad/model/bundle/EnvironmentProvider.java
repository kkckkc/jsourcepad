package kkckkc.jsourcepad.model.bundle;

import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.collect.Collections2;
import com.google.common.collect.Lists;
import kkckkc.jsourcepad.action.ActionContextKeys;
import kkckkc.jsourcepad.model.Application;
import kkckkc.jsourcepad.model.Doc;
import kkckkc.jsourcepad.model.Window;
import kkckkc.jsourcepad.util.Config;
import kkckkc.jsourcepad.util.Cygwin;
import kkckkc.jsourcepad.util.action.ActionManager;
import kkckkc.jsourcepad.util.io.SystemEnvironmentHelper;
import kkckkc.syntaxpane.model.Interval;
import kkckkc.utils.Os;

import java.io.File;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EnvironmentProvider {
    private static final Function<? super File,String> FILE_TO_STRING = new Function<File, String>() {
        @Override
        public String apply(File file) {
            return formatPath(file.toString());
        }
    };
    private static BundleItemSupplier lastBundleItemSupplier;

    public static Map<String, String> getEnvironment(Window window, BundleItemSupplier bundleItemSupplier) {
		Map<String, String> environment = new HashMap<String, String>(getStaticEnvironment());
		Doc activeDoc = window.getDocList().getActiveDoc();

		if (activeDoc != null) {
			environment.put("TM_SCOPE", activeDoc.getActiveBuffer().getInsertionPoint().getScope().getPath());
		
			environment.put("TM_LINE_INDEX", Integer.toString(activeDoc.getActiveBuffer().getInsertionPoint().getLineIndex()));
			environment.put("TM_LINE_NUMBER", Integer.toString(activeDoc.getActiveBuffer().getInsertionPoint().getLineNumber() + 1));
			
			environment.put("TM_CURRENT_LINE", activeDoc.getActiveBuffer().getText(activeDoc.getActiveBuffer().getCurrentLine()));
			
			String s = activeDoc.getActiveBuffer().getText(activeDoc.getActiveBuffer().getCurrentWord());
			if (s != null) {
				environment.put("TM_CURRENT_WORD", s);
			}

			if (activeDoc.getFile() != null) {
				environment.put("TM_FILENAME", activeDoc.getFile().getName());
				environment.put("TM_DIRECTORY", formatPath(activeDoc.getFile().getParentFile().getPath()));
				environment.put("TM_FILEPATH", formatPath(activeDoc.getFile().getPath()));
			}

            Interval selection = activeDoc.getActiveBuffer().getSelection();
            if (selection != null && ! selection.isEmpty()) {
                String text = activeDoc.getActiveBuffer().getText(selection);
                if (text.length() > 60000) text = text.substring(0, 60000);
                environment.put("TM_SELECTED_TEXT", text);
            }
		}

        if (window.getProject() != null) {
		    environment.put("TM_PROJECT_DIRECTORY", formatPath(window.getProject().getProjectDir().getPath()));
        }

		List<File> paths = Lists.newArrayList();

		if (bundleItemSupplier == null) {
            bundleItemSupplier = lastBundleItemSupplier;
        }
        lastBundleItemSupplier = bundleItemSupplier;

        environment.put("TM_BUNDLE_SUPPORT",
                formatPath(new File(bundleItemSupplier.getFile().getParentFile().getParentFile(), "Support").getPath()));

        paths.add(new File(bundleItemSupplier.getFile().getParentFile().getParentFile(), "Support/bin"));

        paths.add(new File(Config.getSupportFolder(), "bin"));
        paths.add(new File(Config.getSupportFolder(), System.getProperty("os.name") + "/bin"));

		List<File> files = Lists.newArrayList();
        ActionManager actionManager = window.getActionManager();
        if (! (actionManager.getActionContext().get(ActionContextKeys.FOCUSED_COMPONENT) instanceof Doc)) {
            if (window.getProject() != null) {
			    files = window.getProject().getSelectedFiles();
            }
		} else {
			if (activeDoc != null && activeDoc.getFile() != null) {
				files = Collections.singletonList(activeDoc.getFile());
			}
		}
		
		if (files.isEmpty()) {
			environment.put("TM_SELECTED_FILE", "");
			environment.put("TM_SELECTED_FILES", "");
		} else {
			environment.put("TM_SELECTED_FILE", "\"" + formatPath(files.get(0).toString()) + "\"");
			environment.put("TM_SELECTED_FILES", Joiner.on("\"").join(Collections2.transform(files, FILE_TO_STRING)));
		}
		

        if (activeDoc != null) {
		    environment.put("TM_SOFT_TABS", activeDoc.getTabManager().isSoftTabs() ? "true" : "false");
		    environment.put("TM_TAB_SIZE", Integer.toString(activeDoc.getTabManager().getTabSize()));
        }

        Map<String, String> systemEnvironment = SystemEnvironmentHelper.getSystemEnvironment();
        environment.put("PATH",
                    Joiner.on(File.pathSeparator).join(Collections2.transform(paths, FILE_TO_STRING)) +
                    File.pathSeparator + systemEnvironment.get("PATH"));

	    return environment;
    }

    private static String formatPath(String s) {
        if (Os.isWindows()) {
            return Cygwin.makePathForEnvironmentUsage(s);
        }
        return s;
    }

    public static Map<String,String> getStaticEnvironment() {
        Map<String, String> systemEnvironment = SystemEnvironmentHelper.getSystemEnvironment();

        Map<String, String> environment = new HashMap<String, String>(systemEnvironment);

        // JSourcePad specific variables
        environment.put("TM_SERVER_PORT", Integer.toString(Config.getHttpPort()));
        if (Application.get().getWindowManager().getFocusedWindow() != null)
            environment.put("TM_FOCUSED_WINDOW", Integer.toString(Application.get().getWindowManager().getFocusedWindow().getId()));

        environment.put("DIALOG", formatPath(new File(Config.getSupportFolder(), "bin/tm_dialog").getPath()));

        environment.put("TM_SUPPORT_PATH", formatPath(Config.getSupportFolder().getPath()));

        // TODO: Make this configurable
        environment.put("TM_SVN", "/usr/bin/svn");

        return environment;
    }
}
