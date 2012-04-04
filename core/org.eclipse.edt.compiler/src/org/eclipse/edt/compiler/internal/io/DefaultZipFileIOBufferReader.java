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

import java.io.File;
import java.io.IOException;

public class DefaultZipFileIOBufferReader extends CommonZipFileIOBufferReader {
	private String filename;
	
	public DefaultZipFileIOBufferReader(String filename) {
		this.filename = filename;
	}
	
	public File getFile() {
		return new File(filename);
	}

	@Override
	public Object readEntry(String name) throws IOException {
		// TODO Auto-generated method stub
		return null;
	}

}
