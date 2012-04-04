/*******************************************************************************
 * Copyright © 2008, 2012 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.edt.ide.core.internal.lookup.workingcopy;

import org.eclipse.core.resources.IFile;
import org.eclipse.edt.compiler.core.ast.File;
import org.eclipse.edt.ide.core.internal.lookup.AbstractProjectInfo;
import org.eclipse.edt.ide.core.internal.lookup.IDuplicatePartRequestor;
import org.eclipse.edt.ide.core.internal.lookup.ResourceFileInfoCreator;

/**
 * Create a FileInfo that is based on an IFile.
 */
public class WorkingCopyCompilerResourceFileInfoCreator extends ResourceFileInfoCreator {
	private boolean checkForDuplicates;
	
	public WorkingCopyCompilerResourceFileInfoCreator(AbstractProjectInfo projectInfo, String[] packageName, IFile file, File fileAST, String fileContents, IDuplicatePartRequestor duplicatePartRequestor, boolean checkForDuplicates) {
		super(projectInfo, packageName, file, fileAST, fileContents, duplicatePartRequestor);
		this.checkForDuplicates = checkForDuplicates;
	}
	
	protected boolean checkForDuplicates(){
		return checkForDuplicates;
	}
}
