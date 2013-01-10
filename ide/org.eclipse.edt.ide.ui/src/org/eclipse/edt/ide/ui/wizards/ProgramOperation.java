/*******************************************************************************
 * Copyright © 2000, 2013 IBM Corporation and others.
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

import org.eclipse.core.runtime.jobs.ISchedulingRule;

public class ProgramOperation extends PartOperation {

	public ProgramOperation(ProgramConfiguration configuration) {
		super(configuration);
	}
	
	public ProgramOperation(ProgramConfiguration configuration, ISchedulingRule rule) {
		super(configuration, rule);
	}

	protected String getFileContents() throws PartTemplateException {		
		String templateid = "org.eclipse.edt.ide.ui.templates.basic_program"; //$NON-NLS-1$		
		ProgramConfiguration configuration = (ProgramConfiguration) super.configuration;
		String partName = configuration.getProgramName();
		
		//Determine type of program and update template Description
		if(configuration.getProgramType()==ProgramConfiguration.TEXT_UI_PROGRAM){
			templateid = "org.eclipse.edt.ide.ui.templates.text_UI_program"; //$NON-NLS-1$
		} 
		else if(configuration.getProgramType()==ProgramConfiguration.TEXT_UI_CALLED_PROGRAM){
			templateid = "org.eclipse.edt.ide.ui.templates.text_UI_called_program"; //$NON-NLS-1$
		}
		else if(configuration.getProgramType()==ProgramConfiguration.BASIC_PROGRAM){
			templateid = "org.eclipse.edt.ide.ui.templates.basic_program"; //$NON-NLS-1$
		}
		else if(configuration.getProgramType()==ProgramConfiguration.BASIC_CALLED_PROGRAM){
			templateid = "org.eclipse.edt.ide.ui.templates.basic_called_program"; //$NON-NLS-1$
		}
		else if(configuration.getProgramType()==ProgramConfiguration.VGWEBTRANS_PROGRAM){
		    templateid = "org.eclipse.edt.ide.ui.templates.VG_WebTrans_program"; //$NON-NLS-1$
		}
		else if(configuration.getProgramType()==ProgramConfiguration.ACTION_PROGRAM){
			templateid = "org.eclipse.edt.ide.ui.templates.action_program"; //$NON-NLS-1$
		}
		else if(configuration.getProgramType()==ProgramConfiguration.UI_PROGRAM){
		    templateid = "org.eclipse.edt.ide.ui.templates.ui_program"; //$NON-NLS-1$
		}
		
		return getFileContents(
			"program", //$NON-NLS-1$
			templateid,
			new String[] {
				"${programName}" //$NON-NLS-1$
			},
			new String[] {
				partName
			});
	}
}
