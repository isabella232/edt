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


public class DefaultSDKProblemRequestorFactory implements ISDKProblemRequestorFactory {

	@Override
	public IProblemRequestor getProblemRequestor(File file, String part) {
		return new SDKProblemRequestor(file, part);
	}

	@Override
	public ISyntaxErrorRequestor getSyntaxErrorRequestor(File file) {
		return new SDKSyntaxErrorRequestor(new SDKSyntaxProblemRequestor(file, "SYN"));
	}

}
