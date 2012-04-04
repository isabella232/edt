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
package org.eclipse.edt.ide.core.internal.compiler.workingcopy;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IPath;
import org.eclipse.edt.compiler.core.ast.ISyntaxErrorRequestor;
import org.eclipse.edt.compiler.internal.core.builder.IProblemRequestor;

public interface IProblemRequestorFactory {
	ISyntaxErrorRequestor getSyntaxErrorRequestor(IFile file);
	IProblemRequestor getProblemRequestor(IFile file, String partName);
	IProblemRequestor getFileProblemRequestor(IFile file);
	IProblemRequestor getGenericTopLevelFunctionProblemRequestor(IFile file, String partName, boolean containerContextDependent);
	IProblemRequestor getContainerContextTopLevelProblemRequestor(IFile file, String functionPartName, String containerContextName, IPath containerContextPath, boolean containerContextDependent);
	
	// TODO Add support for returning "file problems" (duplicate parts in the workspace or file, generatable part error messages (two generatable parts in a file, file doesn't match part name))
	// TODO Add support for returning "syntax problems"
}
