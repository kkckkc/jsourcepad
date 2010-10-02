package kkckkc.utils;

public class CharSequenceUtils {
	public static boolean startsWith(CharSequence a, CharSequence b) {
		int len = b.length();
		if (a.length() < len) return false;
		
		for (int i = 0; i < len; i++) {
			if (a.charAt(i) != b.charAt(i) || i >= a.length()) {
				return false;
			}
		}
		return true;
	}

    public static boolean isWhitespace(CharSequence charSequence) {
         if (charSequence.length() == 0) return true;
         for (int i = 0; i < charSequence.length(); i++) {
             char c = charSequence.charAt(i);
             if (! Character.isWhitespace(c)) return false;
         }
         return true;
     }
    
}
