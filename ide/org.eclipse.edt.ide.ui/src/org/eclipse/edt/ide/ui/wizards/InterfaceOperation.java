/*******************************************************************************
 * Copyright © 2005, 2012 IBM Corporation and others.
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

import java.util.Iterator;
import java.util.List;

import org.eclipse.core.runtime.jobs.ISchedulingRule;
import org.eclipse.edt.compiler.core.IEGLConstants;
import org.eclipse.edt.ide.core.model.IPart;
import org.eclipse.edt.ide.ui.internal.templates.TemplateEngine;

public class InterfaceOperation extends EGLFileOperation {
	private InterfaceConfiguration configuration;
	private List interfaces;

	public InterfaceOperation(InterfaceConfiguration configuration, List superInterfaces) {
		super(configuration);
		this.configuration = configuration;
		interfaces = superInterfaces;
	}
	
	public InterfaceOperation(InterfaceConfiguration configuration, List superInterfaces, ISchedulingRule rule) {
		super(configuration, rule);
		this.configuration = configuration;
		interfaces = superInterfaces;
	}
	
	protected String getFileContents() throws PartTemplateException {        
		String interfaceOutputString = ""; //$NON-NLS-1$
		StringBuffer importString = new StringBuffer();
		
		String templateName = "interface"; //$NON-NLS-1$
		String templateid = "org.eclipse.edt.ide.ui.templates.interface"; //$NON-NLS-1$
		
		String templateVariableInterfaceName = "${interfaceName}"; //$NON-NLS-1$
		String templateVariableSuperInterfaceName = "${superInterfaces}"; //$NON-NLS-1$
		
		if(configuration.getInterfaceType()==InterfaceConfiguration.BASIC_INTERFACE){
			templateid = "org.eclipse.edt.ide.ui.templates.interface"; //$NON-NLS-1$
		}		
		
		//Determine the template to use
		if(configuration.getChosenTemplateSelection()==EGLPartConfiguration.USE_CUSTOM) {
		    interfaceOutputString = TemplateEngine.getCustomizedTemplateString(templateName, templateid);	
		} else if(configuration.getChosenTemplateSelection()==EGLPartConfiguration.USE_DEFAULT){
		    interfaceOutputString = TemplateEngine.getDefaultTemplateString(templateName, templateid);
		}
		
		//Check for error condition (no template found / template disabled)
		if(interfaceOutputString.compareTo("")!=0) { //$NON-NLS-1$
			
			//Find and replace variables
			int variableBegin;
		
			String firstHalfOutputString;
			String secondHalfOutputString;
		
			String newVariableValue = configuration.getInterfaceName();
		
			variableBegin = interfaceOutputString.indexOf(templateVariableInterfaceName);
			
			if(variableBegin == -1){
				throw new PartTemplateException(templateName, templateid, EGLFileConfiguration.TEMPLATE_CORRUPTED);
			}
			
			firstHalfOutputString = interfaceOutputString.substring(0, variableBegin);
			secondHalfOutputString = interfaceOutputString.substring(variableBegin + templateVariableInterfaceName.length(), interfaceOutputString.length());
		
			interfaceOutputString = firstHalfOutputString + newVariableValue + secondHalfOutputString;
		
			//=============if there is super interfaces and it's java objec type interface
			if(configuration.getInterfaceType()==InterfaceConfiguration.JAVAOBJ_INTERFACE) {
				String myInterfacePkgName = configuration.getFPackage();			
				if(interfaces != null && interfaces.size()>0) {
				    StringBuffer superInterfacesVarValue = new StringBuffer();
				    Iterator it = interfaces.iterator();
				    while(it.hasNext()) {
					    String interfaceFQName = (String)it.next();
					    Object interfaceobj = configuration.getInterface(interfaceFQName);
					    if(interfaceobj != null && interfaceobj instanceof IPart) {
					        IPart interfacepart = (IPart)interfaceobj;
					        
					        //------ import section if differs from the current package
					        if(!myInterfacePkgName.equalsIgnoreCase(interfacepart.getPackageFragment().getElementName())) {
					            importString.append(IEGLConstants.KEYWORD_IMPORT);
					            importString.append(" "); //$NON-NLS-1$
					            importString.append(interfaceFQName);
					            importString.append(";"); //$NON-NLS-1$
					            importString.append(newLine);
					        }
					        
					        //------ implements section
					        superInterfacesVarValue.append(interfacepart.getElementName());
					        if(it.hasNext())
					            superInterfacesVarValue.append(", "); //$NON-NLS-1$
					        else
					            superInterfacesVarValue.append(" ");				         //$NON-NLS-1$
					    }
				    }
				    
				    //replace the super interface variable with its value
				    int varBegin = interfaceOutputString.indexOf(templateVariableSuperInterfaceName);
				    if(varBegin == -1)
				        throw new PartTemplateException(templateName, templateid, EGLFileConfiguration.TEMPLATE_CORRUPTED);
				    
				    String leftHalf = interfaceOutputString.substring(0, varBegin);
				    String rightHalf = interfaceOutputString.substring(varBegin + templateVariableSuperInterfaceName.length(), interfaceOutputString.length());
				
				    interfaceOutputString = importString.toString() + leftHalf + superInterfacesVarValue.toString() + rightHalf;
				}
			}
			
			interfaceOutputString = RemoveRemainingTemplateTags(interfaceOutputString, firstHalfOutputString, secondHalfOutputString);
			return interfaceOutputString;
		} else {
			throw new PartTemplateException(templateName, templateid, EGLFileConfiguration.TEMPLATE_NOT_FOUND);
		}
	}
}
