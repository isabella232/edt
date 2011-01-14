/*******************************************************************************
 * Copyright © 2010 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.edt.mof.egl.compiler;

import org.eclipse.edt.compiler.internal.io.ZipFileBuildPathEntry;
import org.eclipse.edt.mof.serialization.IZipFileEntryManager;


public class SystemPackageMOFPathEntry extends ZipFileBuildPathEntry implements IZipFileEntryManager{

	private String fileExtension;

	public SystemPackageMOFPathEntry(String path, String fileExtension) {
		super(path);

		this.fileExtension = fileExtension;
		processEntries();
	}

	protected String getFileExtension() {
		return fileExtension;
	}
	
	public boolean hasEntry(String entry) {
		
		String[] entries = getAllEntries();
		for (int i = 0; i < entries.length; i++) {
			if (entry.equals(entries[i])) {
				return true;
			}
		}
		return false;
	}


}
