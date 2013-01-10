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
package org.eclipse.edt.ide.core.internal.model.indexing;

import java.io.IOException;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.edt.ide.core.internal.model.index.IIndex;
import org.eclipse.edt.ide.core.internal.model.index.impl.IFileDocument;
import org.eclipse.edt.ide.core.internal.model.util.Util;
import org.eclipse.edt.ide.core.model.EGLCore;


class AddEGLFileToIndex extends AddFileToIndex {
	char[] contents;

	public AddEGLFileToIndex(IFile resource, IPath indexedContainer, IndexManager manager) {
		super(resource, indexedContainer, manager);
	}
	protected boolean indexDocument(IIndex index) throws IOException {
		if (!initializeContents()) return false;
		index.add(new IFileDocument(resource, this.contents), new SourceIndexer(resource));
		return true;
	}
	public boolean initializeContents() {
		if (this.contents == null) {
			try {
				IPath location = resource.getLocation();
				if (location != null){
					try {
						String encoding = resource.getCharset();
						
						if(encoding == null)	//get the default encoding setting
							encoding = EGLCore.create(resource.getProject()).getOption(EGLCore.CORE_ENCODING, true);
						
						this.contents = Util.getFileCharContent(location.toFile(), encoding);
					} catch (CoreException e) {						
					}				
				}
					
			} catch (IOException e) {
			}
		}
		return this.contents != null;
	}
}
