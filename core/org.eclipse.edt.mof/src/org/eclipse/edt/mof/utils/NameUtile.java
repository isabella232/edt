package org.eclipse.edt.mof.utils;

public class NameUtile {
	public static String getAsName(String string) {
		if (string == null) {
			return null;
		}
		return string.toUpperCase().toLowerCase();
	}
	
	public static String getAsCaseSensitiveName(String string) {
		return string;
	}
	
	public static boolean equals(String str1, String str2) {
		if (str1 == str2) {
			return true;
		}
		
		if (str1 == null || str2 == null) {
			return false;
		}
		
		return str1.equals(str2);
		
	}
}
