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

import org.eclipse.core.runtime.Path;
import org.eclipse.edt.ide.ui.internal.templates.TemplateEngine;

public class RUIHandlerOperation extends PartOperation {

	public RUIHandlerOperation(RUIHandlerConfiguration configuration) {
		super(configuration);
	}

	protected String getFileContents() throws PartTemplateException {	
		String outputString = "";
		templateName = "handler";
		String templateid = "org.eclipse.edt.ide.ui.templates.rui_handler"; //$NON-NLS-1$		
		RUIHandlerConfiguration configuration = (RUIHandlerConfiguration) super.configuration;
		
		String partName = configuration.gethandlerName();
		String title = configuration.getHandlerTitle();
		String relativeCSSFile = new Path("css/" + configuration.getProjectName() + ".css").toString();
		
		//Determine the template to use
		if(configuration.getChosenTemplateSelection()==EGLPartConfiguration.USE_CUSTOM){
			outputString = TemplateEngine.getCustomizedTemplateString(templateName, templateid);	
		}
		else if(configuration.getChosenTemplateSelection()==EGLPartConfiguration.USE_DEFAULT){
			outputString = TemplateEngine.getDefaultTemplateString(templateName, templateid);
		}
		
		//Check for error condition (no template found / template disabled)
		if(outputString.compareTo("")!=0) { //$NON-NLS-1$
			
			String firstHalfOutputString;
			String secondHalfOutputString;
			String[] templateVariablesToReplace = new String[] {
					"${handlerName}", //$NON-NLS-1$
					"${cssFile}", //$NON-NLS-1$
					"${handlerTitle}" //$NON-NLS-1$
				};
			String[] valuesToReplaceWith = new String[] {
					partName,
					relativeCSSFile,
					title
				};			
			String oldString;
			for(int i = 0; i < templateVariablesToReplace.length; i++) {
				oldString = outputString;
				outputString = replaceTemplateWithString(outputString, templateVariablesToReplace[i], valuesToReplaceWith[i]);
				// The first two variables are required.  The third, handlerTitle,
				// was added in v8.0.1 and won't be on older templates. 
				if(outputString == null) {
					if(i==2) {
						outputString = oldString;
					} else {
						throw new PartTemplateException(templateName, templateid, EGLFileConfiguration.TEMPLATE_CORRUPTED);
					}
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
			throw new PartTemplateException(templateName, templateid, EGLFileConfiguration.TEMPLATE_NOT_FOUND);
		}
	}
}
