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
package org.eclipse.edt.compiler.internal;

import org.eclipse.jface.preference.IPreferenceStore;

public class EGLIncompleteBuildPathSetting {
	
	public static final int _INCOMPLETE_BUILD_PATH_ERROR = 0;
	public static final int _INCOMPLETE_BUILD_PATH_WARNING = 1;

	public static int getIncompleteBuildPathSetting() {
		if (EGLBasePlugin.getPlugin() == null || EGLBasePlugin.getPlugin().getPreferenceStore() == null) {
			return _INCOMPLETE_BUILD_PATH_ERROR;	// Defensive programming -- preference store is null when this method is called from outside Eclipse
		}
		IPreferenceStore preferences = EGLBasePlugin.getPlugin().getPreferenceStore();
		int incompleteBuildPath = preferences.getInt(EGLBasePlugin.INCOMPLETE_BUILD_PATH);
		return incompleteBuildPath;
	}
	
}
