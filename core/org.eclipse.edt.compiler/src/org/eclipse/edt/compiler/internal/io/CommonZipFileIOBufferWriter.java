/*******************************************************************************
 * Copyright © 2011, 2012 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.edt.compiler.internal.io;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;


/**
 * @author svihovec
 *
 */
public abstract class CommonZipFileIOBufferWriter implements IIOBufferWriter {

	private ZipOutputStream zipOutputStream;
	
	public void beginWriting() throws IOException {
		zipOutputStream = new ZipOutputStream(new BufferedOutputStream(createOutputStream()));
	}

	public void writeEntry(String entryName, Object value) throws IOException {
		ZipEntry newEntry = new ZipEntry(entryName); //$NON-NLS-1$
		zipOutputStream.putNextEntry(newEntry);
		ObjectOutputStream objectOutputStream = new ObjectOutputStream(zipOutputStream);
		objectOutputStream.writeObject(value);
		zipOutputStream.flush();
	}

	public void finishWriting() throws IOException {
		zipOutputStream.close();
	}
	
	protected abstract OutputStream createOutputStream() throws IOException;

}
