package util;

public class StringUtil {
	public static String repeat(String string, int n) {
		StringBuilder sb = new StringBuilder();
		for(int i = 0; i < n; i++) {
			sb.append(string);
		}
		return sb.toString();
	}
}
