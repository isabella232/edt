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
package org.eclipse.edt.ide.testserver;

import org.eclipse.osgi.util.NLS;

/**
 * Translated messages.
 */
public class TestServerMessages extends NLS {
	
	private static final String BUNDLE_NAME = "org.eclipse.edt.ide.testserver.TestServerMessages"; //$NON-NLS-1$
	
	private TestServerMessages() {
		// Do not instantiate
	}
	
	static {
		NLS.initializeMessages(BUNDLE_NAME, TestServerMessages.class);
	}
	
	public static String TestServerProcessName;
	public static String CouldNotGetPluginPath;
	public static String ProjectMissingJavaNature;
	public static String InvalidClasspathEntry;
	public static String PingFailed;
	public static String DefaultServletBadStatus;
	
	public static String ErrorDialogContinueButton;
	public static String ErrorDialogTerminateButton;
	public static String ErrorDialogTerminatePluralButton;
	public static String ErrorDialogRememberDecision;
	public static String ErrorDialogChangeLater;
	public static String ErrorDialogTerminateMsg;
	public static String ErrorDialogTerminatePluralMsg;
	public static String TerminateFailedTitle;
	public static String TerminateFailedMsg;
	public static String ObsoleteMethodsTitle;
	public static String ObsoleteMethodsMsg;
	public static String HCRFailedTitle;
	public static String HCRFailedMsg;
	public static String HCRUnsupportedMsg;
	public static String ClasspathChangedTitle;
	public static String ClasspathChangedMsg;
	
	public static String PreferenceEnableDebugLabel;
	public static String PreferenceCPGroupLabel;
	public static String PreferenceCPChangedLabel;
	public static String PreferenceHCRGroupLabel;
	public static String PreferenceHCRFailedLabel;
	public static String PreferenceHCRUnsupportedLabel;
	public static String PreferenceObsoleteMethodsLabel;
	public static String PreferencePromptLabel;
	public static String PreferenceDoNothingLabel;
	public static String PreferenceTerminateLabel;
}
