/*******************************************************************************
 * Copyright © 2011 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.edt.ide.rui.internal.testserver;

import org.eclipse.osgi.util.NLS;

public class TestServerMessages extends NLS {
	
	private static final String BUNDLE_NAME = "org.eclipse.edt.ide.rui.internal.testserver.TestServerMessages"; //$NON-NLS-1$
	
	private TestServerMessages() {
		// Do not instantiate
	}
	
	static {
		NLS.initializeMessages(BUNDLE_NAME, TestServerMessages.class);
	}
	
	public static String TestServerProcessName;
	public static String ServiceMappingAdditionsInvalidTokens;
	public static String ServiceMappingRemovalsInvalidTokens;
	public static String CouldNotGetPluginPath;
	public static String ConfigServletBadStatus;
	
	public static String HotCodeReplaceFailureContinueButton;
	public static String HotCodeReplaceFailureTerminateButton;
	public static String HotCodeReplaceFailureRememberDecision;
	public static String HotCodeReplaceFailureChangeLater;
	public static String HotCodeReplaceFailureObsoleteMethodsTitle;
	public static String HotCodeReplaceFailureObsoleteMethodsMsg;
	public static String HotCodeReplaceFailureHCRFailedTitle;
	public static String HotCodeReplaceFailureHCRFailedMsg;
	public static String HotCodeReplaceFailureHCRUnsupportedMsg;
	public static String HotCodeReplaceFailureTerminateMsg;
	public static String HotCodeReplaceTerminateFailedTitle;
	public static String HotCodeReplaceTerminateFailedMsg;
	
	public static String PreferenceHCRGroupLabel;
	public static String PreferenceHCRFailedLabel;
	public static String PreferenceHCRUnsupportedLabel;
	public static String PreferenceObsoleteMethodsLabel;
	public static String PreferencePromptLabel;
	public static String PreferenceDoNothingLabel;
	public static String PreferenceTerminateLabel;
}
