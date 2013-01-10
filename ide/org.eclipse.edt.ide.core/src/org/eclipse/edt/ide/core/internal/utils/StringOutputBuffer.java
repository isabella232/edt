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
package org.eclipse.edt.ide.core.internal.utils;

import java.io.IOException;



/**
 * Insert the type's description here.
 * Creation date: (1/18/2002 1:06:18 PM)
 * @author: Paul R. Harmon
 */
public class StringOutputBuffer extends java.io.OutputStream {
	private final static int BUFFER_INCREMENT = 200;
	private byte[] buffer = new byte[BUFFER_INCREMENT];
		private int bufpos = 0;
/**
 * StringOutputBuffer constructor comment.
 */
public StringOutputBuffer() {
	super();
}
/**
 * Insert the method's description here.
 * Creation date: (1/18/2002 1:18:16 PM)
 */
public void extendBuffer() {
	byte[] newBuffer = new byte[buffer.length + BUFFER_INCREMENT];
	System.arraycopy(buffer, 0, newBuffer, 0, buffer.length);
	buffer = newBuffer;
}
/**
 * Insert the method's description here.
 * Creation date: (1/18/2002 1:24:26 PM)
 * @return java.lang.String
 */
public String toString() {
	byte[] newBuffer = new byte[bufpos - 1];
	System.arraycopy(buffer, 0, newBuffer, 0, bufpos - 1);
	return new String(newBuffer);
}
/**
 * Writes the specified byte to this output stream. The general 
 * contract for <code>write</code> is that one byte is written 
 * to the output stream. The byte to be written is the eight 
 * low-order bits of the argument <code>b</code>. The 24 
 * high-order bits of <code>b</code> are ignored.
 * <p>
 * Subclasses of <code>OutputStream</code> must provide an 
 * implementation for this method. 
 *
 * @param      b   the <code>byte</code>.
 * @exception  IOException  if an I/O error occurs. In particular, 
 *             an <code>IOException</code> may be thrown if the 
 *             output stream has been closed.
 */
public void write(int b) throws java.io.IOException {

	buffer[bufpos] = (byte) b;
	bufpos = bufpos + 1;

	if (bufpos == buffer.length) {
		extendBuffer();
	}
}
}
