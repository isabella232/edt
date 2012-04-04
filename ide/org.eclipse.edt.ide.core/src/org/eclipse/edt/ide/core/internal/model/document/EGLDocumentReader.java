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
package org.eclipse.edt.ide.core.internal.model.document;

import java.io.IOException;
import java.io.Reader;

import org.eclipse.jface.text.BadLocationException;

/**
 * @author winghong
 */
public class EGLDocumentReader extends Reader {

	private EGLDocument document;

	private int length;

	private int next;

	public EGLDocumentReader(EGLDocument document) {
		super();
		this.document = document;
		this.length = document.getLength();
	}
	
	public EGLDocumentReader(EGLDocument document, int startOffset) {
		super();
		this.document = document;
		this.next = startOffset;
		this.length = document.getLength();
	}

	/* (non-Javadoc)
	 * @see java.io.Reader#read(char[], int, int)
	 */
	public int read(char[] cbuf, int off, int len) throws IOException {
		if ((off < 0) || (off > cbuf.length) || (len < 0) || ((off + len) > cbuf.length) || ((off + len) < 0)) {
			throw new IndexOutOfBoundsException();
		} else if (len == 0) {
			return 0;
		}
		if (next >= length)
			return -1;
		int n = Math.min(length - next, len);
		try {
			document.get(next, n).getChars(0, n, cbuf, off);
		} catch (BadLocationException e) {
			throw new IOException();
		}
		next += n;
		return n;
	}
	
	/* (non-Javadoc)
	 * @see java.io.Reader#read()
	 */
	public int read() throws IOException {
		try {
			return next >= length ? -1 : document.getChar(next++);
		} catch (BadLocationException e) {
			throw new IOException();
		}
	}

	/* (non-Javadoc)
	 * @see java.io.Reader#close()
	 */
	public void close() throws IOException {
	}

}
