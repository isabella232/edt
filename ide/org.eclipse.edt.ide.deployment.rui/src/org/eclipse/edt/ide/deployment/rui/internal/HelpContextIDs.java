/*******************************************************************************
 * Copyright © 2008, 2013 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.edt.ide.deployment.rui.internal;

import org.eclipse.edt.compiler.internal.EGLBasePlugin;


public interface HelpContextIDs {
	
	public static final String PREFIX = EGLBasePlugin.getHelpIDPrefix() + "."; //$NON-NLS-1$
	
	public static final String RUI_Deply_Wizard_Page_One = PREFIX + "ruid0001"; //$NON-NLS-1$
	public static final String RUI_Deply_Wizard_Page_Two = PREFIX + "ruid0002"; //$NON-NLS-1$
	public static final String RUI_Deply_Wizard_Page_Three = PREFIX + "ruid0003"; //$NON-NLS-1$
	public static final String RUI_Deply_Wizard_Basic_Solution = PREFIX + "ruid0004"; //$NON-NLS-1$
	
	// DD editor and its associated wizards
	public static final String RUI_DD_EDITOR_MAIN_PAGE = PREFIX + "ruidd0001"; //$NON-NLS-1$
	public static final String RUI_DD_EDITOR_OVERVIEW_PAGE = PREFIX + "ruidd0002"; //$NON-NLS-1$
	public static final String RUI_DD_ADD_HANDLERS_WIZARD = PREFIX + "ruidd0003"; //$NON-NLS-1$
	
	// Deployment preference page
	public static final String RUI_DEPLOYMENT_PREFERENCE_PAGE = PREFIX + "ruidp0001"; //$NON-NLS-1$
	
}
