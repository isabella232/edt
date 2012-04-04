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
package org.eclipse.edt.compiler.internal.sdk.compile;

import java.io.File;

import org.eclipse.edt.compiler.core.ast.ISyntaxErrorRequestor;
import org.eclipse.edt.compiler.internal.core.builder.FunctionContainerContextTopLevelFunctionProblemRequestor;
import org.eclipse.edt.compiler.internal.core.builder.GenericTopLevelFunctionProblemRequestor;
import org.eclipse.edt.compiler.internal.core.builder.IProblemRequestor;


public class DefaultSDKProblemRequestorFactory implements ISDKProblemRequestorFactory {

	public IProblemRequestor getProblemRequestor(File file, String part) {
		return new SDKProblemRequestor(file, part);
	}

	public IProblemRequestor getGenericTopLevelFunctionProblemRequestor(File file, String partName, boolean containerContextDependent) {
		return new GenericTopLevelFunctionProblemRequestor(new SDKProblemRequestor(file, partName), containerContextDependent);
	}

	public IProblemRequestor getContainerContextTopLevelProblemRequestor(File file, String containerContextName,
			boolean containerContextDependent) {
		return new FunctionContainerContextTopLevelFunctionProblemRequestor(new SDKProblemRequestor(file, containerContextName),
				containerContextDependent);
	}

	public ISyntaxErrorRequestor getSyntaxErrorRequestor(File file) {
		return new SDKSyntaxErrorRequestor(new SDKSyntaxProblemRequestor(file, "SYN"));
	}

}
