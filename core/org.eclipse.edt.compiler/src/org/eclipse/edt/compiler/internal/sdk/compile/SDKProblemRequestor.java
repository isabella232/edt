/*******************************************************************************
 * Copyright © 2011, 2013 IBM Corporation and others.
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

public class SDKProblemRequestor extends SDKSyntaxProblemRequestor {

	String partName;
	File file;
	public SDKProblemRequestor(File file, String partName) {
		super(file, "VAL");//$NON-NLS-1$
		this.file = file;
		this.partName = partName;
	}
}
