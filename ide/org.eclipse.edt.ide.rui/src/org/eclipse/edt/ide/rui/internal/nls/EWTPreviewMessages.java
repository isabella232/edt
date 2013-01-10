/*******************************************************************************
 * Copyright © 2012, 2013 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.edt.ide.rui.internal.nls;

import org.eclipse.osgi.util.NLS;


public class EWTPreviewMessages extends NLS {

	private static final String BUNDLE_NAME = "org.eclipse.edt.ide.rui.internal.nls.EWTPreviewMessages"; //$NON-NLS-1$
		
	private EWTPreviewMessages() {
		// Do not instantiate
	}

	static {
		NLS.initializeMessages(BUNDLE_NAME, EWTPreviewMessages.class);
	}

	public static String GENFAILEDPAGE_TITLE; 
	public static String GENFAILEDPAGE_HEADERMSG;
	public static String COMPILEFAILEDPAGE_HEADERMSG;
}
