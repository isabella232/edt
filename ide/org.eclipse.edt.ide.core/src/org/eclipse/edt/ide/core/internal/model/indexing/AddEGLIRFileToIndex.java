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
package org.eclipse.edt.ide.core.internal.model.indexing;

import java.io.IOException;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IPath;
import org.eclipse.edt.ide.core.internal.model.index.IIndex;
import org.eclipse.edt.ide.core.internal.model.index.impl.IFileDocument;
import org.eclipse.edt.ide.core.internal.model.util.Util;


public class AddEGLIRFileToIndex extends AddFileToIndex {
	byte[] contents;

	public AddEGLIRFileToIndex(IFile resource, IPath indexedContainer,
			IndexManager manager) {
		super(resource, indexedContainer, manager);
	}

	protected boolean indexDocument(IIndex index) throws IOException {
		if (!initializeContents())
			return false;
		index.add(new IFileDocument(resource, this.contents),
				new BinaryIndexer());
		return true;
	}

	public boolean initializeContents() {
		if (this.contents == null) {
			IPath location = resource.getLocation();
			if (location != null) {
				try {
					this.contents = Util.getFileByteContent(location.toFile());
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		return this.contents != null;
	}
}
