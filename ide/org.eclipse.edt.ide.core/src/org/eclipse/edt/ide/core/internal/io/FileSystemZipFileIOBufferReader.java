/*******************************************************************************
 * Copyright © 2005, 2012 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.edt.ide.core.internal.io;

import java.io.File;

import org.eclipse.core.runtime.IPath;


/**
 * @author svihovec
 *
 */
public class FileSystemZipFileIOBufferReader extends ZipFileIOBufferReader {

	private IPath outputPath;
	
	public FileSystemZipFileIOBufferReader(IPath bufferPath) {
		this.outputPath = bufferPath;
	}


	public File getFile() {
		return outputPath.toFile();
	}

}
