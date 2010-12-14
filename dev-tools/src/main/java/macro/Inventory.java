package macro;

import kkckkc.jsourcepad.model.bundle.MacroEngine;
import kkckkc.utils.plist.GeneralPListReader;
import kkckkc.utils.plist.PListFormatter;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Inventory {
	private static Map<String, String> commandset = new HashMap<String, String>();
	
	public static void main(String... args) throws IOException {
		recurse(new File("/home/magnus/.jsourcepad/Shared/Bundles"));
		
		int i = 0;
		for (String s: commandset.keySet()) {
			if (MacroEngine.commands.containsKey(s)) continue;
			
			i++;
			System.out.println(s);
			System.out.println(commandset.get(s));
		}
		
		System.out.println("--------------------------");
		System.out.println(i);
	}

	private static void recurse(File dir) throws IOException {
	    for (File file : dir.listFiles()) {
	    	if (file.isDirectory()) {
	    		recurse(file);
	    	} else if (file.getName().endsWith("tmMacro") || (file.isFile() && dir.getName().endsWith("Macros"))) {
	    		GeneralPListReader gpl = new GeneralPListReader();
	    		Map props = (Map) gpl.read(file);
	    		
	    		List<Map> commands = (List<Map>) props.get("commands");
	    		for (Map cmd : commands) {
	    			commandset.put((String) cmd.get("command"), new PListFormatter().format(cmd));
	    		}
	    		//System.out.println(new PListFormatter().format(m));
	    	}
	    }
    }
}
