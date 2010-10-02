public class Test {
	int i = 0;
	
	public static void main(String... args) {

        java.util.regex.Pattern pattern = java.util.regex.Pattern.compile("\\(\\?([0-9]+):([^):]+)(:([^)]+))?\\)");
        java.util.regex.Matcher m = pattern.matcher("Lorem(?5:kalle)olle(?6:nisse:pelle)kaslkas");

        StringBuffer b = new StringBuffer();
        while (m.find()) {
            System.out.println(m.group(1));
            System.out.println(m.group(2));
            if (m.group(4) != null) {
                System.out.println(m.group(4));
            }
            System.out.println("-------------------");
            m.appendReplacement(b, "***");
        }

        m.appendTail(b);

        System.out.println(b);
	}
}
