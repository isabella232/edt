/*******************************************************************************
 * Copyright © 2000, 2010 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.edt.compiler.internal;

import org.eclipse.jface.preference.IPreferenceStore;


/**
 * @author winghong
 */
public class EGLAliasJsfNamesSetting {

	public static final int READ_FROM_PREFERENCE = 0;
	public static final int NO_ALIAS = 1;
	public static final int ALIAS = 2;
	
	private static int setting = 0;
	
	public static boolean getAliasJsfNamesFromPrefs() {
		IPreferenceStore preferences = EGLBasePlugin.getPlugin().getPreferenceStore();
		return preferences.getBoolean(EGLBasePlugin.ALIAS_JSF_HANDLER_NAMES);
	}
	
	public static boolean isAliasJsfNames() {
		if(setting == READ_FROM_PREFERENCE) {
			
			if(EGLBasePlugin.getPlugin() == null || EGLBasePlugin.getPlugin().getPreferenceStore() == null) {
				return true;	// Defensive programming -- preference store is null when this method is called from outside Eclipse
			}
			return getAliasJsfNamesFromPrefs();
		}
		else if(setting == NO_ALIAS) {
			return false;
		}
		else if(setting == ALIAS) {
			return true;
		}
		else {
			throw new IllegalStateException();
		}
	}
	
	public static void setAliasJsfNames(int setting) {
		if(setting > 2) {
			throw new IllegalArgumentException();
		}
		
		EGLAliasJsfNamesSetting.setting = setting;
	}
	
	public static int getJsfNamesSetting() {
		return EGLAliasJsfNamesSetting.setting;
	}
	
}
