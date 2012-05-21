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
package org.eclipse.edt.debug.internal.ui.java;

import org.eclipse.osgi.util.NLS;

public class EGLJavaMessages extends NLS
{
	private static final String BUNDLE_NAME = "org.eclipse.edt.debug.internal.ui.java.EGLJavaMessages"; //$NON-NLS-1$
	
	static
	{
		// load message values from bundle file
		NLS.initializeMessages( BUNDLE_NAME, EGLJavaMessages.class );
	}
	
	public static String LineBreakpointUnkown;
	public static String LineBreakpointLabel;
	public static String ThreadLabelSuspendedAtBreakpoint;
	public static String PreferencePageMessage;
	public static String JavaPreferencePageLink;
	public static String JavaFilterPreferencePageLink;
	public static String FilterStepIntoLabel;
	public static String FilterStepReturnLabel;
	public static String TypeFiltersGroupLabel;
	public static String TypeFiltersDescription;
	public static String TypeFiltersEnableButtonLabel;
	public static String TypeFiltersCategoryColumn;
	public static String TypeFiltersBehaviorColumn;
	public static String TypeFilterDescriptionLabel;
}
