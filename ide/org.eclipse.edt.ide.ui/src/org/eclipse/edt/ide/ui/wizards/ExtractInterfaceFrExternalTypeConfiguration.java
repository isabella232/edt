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
package org.eclipse.edt.ide.ui.wizards;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.edt.compiler.core.ast.ExternalType;
import org.eclipse.edt.compiler.core.ast.Part;
import org.eclipse.edt.ide.core.model.IEGLFile;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IWorkbench;

public class ExtractInterfaceFrExternalTypeConfiguration extends ExtractInterfaceConfiguration {
	//this array has bound external type as well as bound record
	private ExternalType[] fBoundExternalTypes;
	private List fExtraInterfaceConfigs;
	
	public ExtractInterfaceFrExternalTypeConfiguration(){
		super();
		fExtraInterfaceConfigs = new ArrayList();
	}
	
	/**
	 * 
	 * @param ETPkgName
	 * @param ETFileName - file name w/o extension
	 * @param astParts - just the ast node, not bound 
	 */
	public void init(String ETPkgName, String ETFileName, Part[] astParts){		
		setFPackage(ETPkgName);
		String namePrefix = "I";
		//init the file name
		setFileName(namePrefix + ETFileName);		 //$NON-NLS-1$
		
		fExtraInterfaceConfigs = new ArrayList();
		int len = astParts.length;
		//make a copy of the bound parts
		fBoundExternalTypes = new ExternalType[len];
		for(int i=0; i<len; i++){
			if(astParts[i] instanceof ExternalType){
				fBoundExternalTypes[i] = (ExternalType)astParts[i];
				initExtractInterfaceConfigs(astParts[i], namePrefix);
			}
		}
	}
	
	public void init(IWorkbench workbench, IStructuredSelection selection) {
		super.init(workbench, selection);
		IEGLFile eglFile = getSelectedIEGLFile(selection);
		if(eglFile != null) {				
			//compile all parts to get all the bound parts
			List boundParts = getBoundParts(eglFile);
			for(Iterator it=boundParts.iterator(); it.hasNext();){
				Part boundPart = (Part)it.next();
				initExtractInterfaceConfigs(boundPart, "");
			}			
		}
	}

	private void initExtractInterfaceConfigs(Part boundPart, String namePrefix) {
		if(boundPart instanceof ExternalType){
			ExtractInterfaceConfiguration configElem = new ExtractInterfaceConfiguration();
			configElem.setInterfaceName(namePrefix + boundPart.getName().getCaseSensitiveIdentifier());
			configElem.initBoundPart_FunctionList(boundPart);
			
			fExtraInterfaceConfigs.add(configElem);
		}
	}
		
	public List getExtractInterfaceConfigurations(){
		return fExtraInterfaceConfigs;
	}
	
	/**
	 * should only call this after one of the init has been called
	 * @param boundParts - parts are ast bound parts
	 */
	public void setBoundParts(Part[] boundParts){
		
		int len = boundParts.length;
		
		//make a copy of the bound parts
		int boundIdx = 0;
		for(int i=0; i<len; i++){
			if(boundParts[i] instanceof ExternalType){
				fBoundExternalTypes[boundIdx] = (ExternalType)boundParts[i];
				((ExtractInterfaceConfiguration)(getExtractInterfaceConfigurations().get(boundIdx++))).setTheBoundPart(boundParts[i]);
			}
		}		
	}
	
}
