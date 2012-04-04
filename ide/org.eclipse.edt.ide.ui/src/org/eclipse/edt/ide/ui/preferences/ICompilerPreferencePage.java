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
package org.eclipse.edt.ide.ui.preferences;

/**
 * Contributors to the <code>org.eclipse.ui.preferencePages</code> extension
 * point and <code>"org.eclipse.edt.ide.ui.compilerPreferences"</code> category
 * must specify an implementation of this interface.
 * 
 * Clients may implement this interface.
 * 
 */

public interface ICompilerPreferencePage {
	/**
	 * Returns the id of the compiler that this preference page is for.
	 */
	public String getPreferencePageCompilerId();
}	

