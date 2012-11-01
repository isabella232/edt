package org.eclipse.edt.compiler.core.dev.tools;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * For all messages that used the format "123={0} - <msg>. At line {x} in file {y}." it strips out the <msg> and decrements all inserts. These extra
 * inserts were inserted for TLFs but that has been removed, so the extra inserts now throw off the messages.
 */
public class RemoveInsertShifts {
	public static void main(String[] args) throws Exception {
		BufferedReader br = null;
		try {
			File file = new File("src/org/eclipse/edt/compiler/internal/core/builder/EGLValidationResources.properties");
			br = new BufferedReader(new FileReader(file));
			
			StringBuilder buf = new StringBuilder((int)file.length());
			String NL = System.getProperty("line.separator");
			
			String line;
			while ((line = br.readLine()) != null) {
				StringBuilder toAppend = new StringBuilder(line);
				
				String s = line.trim();
				if (!s.startsWith("#") && s.contains("=")) {
					String msg = s.substring(s.indexOf('=') + 1).trim();
					if (msg.startsWith("{0} - ") && msg.contains(" At line {")) {
						toAppend = new StringBuilder();
						toAppend.append(s.substring(0, s.indexOf('=') + 1));
						int end = msg.indexOf( " At line {" );
						toAppend.append(msg.substring(6, end));
						
						// Decrement all inserts
						Pattern p = Pattern.compile("\\{([0-9])\\}");
						Matcher m = p.matcher(toAppend);
						while (m.find()) {
							String match = m.group(1);
							// Always 1 char so the offsets never change as we edit.
							int val = Integer.parseInt(match);
							val--;
							toAppend.setCharAt(m.start(1), Integer.toString(val).charAt(0));
						}
					}
				}
				buf.append(toAppend);
				buf.append(NL);
			}
			
			System.out.println(buf.toString());
		}
		finally {
			if (br != null) {
				br.close();
				br = null;
			}
		}
	}
	
}
