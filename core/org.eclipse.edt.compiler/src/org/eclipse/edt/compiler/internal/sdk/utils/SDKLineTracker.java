/*******************************************************************************
 * Copyright © 2011, 2013 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.edt.compiler.internal.sdk.utils;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class SDKLineTracker {

	public class LineInfo{
		public int offset;
		public int length;
		public LineInfo(int oset,int len){
			offset = oset;
			length = len;
		}
		public int getLength() {
			return length;
		}
		public int getOffset() {
			return offset;
		}
		
	}
	
	String fileContents;

	int[] lineOffsets;

	public SDKLineTracker() {
		super();
	
	}

	public void set(String fileContents){
		this.fileContents = fileContents;	
	}
	
	public LineInfo getLineInfo(int linenum){
		int[] offsets = getOffsetsForLine(linenum);
		return new LineInfo(offsets[0], offsets[1] - offsets[0] );
	}
	
	public int getLineOffset(int linenum){
		return getLineInfo(linenum).offset;
	}
	
	public int[] getLineOffsets() {

		if (lineOffsets != null) {
			return lineOffsets;
		}

		if (fileContents == null) {
			lineOffsets = new int[0];
			return lineOffsets;
		}

		List list = new ArrayList();
		list.add(new Integer(0));
		int length = fileContents.length();
		char myChar;
		for (int i = 0; i < length; i++) {
			myChar = fileContents.charAt(i);

			if (myChar == '\n') {
				list.add(new Integer(i + 1));
			}

			if (myChar == '\r') {
				if (i + 1 < length) {
					if (fileContents.charAt(i + 1) == '\n') {
						list.add(new Integer(i + 2));
						i = i + 1;
					} else {
						list.add(new Integer(i + 1));
					}
				}
			}
		}

		int[] lines = new int[list.size() + 1];
		lines[0] = -1;
		int index = 1;
		Iterator i = list.iterator();
		while (i.hasNext()) {
			Integer x = (Integer) i.next();
			lines[index] = x.intValue();
			index = index + 1;
		}
		lineOffsets = lines;
		return lineOffsets;
	}

	private int getContentsLength() {
		if (fileContents == null) {
			return 0;
		}
		return fileContents.length();

	}

	
	public int[] getOffsetsForLine(int line) {
		if (line == 0) {
			return new int[] { 0, 0 };
		}

		int[] lines = getLineOffsets();

		// lines is out of bounds, just return the start ande end offsets of the
		// last line
		if (line >= getLineOffsets().length) {
			if (lines.length == 0) {
				return new int[] { 0, getContentsLength() };
			}
			return new int[] { lines[lines.length - 1], getContentsLength() };
		}

		int start = lines[line];
		int end;
		if (line == lines.length - 1) {
			end = getContentsLength();
		} else {
			end = lines[line + 1];
		}

		return new int[] { start, end };

	}

	public int getLineNumberOfOffset(int offset){
		int[] lineoffsets = getLineOffsets();
		
		if (lineoffsets.length == 2){
			return 1;
		}
		
		if (offset > getContentsLength()){
			return lineoffsets.length - 1;
		}
		
		for (int i = 2; i < lineoffsets.length; i++){
			if (offset < lineoffsets[i]){
				return i - 1;
			}
				
		}
		
		return lineoffsets.length - 1;
	}

}
