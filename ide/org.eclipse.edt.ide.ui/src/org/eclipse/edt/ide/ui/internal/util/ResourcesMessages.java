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
package org.eclipse.edt.ide.ui.internal.util;

import org.eclipse.osgi.util.NLS;

public final class ResourcesMessages extends NLS {

	private static final String BUNDLE_NAME= "org.eclipse.edt.ide.ui.internal.util.ResourcesMessages";//$NON-NLS-1$

	private ResourcesMessages() {
		// Do not instantiate
	}

	public static String Resources_outOfSyncResources;
	public static String Resources_outOfSync;
	public static String Resources_modifiedResources;
	public static String Resources_fileModified;

	static {
		NLS.initializeMessages(BUNDLE_NAME, ResourcesMessages.class);
	}

	public static String JavaModelUtil_applyedit_operation;
}
