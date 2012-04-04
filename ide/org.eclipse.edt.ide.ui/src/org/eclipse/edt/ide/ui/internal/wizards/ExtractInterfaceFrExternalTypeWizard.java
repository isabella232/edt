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
package org.eclipse.edt.ide.ui.internal.wizards;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.edt.ide.ui.internal.util.ExternalType2RuiInterfaceConverter;
import org.eclipse.edt.ide.ui.internal.EGLLogger;
import org.eclipse.edt.ide.ui.wizards.EGLPackageConfiguration;
import org.eclipse.edt.ide.ui.wizards.ExtractInterfaceFrExternalTypeConfiguration;
import org.eclipse.edt.ide.ui.wizards.ExtractInterfaceFrExternalTypeOperation;
import org.eclipse.edt.ide.ui.wizards.WSDL2EGLAdditionalFileOperation;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.operation.IRunnableContext;

public class ExtractInterfaceFrExternalTypeWizard extends ExtractInterfaceWizard {
	public EGLPackageConfiguration getConfiguration() {
		if (configuration == null)
			configuration = new ExtractInterfaceFrExternalTypeConfiguration();
		return configuration;
	}	
	
	private ExtractInterfaceFrExternalTypeConfiguration getExtractInterfaceFrExternalTypeConfiguration(){
		return (ExtractInterfaceFrExternalTypeConfiguration)getConfiguration();
	}

	public void addPages() {
		addPage(new ExtractInterfaceFrExternalTypeWizardPage(ExtractInterfaceFrExternalTypeWizardPage.WIZPAGENAME_ExtractInterfaceFrExternalTypeWizardPage));
	}
	
	protected boolean runExtractInterfaceOp() {		
		IFile interfacefile = null;		
		try {
			interfacefile = executeExtractInterfaceOp(getContainer(), getExtractInterfaceFrExternalTypeConfiguration());
			
		} catch (InterruptedException e) {
			e.printStackTrace();
			EGLLogger.log(this, e);
			return false;
		}catch (InvocationTargetException e) {
			if(e.getTargetException() instanceof CoreException) {
				ErrorDialog.openError(
					getContainer().getShell(),
					null,
					null,
					((CoreException) e.getTargetException()).getStatus());
			}
			else {
				EGLLogger.log(this, e);
				e.printStackTrace();
			}
			return false;
		}
		
		//open the service/interface file
		openResource(interfacefile);

		return true;
	}

	/**
	 * 
	 * @param runnable
	 * @param config
	 * @return  - the extracted interface IFile
	 * @throws InvocationTargetException
	 * @throws InterruptedException
	 */
	public static IFile executeExtractInterfaceOp(IRunnableContext runnable, ExtractInterfaceFrExternalTypeConfiguration config) throws InvocationTargetException,
			InterruptedException {
		IFile interfacefile;
		String interfacePkg = config.getFPackage();		
		String fileName = config.getFileName();
		
		Map filecontents = ExternalType2RuiInterfaceConverter.convert(config);
		
		StringBuffer interfaceContent = new StringBuffer();
		
		List<String> additionalFiles = new ArrayList<String>();
		for(Iterator it= filecontents.keySet().iterator(); it.hasNext();){
			String key = (String)it.next();
			if(key.compareToIgnoreCase(interfacePkg) != 0){
				additionalFiles.add(key);
			}
			else{
				interfaceContent = (StringBuffer)filecontents.get(key);
			}
		}
		
		ExtractInterfaceFrExternalTypeOperation op = new ExtractInterfaceFrExternalTypeOperation(config, interfaceContent);
		
		runnable.run(false, true, op);
		
		//save the interface file name, open this file at the end of the wizard operation
		interfacefile = config.getFile();		
		
		for(String pkgName : additionalFiles) {
			config.setFileName(fileName + WSDL2EGLAdditionalFileOperation.ADDITIONALDATAFILE_APPENDNAME);
			config.setFPackage(pkgName);
			
			StringBuffer fileContent_Data = (StringBuffer)(filecontents.get(pkgName));
			ExtractInterfaceFrExternalTypeOperation addtionalFileOp = new ExtractInterfaceFrExternalTypeOperation(config, fileContent_Data);
			runnable.run(false, true, addtionalFileOp);
		}
		
		return interfacefile;
	}
}
