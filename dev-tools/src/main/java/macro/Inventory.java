package macro;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import kkckkc.syntaxpane.util.plist.GeneralPListReader;

public class Inventory {
	private static Set<String> commandset = new HashSet<String>();
	
	public static void main(String... args) throws IOException {
		recurse(new File("/home/magnus/.jsourcepad/Shared/Bundles"));
		
		for (String s: commandset) {
			System.out.println(s);
		}
		
		System.out.println("--------------------------");
		System.out.println(commandset.size());
	}

	private static void recurse(File dir) throws IOException {
	    for (File file : dir.listFiles()) {
	    	if (file.isDirectory()) {
	    		recurse(file);
	    	} else if (file.getName().endsWith("tmMacro") || (file.isFile() && dir.getName().endsWith("Macros"))) {
	    		GeneralPListReader gpl = new GeneralPListReader();
	    		Map m = (Map) gpl.read(file);
	    		
	    		List<Map> commands = (List<Map>) m.get("commands");
	    		for (Map cmd : commands) {
	    			commandset.add((String) cmd.get("command"));
	    		}
	    		//System.out.println(new PListFormatter().format(m));
	    	}
	    }
    }
}
