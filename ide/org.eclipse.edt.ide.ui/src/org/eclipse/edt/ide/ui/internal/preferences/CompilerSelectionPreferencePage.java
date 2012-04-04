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

import org.eclipse.edt.ide.ui.preferences.CompilerPropertyAndPreferencePage;
import org.eclipse.edt.ide.ui.preferences.ICompilerPreferencePage;



public class CompilerSelectionPreferencePage extends CompilerPropertyAndPreferencePage 
										implements ICompilerPreferencePage {
	
	public static final String COMPILER_SELECTION_ID = "org.eclipse.edt.ide.ui.compiler.selection";
	
	/**
	 * Returns the id of the compiler that this preference page is for.
	 */
	public String getPreferencePageCompilerId() {
		return COMPILER_SELECTION_ID;
	}
	
}
