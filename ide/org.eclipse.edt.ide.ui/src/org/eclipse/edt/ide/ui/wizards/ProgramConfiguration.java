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
package org.eclipse.edt.ide.ui.wizards;

import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IWorkbench;

public class ProgramConfiguration extends EGLPartConfiguration {
	
	/** Program Types */
	public final static int BASIC_CALLED_PROGRAM = 0;
	public final static int BASIC_PROGRAM = 1;
	public final static int TEXT_UI_CALLED_PROGRAM = 2;
	public final static int TEXT_UI_PROGRAM = 3;
	public final static int ACTION_PROGRAM = 4; 
	public final static int VGWEBTRANS_PROGRAM = 5;	
	public final static int UI_PROGRAM = 6;
	
	/** The name of the program */
	private String programName;
	
	/** The type of program */
	private int programType;
	
	public void init(IWorkbench workbench, IStructuredSelection selection) {
		super.init(workbench, selection);

		setDefaultAttributes();
	}

	/**
	 * @return
	 */
	public int getProgramType() {
		return programType;
	}

	/**
	 * @param i
	 */
	public void setProgramType(int i) {
		programType = i;
	}

	/**
	 * @return
	 */
	public String getProgramName() {
		return programName;
	}

	/**
	 * @param string
	 */
	public void setProgramName(String string) {
		programName = string;
	}
	
	private void setDefaultAttributes() {
		programType = 1;
		programName = ""; //$NON-NLS-1$
	}

}
