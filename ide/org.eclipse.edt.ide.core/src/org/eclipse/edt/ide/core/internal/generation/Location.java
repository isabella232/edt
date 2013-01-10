/*******************************************************************************
 * Copyright © 2000, 2013 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.edt.ide.core.internal.generation;


import java.io.Serializable;

import org.eclipse.edt.compiler.internal.interfaces.IEGLLocation;

/**
 * Insert the type's description here.
 * Creation date: (8/6/2001 11:08:49 AM)
 * @author: Paul R. Harmon
 */
public class Location implements IEGLLocation, Serializable {
	private int line;
	private int column;
	private int offset;
	private int length;
	private int endLine;

	/**
	 * Location constructor comment.
	 */
	public Location() {
		super();
	}
	public Location(int line, int column, int offset) {
		this(line, column, offset, 0);
	}
	
	public Location(int line, int column, int offset, int length) {
		this.line = line;
		this.column = column;
		this.offset = offset;
		this.length = length;
	}
	/**
	 * Compares its two Locations for order, using line and column.
	 *   Returns a negative integer,
	 * zero, or a positive integer as the first argument is less than, equal
	 * to, or greater than the second.<p>
	 *
	 * @return a negative integer, zero, or a positive integer as the
	 *         first argument is less than, equal to, or greater than the
	 *         second. 
	 */
	public int compareTo(Location location) {
		if (this.line == location.line) {

			if (this.column == location.column) {
				return 0;

			} else if (this.column < location.column) {
				return -1;

			} else {
				return 1;
			}

		} else if (this.line < location.line) {
			return -1;

		} else {
			return 1;

		}
	}
	/**
	 * Whether or not two locations are equal.
	 * @boolean
	 */
	public boolean equals(Location location) {
		if (this.compareTo(location) == 0)
			return true;
		else
			return false;
	}
	/**
	 * Whether or not two locations are equal.
	 * @boolean
	 */
	public boolean equals(Object object) {
		if (object instanceof Location)
			return this.equals((Location) object);
		else
			return false;
	}
	public int getColumn() {
		return column;
	}
	public int getLine() {
		return line;
	}
	public int getOffset() {
		return offset;
	}
	/**
	 * @return int A hashcode for this Location
	 */
	public int hashCode() {
		return toString().hashCode();
	}
	public void setColumn(int column) {
		this.column = column;
	}
	public void setLine(int line) {
		this.line = line;
	}
	public void setOffset(int offset) {
		this.offset = offset;
	}
	/**
	 * @return String A String representation of this Location
	 */
	public String toString() {
		return "Line: " + line + " Column: " + column + " Offset:  " + offset; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
	}
	/**
	 * @return
	 */
	public int getEndLine() {
		return endLine;
	}

	/**
	 * @return
	 */
	public int getLength() {
		return length;
	}

	/**
	 * @param i
	 */
	public void setEndLine(int i) {
		endLine = i;
	}

	/**
	 * @param i
	 */
	public void setLength(int i) {
		length = i;
	}

}
