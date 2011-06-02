/*
 * Licensed Materials - Property of IBM
 *
 * Copyright IBM Corporation 2008, 2010. All Rights Reserved.
 *
 * U.S. Government Users Restricted Rights - Use, duplication or
 * disclosure restricted by GSA DP Schedule Contract with IBM Corp.
 */
package org.eclipse.edt.mof.egl.utils;

public class TimeStampAndIntervalPatternFixer {

	PatternEntry first;

	private static class PatternEntry {
		String value;

		PatternEntry next;

		PatternEntry previous;
		
		public PatternEntry(String value) {
			super();
			this.value = value;
		}

		boolean isYear() {
			return value.toUpperCase().toLowerCase().startsWith("y");
		}

		boolean isDay() {
			return value.toUpperCase().toLowerCase().startsWith("d");
		}

		boolean isMonth() {
			if (!value.toUpperCase().toLowerCase().startsWith("m")) {
				return false;
			}
			if (previous != null) {
				return previous.isYear();
			}
			if (next != null) {
				return next.isDay();
			}

			return value.startsWith("M");
		}

		boolean isHour() {
			return value.toUpperCase().toLowerCase().startsWith("h");
		}

		public String toString() {
			if (isMonth() || isHour()) {
				return value.toUpperCase();
			}
			return value.toLowerCase();
		}

		public PatternEntry getNext() {
			return next;
		}

		public void setNext(PatternEntry next) {
			this.next = next;
		}

		public PatternEntry getPrevious() {
			return previous;
		}

		public void setPrevious(PatternEntry previous) {
			this.previous = previous;
		}

		public String getValue() {
			return value;
		}

		public void setValue(String value) {
			this.value = value;
		}

	}

	public TimeStampAndIntervalPatternFixer(String pattern) {
		super();
		first = parse(pattern);
	}

	private PatternEntry parse(String pattern) {
		
		if (pattern == null) {
			return null;
		}
		
		PatternEntry firstEntry = null;
		PatternEntry currentEntry = null;
		
		String curPattern = null;
		String curChar = null;
		String prevChar = null;

		for (int i = 0; i < pattern.length(); i++) {
			curChar = pattern.substring(i, i + 1);

			if (prevChar == null) {
				curPattern = curChar;
			} else {

				if (curChar.equalsIgnoreCase(prevChar)) {
					curPattern = curPattern + curChar;
				} else {
					PatternEntry newEntry = new PatternEntry(curPattern);
					if (firstEntry == null) {
						firstEntry = newEntry;
					}
					else {
						currentEntry.setNext(newEntry);
						newEntry.setPrevious(currentEntry);
					}
					currentEntry = newEntry;
					curPattern = curChar;
				}
			}

			prevChar = curChar;
		}
		
		//add in the last entry
		PatternEntry newEntry = new PatternEntry(curPattern);
		if (firstEntry == null) {
			firstEntry = newEntry;
		}
		else {
			currentEntry.setNext(newEntry);
			newEntry.setPrevious(currentEntry);
		}
		currentEntry = newEntry;
		curPattern = "";

		
		return firstEntry;
	}
	
	public String toString() {
		if (first == null) {
			return null;
		}
		
		StringBuffer buffer = new StringBuffer();
		PatternEntry curEntry = first;
		while (curEntry != null) {
			buffer.append(curEntry.toString());
			curEntry = curEntry.next;
		}
		return buffer.toString();
	}

}
