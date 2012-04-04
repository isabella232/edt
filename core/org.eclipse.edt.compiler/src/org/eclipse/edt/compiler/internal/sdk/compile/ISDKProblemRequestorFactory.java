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
import org.eclipse.edt.compiler.internal.core.builder.IProblemRequestor;


public interface ISDKProblemRequestorFactory {
	ISyntaxErrorRequestor getSyntaxErrorRequestor(File file);
	IProblemRequestor getProblemRequestor(File file, String partName);
	IProblemRequestor getGenericTopLevelFunctionProblemRequestor(File file, String partName,boolean containerContextDependent);
	IProblemRequestor getContainerContextTopLevelProblemRequestor(File file, String containerContextName,boolean containerContextDependent);
	
	
}
