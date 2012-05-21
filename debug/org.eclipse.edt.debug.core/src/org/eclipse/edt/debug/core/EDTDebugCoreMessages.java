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
package org.eclipse.edt.debug.core;

import org.eclipse.osgi.util.NLS;

public class EDTDebugCoreMessages extends NLS
{
	private static final String BUNDLE_NAME = "org.eclipse.edt.debug.core.EDTDebugCoreMessages"; //$NON-NLS-1$
	
	static
	{
		NLS.initializeMessages( BUNDLE_NAME, EDTDebugCoreMessages.class );
	}
	
	public static String TransformerUnknownTag;
	public static String TransformerUnsupportedEncoding;
	public static String ErrorRetrievingValue;
	public static String WatchExprsUnsupported;
	public static String TypeFilterExtensionRequiredAttributeMissing;
	public static String TypeFilterProviderMissingCategory;
	public static String NoDescription;
	public static String TypeFilterClasspathEntryNotSupported;
	public static String StackFrameLabelBasic;
	public static String StackFrameLineUnknown;
}
