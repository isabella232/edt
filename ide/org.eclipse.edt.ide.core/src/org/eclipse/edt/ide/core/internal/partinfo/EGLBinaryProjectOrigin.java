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
package org.eclipse.edt.ide.core.internal.partinfo;

import org.eclipse.core.resources.IFile;

public class EGLBinaryProjectOrigin implements IPartOrigin {
	IFile file;
	
	
	
	public EGLBinaryProjectOrigin(IFile file) {
		super();
		this.file = file;
	}

	public IFile getEGLFile() {
		return file;
	}

	public boolean isOriginEGLFile() {
		return false;
	}

	public boolean isSourceCodeAvailable() {
		return false;
	}

}
