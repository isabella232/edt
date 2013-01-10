/*******************************************************************************
 * Copyright © 2011, 2013 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.edt.ide.ui.internal.project.wizards;

import java.util.List;

import org.eclipse.edt.compiler.IGenerator;
import org.eclipse.edt.ide.core.EDTCoreIDEPlugin;
import org.eclipse.edt.ide.ui.internal.project.wizard.pages.BasicProjectGeneratorSelectionPage;
import org.eclipse.edt.ide.ui.internal.wizards.NewWizardMessages;
import org.eclipse.edt.ide.ui.project.templates.ProjectTemplateWizard;


public class BasicProjectTemplateWizard extends ProjectTemplateWizard {
	private BasicProjectGeneratorSelectionPage generatorPage;
	
	public void addPages() {
		this.generatorPage = new BasicProjectGeneratorSelectionPage(NewWizardMessages.GeneratorSelectionPage, this);
		addPage(generatorPage);
		super.addPages();
	}
	
	public boolean performFinish() {
		if(generatorPage != null){
			String[] genIds = getGeneratorIds();
			if(genIds != null)
				((NewEGLProjectWizard) getParentWizard()).getModel().setSelectedGenerators(genIds);
		}else{
			((NewEGLProjectWizard) getParentWizard()).getModel().setEmptyCompilerProperty();
		}
		return true;
	}
	
	public boolean proecssGenerationDirectorySetting() {
		if(generatorPage != null){
			return generatorPage.performOK();
		}
		return true;
	}

	private String[] getGeneratorIds() {
		String[] genIds = null;
		if(generatorPage != null){
			List<String> selectedGenerators = generatorPage.getSelectedGenerators();
			genIds = new String[selectedGenerators.size()];
			int index = 0;
			StringBuilder buffer = new StringBuilder(100);
			for( String genName : selectedGenerators ) {
				genIds[index] = convertGeneratorNameToId( genName );				
				if (index != 0) {
					buffer.append(',');
				}
				buffer.append( genIds[index] );
				index++;
			}
		}		
		return genIds;
	}
	
	private String convertGeneratorNameToId( String name ) {
		if( name != null && name.length() > 0 ) {
			IGenerator[] generators = EDTCoreIDEPlugin.getPlugin().getGenerators();
			for( final IGenerator currentGenerator : generators ) {
				if( currentGenerator.getName().equalsIgnoreCase( name ) )  {
					return currentGenerator.getId();
				}
			}
		}
		return "";
	}
}
