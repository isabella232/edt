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
package org.eclipse.edt.ide.rui.internal.nls;

import org.eclipse.osgi.util.NLS;



public class RUINlsStrings extends NLS {

	private static final String BUNDLE_NAME = "org.eclipse.edt.ide.rui.internal.nls.RUIResources"; //$NON-NLS-1$
	
	private RUINlsStrings() {
		// Do not instantiate
	}

	static {
		NLS.initializeMessages(BUNDLE_NAME, RUINlsStrings.class);
	}

	public static String RegnerateAllFindingProjects;
	public static String html_gen_error_1;
	
	public static String locale_English;
	public static String locale_Arabic;
	public static String locale_Arabic_Egypt;
	public static String locale_Arabic_Saudi_Arabia;
	public static String locale_Brazilian;
	public static String locale_Chinese_Simplified;
	public static String locale_Chinese_Taiwan;
	public static String locale_Chinese_Hong_Kong;
	public static String locale_Czech;
	public static String locale_French;
	public static String locale_German;
	public static String locale_Hungarian;
	public static String locale_Italian;
	public static String locale_Japanese;
	public static String locale_Korean;
	public static String locale_Polish;
	public static String locale_Russian;
	public static String locale_Spanish;
	
	public static String RUIDeployPreferencePage_0;
	public static String RUIDeployPreferencePage_1;
	public static String RUIDeployPreferencePage_2;
	public static String RUIDeployPreferencePage_4;
	public static String RUIDeployPreferencePage_5;
	public static String RUIDeployPreferencePage_6;
	public static String RUIDeployPreferencePage_Locales_that_the_handler_will_suppo_;
	public static String RUIDeployPreferencePage_edit_button;
	
	public static String NewLocaleDialog_Please_enter_a_unique_locale_cod_;
	public static String NewLocaleDialog_The_description_is_not_uniqu_;
	public static String NewLocaleDialog_Please_enter_a_unique_locale_descri_;
	public static String NewLocaleDialog_Create_a_new_local_;
	public static String NewLocaleDialog_locale_combo_already_exists;
	
	public static String EditLocaleDialog_edit_locale_;
	
	public static String ImportWidgetJob_Name;
	public static String ImportWidgetJob_Error;
	
	public static String Genopt_title;
	public static String Genopt_Description;
	public static String genopt_change;
	public static String genopt_continue;
	
	public static String ContextKeyNotFound;
	
	public static String RUIPreferencePage_1;
	public static String RUIPreferencePage_2;
	public static String Continue_to_prompt;

	public static String NoPreviewContent_Title;
	public static String NoPreviewContent_Msg;
	public static String NoBrowserDialog_Title;
	public static String NoBrowserDialog_Msg;
	public static String NoDefaultBrowserDialog_Msg;
	public static String NoWebContent_Title;
}

