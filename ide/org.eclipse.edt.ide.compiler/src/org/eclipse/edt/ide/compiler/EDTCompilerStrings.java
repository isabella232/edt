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
package org.eclipse.edt.ide.compiler;

import org.eclipse.osgi.util.NLS;

public class EDTCompilerStrings {
	private static final String BUNDLE_NAME = "org.eclipse.edt.ide.compiler.EDTCompilerStrings"; //$NON-NLS-1$

	private EDTCompilerStrings() {
		// Do not instantiate
	}

	static {
		NLS.initializeMessages(BUNDLE_NAME, EDTCompilerStrings.class);
	}
	
	public static String javaRuntimeName;
	public static String javaRuntimeDescription;
}
