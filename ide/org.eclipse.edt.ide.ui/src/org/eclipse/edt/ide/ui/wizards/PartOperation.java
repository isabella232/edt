/*******************************************************************************
 * Copyright © 2008, 2012 IBM Corporation and others.
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
import org.eclipse.edt.ide.ui.internal.templates.TemplateEngine;

public abstract class PartOperation extends EGLFileOperation {

	protected String templateName;

	protected EGLPartConfiguration configuration;
	
	public PartOperation(EGLPartConfiguration configuration) {
		super(configuration);
		this.configuration = configuration;
	}

	public PartOperation(EGLPartConfiguration configuration, ISchedulingRule rule) {
		super(configuration, rule);
		this.configuration = configuration;
	}
	
	protected String getFileContents(String templateName, String templateId, String[] templateVariablesToReplace, String[] valuesToReplaceWith) throws PartTemplateException {
		String outputString = ""; //$NON-NLS-1$
		
		//Determine the template to use
		if(configuration.getChosenTemplateSelection()==EGLPartConfiguration.USE_CUSTOM){
			outputString = TemplateEngine.getCustomizedTemplateString(templateName, templateId);	
		}
		else if(configuration.getChosenTemplateSelection()==EGLPartConfiguration.USE_DEFAULT){
			outputString = TemplateEngine.getDefaultTemplateString(templateName, templateId);
		}
		//Check for error condition (no template found / template disabled)
		if(outputString.compareTo("")!=0){ //$NON-NLS-1$

			String firstHalfOutputString;
			String secondHalfOutputString;
			
			for(int i = 0; i < templateVariablesToReplace.length; i++) {
				outputString = replaceTemplateWithString(outputString, templateVariablesToReplace[i], valuesToReplaceWith[i]);
				if(outputString == null) {
					throw new PartTemplateException(templateName, templateId, EGLFileConfiguration.TEMPLATE_CORRUPTED);
				}
			}
			
			//Remove remaining template tags
			int tagStart;
			int tagEnd;
		
			while(outputString.indexOf("${")!=-1){ //$NON-NLS-1$
				tagStart = outputString.indexOf("${"); //$NON-NLS-1$
				tagEnd = outputString.indexOf("}", tagStart); //$NON-NLS-1$
		
				firstHalfOutputString = outputString.substring(0, tagStart);
				secondHalfOutputString = outputString.substring(tagStart + 2, outputString.length());
		
				outputString = firstHalfOutputString + secondHalfOutputString;
		
				firstHalfOutputString = outputString.substring(0, tagEnd -2);
				secondHalfOutputString = outputString.substring(tagEnd - 1, outputString.length());
				
				//Check for (and remove) cursor variable
				if(firstHalfOutputString.endsWith("cursor")) //$NON-NLS-1$
					firstHalfOutputString = firstHalfOutputString.substring(0, firstHalfOutputString.length()-"cursor".length()); //$NON-NLS-1$
				
				outputString = firstHalfOutputString + secondHalfOutputString;
			}
		
			return outputString;
		}
		else{
			throw new PartTemplateException(templateName, templateId, EGLFileConfiguration.TEMPLATE_NOT_FOUND);
		}
	}
	
	protected String replaceTemplateWithString(String templateString, String templateVariableName, String newVariableValue) throws PartTemplateException {
		int variableBegin = templateString.indexOf(templateVariableName);
		
		if(variableBegin == -1){
			return null;
		}			
		
		String firstHalfOutputString = templateString.substring(0, variableBegin);
		String secondHalfOutputString = templateString.substring(variableBegin + templateVariableName.length(), templateString.length());
	
		return firstHalfOutputString + newVariableValue + secondHalfOutputString;
	}
}
