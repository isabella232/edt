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
package org.eclipse.edt.ide.ui.internal.project.wizard.pages;

import java.util.List;

import org.eclipse.edt.ide.ui.preferences.CompilerPropertyAndPreferencePage;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

public class InnerCompilerGeneratorPage extends
		CompilerPropertyAndPreferencePage {
	
	/**
	 * Returns the id of the compiler that this preference page is for.
	 */
	public String getPreferencePageCompilerId() {
		return "org.eclipse.edt.ide.compiler.edtCompiler";
	}
	
	protected boolean isValidWorkspaceExtensions() {
		return true;
	}
	public Control createContents(Composite parent) {
		return super.createContents(parent);
	}
	
	public List<String> getSelectedGenerators() {
		return super.getSelectedGenerators();
	}
}
