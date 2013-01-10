/*******************************************************************************
 * Copyright © 2005, 2013 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.edt.ide.core.internal.lookup;

import java.io.IOException;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.edt.compiler.core.ast.File;

/**
 * @author svihovec
 * 
 * TODO What about files with the same name but different cases on Linux?
 *
 */
public class ResourceFileInfoCreator extends AbstractFileInfoCreator {

	private String contents = null;
	
	public ResourceFileInfoCreator(AbstractProjectInfo projectInfo, String packageName, IFile file, File fileAST, String fileContents, IDuplicatePartRequestor duplicatePartRequestor){
		super(projectInfo, packageName, file, fileAST, duplicatePartRequestor);
		
		this.contents = fileContents;
	}
	
	protected String getContents() throws CoreException, IOException {
		return contents;
	}
}
