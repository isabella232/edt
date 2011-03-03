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
package org.eclipse.edt.compiler.internal;

import org.eclipse.jface.preference.IPreferenceStore;


/**
 * @author winghong
 */
public class EGLVAGCompatibilitySetting {

	public static final int READ_FROM_PREFERENCE = 0;
	public static final int NO_VAG_COMPATIBILITY = 1;
	public static final int VAG_COMPATIBILITY = 2;
	
	private static int setting = 0;
	
	public static boolean isVAGCompatibility() {
		if(setting == READ_FROM_PREFERENCE) {
			
			if(EGLBasePlugin.getPlugin() == null || EGLBasePlugin.getPlugin().getPreferenceStore() == null) {
				return false;	// Defensive programming -- preference store is null when this method is called from outside Eclipse
			}

			IPreferenceStore preferences = EGLBasePlugin.getPlugin().getPreferenceStore();

			String vagCompatibility = preferences.getString(EGLBasePlugin.VAGCOMPATIBILITY_OPTION);
			if(vagCompatibility.equalsIgnoreCase("TRUE")) { //$NON-NLS-1$
				return true;
			}
			else {
				return false;
			}
		}
		else if(setting == NO_VAG_COMPATIBILITY) {
			return false;
		}
		else if(setting == VAG_COMPATIBILITY) {
			return true;
		}
		else {
			throw new IllegalStateException();
		}
	}
	
	public static void setVAGCompatibility(int setting) {
		if(setting > 2) {
			throw new IllegalArgumentException();
		}
		
		EGLVAGCompatibilitySetting.setting = setting;
	}
	
	public static int getVAGCompatibilitySetting() {
		return EGLVAGCompatibilitySetting.setting;
	}
	
}
