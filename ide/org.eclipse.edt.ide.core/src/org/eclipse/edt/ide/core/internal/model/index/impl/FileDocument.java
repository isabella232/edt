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
package org.eclipse.edt.ide.core.internal.model.index.impl;

import java.io.File;
import java.io.IOException;

import org.eclipse.edt.ide.core.internal.model.util.Util;
import org.eclipse.edt.ide.core.model.EGLCore;


/**
 * A <code>FileDocument</code> represents a java.io.File.
 */

public class FileDocument extends PropertyDocument {
	File file;

	public FileDocument(File file) {
		super();
		this.file= file;
	}
	/**
	 * @see IDocument#getByteContent
	 */
	public byte[] getByteContent() throws IOException {
		return Util.getFileByteContent(file);
	}
	/**
	 * @see IDocument#getCharContent
	 */
	public char[] getCharContent() throws IOException {
		return Util.getFileCharContent(file, null);
	}
	/**
	 * @see org.eclipse.edt.ide.core.internal.model.index.jdt.internal.core.index.IDocument#getEncoding()
	 */
	public String getEncoding() {
		return EGLCore.getOption(EGLCore.CORE_ENCODING); // no custom encoding
	}

	/**
	 * @see IDocument#getName
	 */
	public String getName() {
		return file.getAbsolutePath().replace(File.separatorChar, IIndexConstants.FILE_SEPARATOR);
	}
	/**
	 * @see IDocument#getStringContent
	 */
	public String getStringContent() throws IOException {
		return new String(getCharContent());
	}
	/**
	 * @see IDocument#getType
	 */
	public String getType() {
		int lastDot= file.getPath().lastIndexOf('.');
		if (lastDot == -1)
			return ""; //$NON-NLS-1$
		return file.getPath().substring(lastDot + 1);
	}
}
