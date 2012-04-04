/*******************************************************************************
 * Copyright © 2000, 2012 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.edt.ide.core.internal.errors;

import java.util.ArrayList;

/**
 * @author winghong
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public class ErrorLineTracker {
	private final static String[] DELIMITERS= { "\r\n" }; 
	
	private String contents;
	private ArrayList lines = new ArrayList();
	
	private static class LineInfo {
		public int start;
		public int length;
		
		public LineInfo(int start, int length) {
			this.start = start;
			this.length = length;
		}
		
		public String toString() {
			return "[" + start + "," + length + "]";
		}
	}
	
	public ErrorLineTracker(String contents) {
		this.contents = contents;
		createLineInfo();
	}
	
	public String getLine(int line) {
		if(line >= getNumberOfLines()) {
			return null;
		}
		
		LineInfo lineInfo = (LineInfo) lines.get(line);
		return contents.substring(lineInfo.start, lineInfo.length + lineInfo.start);
	}
	
	public int getNumberOfLines() {
		return lines.size();
	}
	
	private void createLineInfo() {
		int startPos = 0;
		for(;;) {
			String delimiter = null;
			int firstDelimiterPos = contents.length();
			for (int i = 0; i < DELIMITERS.length; i++) {
				int curDelimiterPos = contents.indexOf(DELIMITERS[i], startPos);
				if(curDelimiterPos >= 0 && curDelimiterPos < firstDelimiterPos) {
					 delimiter = DELIMITERS[i];
					 firstDelimiterPos = curDelimiterPos;
				}
			}
			
			int nextStartPos = (delimiter == null ? contents.length() : firstDelimiterPos + delimiter.length());
			lines.add(new LineInfo(startPos, nextStartPos - startPos));
			startPos = nextStartPos;
			
			if(delimiter == null) return;
		}
	}
	
	public static void main(String[] args) {
		ErrorLineTracker lineTracker = new ErrorLineTracker(FileReaderUtil.readFile("file17.egl"));
		for (int i = 0; i < lineTracker.lines.size(); i++) {
			System.out.println(lineTracker.getLine(i));
		}
	}
}
