/*******************************************************************************
 * Copyright © 2010, 2012 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.edt.ide.core.internal.model.search;

import java.io.IOException;

import org.eclipse.core.runtime.IPath;
import org.eclipse.edt.compiler.internal.eglar.FileInEglar;


public class EGLSearchDocument extends SearchDocument {
	protected byte[] byteContents;
	protected char[] charContents;
//private IDocument 
	public EGLSearchDocument(java.util.zip.ZipEntry zipEntry, IPath zipFilePath, byte[] contents, SearchParticipant participant) {
		super(zipFilePath + FileInEglar.EGLAR_SEPARATOR + zipEntry.getName(), participant);
		this.byteContents = contents;
	}

	protected EGLSearchDocument(String documentPath, SearchParticipant participant) {
		super(documentPath, participant);
	}

	public byte[] getByteContent() {
		return this.byteContents;
	}

	public char[] getCharContent() {
		return charContents;
	}

	public String getEncoding() {
		return null;
	}

	public String getName() {
		return getPath();
	}

	public String getStringContent() throws IOException {
		return new String(getCharContent());
	}

	public String getType() {
		return "eglxml";
	}

}
