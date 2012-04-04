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
import org.eclipse.edt.compiler.internal.core.builder.NullProblemRequestor;

public class NullProblemRequestorFactory implements IProblemRequestorFactory {

	private static final NullProblemRequestorFactory INSTANCE = new NullProblemRequestorFactory();
	
	private NullProblemRequestorFactory(){}
	
	public static IProblemRequestorFactory getInstance(){
		return INSTANCE;
	}
	
	public IProblemRequestor getContainerContextTopLevelProblemRequestor(IFile file, String functionPartName, String containerContextName, IPath containerContextPath, boolean containerContextDependent) {
		return NullProblemRequestor.getInstance();
	}

	public IProblemRequestor getGenericTopLevelFunctionProblemRequestor(IFile file, String partName, boolean containerContextDependent) {
		return NullProblemRequestor.getInstance();
	}

	public IProblemRequestor getProblemRequestor(IFile file, String partName) {
		return NullProblemRequestor.getInstance();
	}

	public IProblemRequestor getFileProblemRequestor(IFile file) {
		return NullProblemRequestor.getInstance();
	}

	public ISyntaxErrorRequestor getSyntaxErrorRequestor(IFile file) {
		return new org.eclipse.edt.compiler.core.ast.NullProblemRequestor();
	}
}
