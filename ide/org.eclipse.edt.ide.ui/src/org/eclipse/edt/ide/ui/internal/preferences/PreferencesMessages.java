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
package org.eclipse.edt.ide.ui.internal.preferences;

import org.eclipse.osgi.util.NLS;

public final class PreferencesMessages extends NLS {

	private static final String BUNDLE_NAME= "org.eclipse.edt.ide.ui.internal.preferences.PreferencesMessages";//$NON-NLS-1$

	private PreferencesMessages() {
		// Do not instantiate
	}

	public static String CodeAssistAdvancedConfigurationBlock_page_description;
	public static String CodeAssistAdvancedConfigurationBlock_no_shortcut;
	public static String CodeAssistAdvancedConfigurationBlock_default_table_description;
	public static String CodeAssistAdvancedConfigurationBlock_default_table_category_column_title;
	public static String CodeAssistAdvancedConfigurationBlock_default_table_keybinding_column_title;
	public static String CodeAssistAdvancedConfigurationBlock_key_binding_hint;
	public static String CodeAssistAdvancedConfigurationBlock_separate_table_description;
	public static String CodeAssistAdvancedConfigurationBlock_separate_table_category_column_title;
	public static String CodeAssistAdvancedConfigurationBlock_Up;
	public static String CodeAssistAdvancedConfigurationBlock_Down;
	public static String CodeAssistAdvancedConfigurationBlock_parameterNameFromAttachedJavadoc_timeout;
	public static String EGLEditorPreferencePage_autoActivationDelay;
	public static String SpellingPreferencePage_empty_threshold;
	public static String SpellingPreferencePage_invalid_threshold;
	
	static {
		NLS.initializeMessages(BUNDLE_NAME, PreferencesMessages.class);
	}

}
