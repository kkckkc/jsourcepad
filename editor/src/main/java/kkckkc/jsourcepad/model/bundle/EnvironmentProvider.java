package kkckkc.jsourcepad.model.bundle;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import kkckkc.jsourcepad.model.Doc;
import kkckkc.jsourcepad.model.Window;
import kkckkc.syntaxpane.model.Interval;

public class EnvironmentProvider {
	public static Map<String, String> getEnvironment(Window window, BundleItemSupplier bundleItemSupplier) {
		Map<String, String> environment = Maps.newHashMap();
		Doc activeDoc = window.getDocList().getActiveDoc();

		if (activeDoc != null) {
			environment.put("TM_SCOPE", activeDoc.getActiveBuffer().getInsertionPoint().getScope().getPath());
		
			environment.put("TM_LINE_INDEX", Integer.toString(activeDoc.getActiveBuffer().getInsertionPoint().getLineIndex()));
			environment.put("TM_LINE_NUMBER", Integer.toString(activeDoc.getActiveBuffer().getInsertionPoint().getLineNumber() + 1));
			
			environment.put("TM_CURRENT_LINE", activeDoc.getActiveBuffer().getCurrentLine());
			
			String s = activeDoc.getActiveBuffer().getCurrentWord();
			if (s != null) {
				environment.put("TM_CURRENT_WORD", s);
			}

			if (activeDoc.getFile() != null) {
				environment.put("TM_FILENAME", activeDoc.getFile().getName());
				environment.put("TM_DIRECTORY", activeDoc.getFile().getParentFile().getPath());
				environment.put("TM_FILEPATH", activeDoc.getFile().getPath());
				
				Interval selection = activeDoc.getActiveBuffer().getSelection();
				if (selection != null && ! selection.isEmpty()) {
					String text = activeDoc.getActiveBuffer().getText(selection);
					if (text.length() > 60000) text = text.substring(0, 60000);
					environment.put("TM_SELECTED_TEXT", text);
				}
			}
		}

		environment.put("TM_PROJECT_DIRECTORY", window.getProject().getProjectDir().getPath());

		List<File> paths = Lists.newArrayList();
		
		if (bundleItemSupplier != null) {
			environment.put("TM_BUNDLE_SUPPORT", 
					new File(bundleItemSupplier.getFile().getParentFile().getParentFile(), "Support").getPath());
			paths.add(new File(bundleItemSupplier.getFile().getParentFile().getParentFile(), "Support/bin"));
		}
		
		environment.put("TM_SUPPORT_PATH", System.getProperty("supportPath"));
		paths.add(new File(System.getProperty("supportPath") + "/bin"));
		paths.add(new File(System.getProperty("supportPath") + "/" + System.getProperty("os.name") + "/bin"));

		List<File> files = Lists.newArrayList();
		if (window.getFocusedComponent() == Window.FocusedComponentType.PROJECT) {
			files = window.getProject().getSelectedFiles();
		} else {
			if (activeDoc.getFile() != null) {
				files = Collections.singletonList(activeDoc.getFile());
			}
		}
		
		if (files.isEmpty()) {
			environment.put("TM_SELECTED_FILE", "");
			environment.put("TM_SELECTED_FILES", "");
		} else {
			List<String> s = Lists.newArrayList();
			for (File f : files) {
				s.add("\"" + f.toString() + "\"");
			}
			environment.put("TM_SELECTED_FILE", "\"" + s.get(0));
			environment.put("TM_SELECTED_FILES", Joiner.on("\"").join(s));
		}
		
		
		environment.put("TM_SOFT_TABS", activeDoc.getTabManager().isSoftTabs() ? "true" : "false");
		environment.put("TM_TAB_SIZE", Integer.toString(activeDoc.getTabManager().getTabSize()));

		// Build path
		environment.put("PATH", 
				Joiner.on(File.pathSeparator).join(System.getenv("PATH"), paths));
		
	    return environment;
    }
}
