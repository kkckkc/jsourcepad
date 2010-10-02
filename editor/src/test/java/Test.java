import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Test {
	int i = 0;
	
	public static void main(String... args) throws MalformedURLException, UnsupportedEncodingException {

        String path = "/open/?url=http://localhost:8080/files/Users/magnus/Documents/Projects/jsourcepad/editor/src/main/java/kkckkc/jsourcepad/action/EditPasteFromHistoryAction.java&line=41";

        String cmd = path.substring(1, path.indexOf("/", 2));

        System.out.println("cmd = " + cmd);

        Map<String, List<String>> params = new HashMap<String, List<String>>();
String[] urlParts = path.split("\\?");
if (urlParts.length > 1) {
    String query = urlParts[1];
    for (String param : query.split("&")) {
        String[] pair = param.split("=");
        String key = URLDecoder.decode(pair[0], "UTF-8");
        String value = URLDecoder.decode(pair[1], "UTF-8");
        List<String> values = params.get(key);
        if (values == null) {
            values = new ArrayList<String>();
            params.put(key, values);
        }
        values.add(value);
    }
}

        System.out.println("params = " + params);
	}
}
