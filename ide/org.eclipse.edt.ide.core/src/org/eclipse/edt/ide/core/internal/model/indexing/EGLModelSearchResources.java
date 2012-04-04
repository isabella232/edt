/*******************************************************************************
 * Copyright © 2000, 2012 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.edt.ide.core.internal.model.indexing;

import org.eclipse.osgi.util.NLS;
;

public class EGLModelSearchResources extends NLS {
	
	private static final String BUNDLE_NAME = "org.eclipse.edt.ide.core.internal.model.indexing.EGLModelSearchResources"; //$NON-NLS-1$

	private EGLModelSearchResources() {
		// Do not instantiate
	}

	static {
		NLS.initializeMessages(BUNDLE_NAME, EGLModelSearchResources.class);
	}
	
	public static String EngineSearching;
	public static String ProcessName;
	public static String ExceptionWrongFormat;
	public static String ManagerFilesToIndex;

}

