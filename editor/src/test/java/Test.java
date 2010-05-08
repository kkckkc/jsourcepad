import java.io.File;
import java.util.Set;
import java.util.TreeSet;


public class Test {
	int i = 0;
	
	public static void main(String... args) {
		for (int i = 0; i < 20; i++) {
			long l = System.currentTimeMillis();
			Test test = new Test();
			
			TreeSet<String> dest = new TreeSet<String>();
			test.recurse(dest, "asp", new File("/home/magnus"));
			System.out.println(dest.iterator().next() + " " + (System.currentTimeMillis() - l));
		}
	}

	private void recurse(Set<String> dest, String s, File file) {
		if (i++ > 10000) return;
		
		if (! file.isDirectory()) {
			if (matches(file.getName(), s)) dest.add(file.getName());
			
			return;
		}
		
		File[] children = file.listFiles();
		if (children == null) return;
		
		for (File f : children) {
			recurse(dest, s, f);
		}
	}

	private boolean matches(String name, String matchWith) {
		int i = 0;

a:
		for (int j = 0; j < matchWith.length(); j++) {
			for (; i < name.length(); i++) {
				if (name.charAt(i) == matchWith.charAt(j)) {
					continue a;
				}
			}
			return false;
		}
		
		return true;
	}
}
