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
package org.eclipse.edt.ide.rui.internal;

import org.eclipse.edt.compiler.internal.EGLBasePlugin;


public interface HelpContextIDs {

	public static final String PREFIX = EGLBasePlugin.getHelpIDPrefix() + "."; //$NON-NLS-1$
	
	public static final String RUI_BASE_PREFERENCE_PAGE = PREFIX + "rui0001"; //$NON-NLS-1$
	
	public static final String RUI_New_Locale_Wizard = PREFIX + "ruil0001"; //$NON-NLS-1$
}
