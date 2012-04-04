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
package org.eclipse.edt.ide.core.internal.utils;


/**
 * Insert the type's description here.
 * Creation date: (9/4/2001 10:19:32 AM)
 * @author: Paul R. Harmon
 */
public class PatternMatcher {
	protected Stream pattern = null;
	private Stream comparator = null;
	private int state;
	private final static int STATE_BASE = 10;
	private final static int STATE_POUND = 20;
	private final static int STATE_ASTERISK = 30;
	private final static int STATE_NOTEQUAL = -1;
	protected final static int STATE_EQUAL = 0;
	private final static int STATE_COMPARE = 40;

	/**
	* Insert the type's description here.
	* Creation date: (9/4/2001 10:46:07 AM)
	* @author: Paul R. Harmon
	*/
	public class Stream {
		private java.lang.String string;
		private int index = 0;
		/**
		 * Stream constructor comment.
		 */
		public Stream() {
			super();
		}
		/**
		 * Insert the method's description here.
		 * Creation date: (9/4/2001 10:46:57 AM)
		 * @param string java.lang.String
		 */
		public Stream(String string) {
			super();
			if (string == null) {
				this.string = ""; //$NON-NLS-1$
			} else {
				this.string = string;
			}
		}
		/**
		 * Insert the method's description here.
		 * Creation date: (9/4/2001 10:47:32 AM)
		 * @return boolean
		 */
		public boolean hasNext() {
			return index < string.length();
		}
		/**
		 * Insert the method's description here.
		 * Creation date: (9/4/2001 10:49:29 AM)
		 * @return java.lang.String
		 */
		public String next() {
			if (hasNext()) {
				index++;
				return string.substring(index - 1, index);
			} else {
				return ""; //$NON-NLS-1$
			}
		}
		/**
		 * Insert the method's description here.
		 * Creation date: (9/4/2001 10:49:46 AM)
		 * @return java.lang.String
		 */
		public String peek() {
			if (hasNext()) {
				return string.substring(index, index + 1);
			} else {
				return ""; //$NON-NLS-1$
			}
		}
		/**
		 * Insert the method's description here.
		 * Creation date: (9/4/2001 10:49:46 AM)
		 * @return java.lang.String
		 */
		public String peekToEnd() {
			if (hasNext()) {
				return string.substring(index, string.length());
			} else {
				return ""; //$NON-NLS-1$
			}
		}
		/**
		 * Insert the method's description here.
		 * Creation date: (9/4/2001 10:49:46 AM)
		 * @return java.lang.String
		 */
		public String upToEnd() {
			if (hasNext()) {
				String toEnd = peekToEnd();
				index = string.length();
				return toEnd;
			} else {
				return ""; //$NON-NLS-1$
			}
		}
		/**
		 * Insert the method's description here.
		 * Creation date: (9/4/2001 12:21:18 PM)
		 */
		public void skip() {
			index++;
		}
		/**
		 * Insert the method's description here.
		 * Creation date: (9/4/2001 12:21:18 PM)
		 */
		public void reset() {
			index = 0;
		}
	}

	private final static java.lang.String wildCards = "*#"; //$NON-NLS-1$
/**
 * PatternMatcher constructor comment.
 */
public PatternMatcher() {
	super();
}
/**
 * Insert the method's description here.
 * Creation date: (9/4/2001 10:30:52 AM)
 * @param pattern java.lang.String
 */
public PatternMatcher(String pattern) {
	super();
	this.pattern = new Stream(pattern);
}
/**
 * Insert the method's description here.
 * Creation date: (9/4/2001 10:43:05 AM)
 */
private void checkState() {
	String next;
	switch (state) {
		case STATE_BASE :
			{
				stateBase();
				break;
			}
		case STATE_COMPARE :
			{
				stateCompare();
				break;
			}
		case STATE_POUND :
			{
				statePound();
				break;
			}
		case STATE_ASTERISK :
			{
				stateAsterisk();
				break;
			}
		default :
			{

			}
	}
}
/**
 * Insert the method's description here.
 * Creation date: (9/4/2001 10:35:24 AM)
 * @return int
 * @param compareString java.lang.String
 */
protected int compareTo(String compareString) {
	pattern.reset();
	comparator = new Stream(compareString);
	state = STATE_BASE;
	
	while (!done()) {
		checkState();
	}
	
	return state;
}
/**
 * Insert the method's description here.
 * Creation date: (9/7/2001 10:13:22 AM)
 * @return boolean
 */
public boolean containsWildCards() {
	boolean found = false;
	pattern.reset();
	while (pattern.hasNext() && !found) {
		if (wildCards.indexOf(pattern.next()) >= 0) {
			found = true;
		}
	}
	pattern.reset();
	return found;
}
/**
 * Insert the method's description here.
 * Creation date: (9/6/2001 12:46:52 PM)
 * @return boolean
 */
private boolean done() {
	return (state == STATE_EQUAL)
		|| (state == STATE_NOTEQUAL);
}
/**
 * Insert the method's description here.
 * Creation date: (9/6/2001 1:11:09 PM)
 * @return boolean
 * @param string java.lang.String
 */
public boolean equals(String string) {
	return compareTo(string) == STATE_EQUAL;
}
/**
 * Insert the method's description here.
 * Creation date: (9/6/2001 12:43:31 PM)
 * @param string java.lang.String
 */
private void setPattern(String string) {
	pattern = new Stream(string);
}
/**
 * Insert the method's description here.
 * Creation date: (9/4/2001 12:10:10 PM)
 */
private void stateAsterisk() {

	pattern.skip();
	if (pattern.hasNext()) {
		PatternMatcher newMatcher = new PatternMatcher(pattern.upToEnd());
		int compareValue = STATE_NOTEQUAL;
		Integer save = null;
		while ((comparator.hasNext()) && (compareValue != STATE_EQUAL)) {
			compareValue = newMatcher.compareTo(comparator.peekToEnd());
			if (save == null) {
				save = new Integer(compareValue);
			}
			comparator.skip();
		}
		if (compareValue == STATE_EQUAL) {
			state = STATE_EQUAL;
		} else {
			if (save == null) {
				state = compareValue;
			} else {
				state = save.intValue();
			}
		}

	} else {
		state = STATE_EQUAL;
	}
}
/**
 * Insert the method's description here.
 * Creation date: (9/4/2001 12:10:10 PM)
 */
private void stateBase() {

	String next;
	
	if (!pattern.hasNext()) {
		if (!comparator.hasNext()) {
			state = STATE_EQUAL;
		} else {
			state = STATE_NOTEQUAL;
		}
	} else {
		next = pattern.peek();
		if (next.equals("#")) { //$NON-NLS-1$
			state = STATE_POUND;
		} else {
			if (next.equals("*")) { //$NON-NLS-1$
				state = STATE_ASTERISK;
			} else {
				state = STATE_COMPARE;
			}
		}
	}
}
/**
 * Insert the method's description here.
 * Creation date: (9/4/2001 12:10:10 PM)
 */
private void stateCompare() {

	if (comparator.hasNext()) {
		int result = pattern.next().compareTo(comparator.next());
		if (result == 0) {
			state = STATE_BASE;
		} else {
				state = STATE_NOTEQUAL;
		}
	} else {
		state = STATE_NOTEQUAL;
	}

}
/**
 * Insert the method's description here.
 * Creation date: (9/4/2001 12:10:10 PM)
 */
private void statePound() {

	if (comparator.hasNext()) {
		pattern.skip();
		comparator.skip();
		state = STATE_BASE;
	} else {
		state = STATE_NOTEQUAL;
	}

}
}
