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
package org.eclipse.edt.ide.ui.internal.wizards;

import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.jobs.ISchedulingRule;
import org.eclipse.edt.ide.ui.internal.EGLLogger;
import org.eclipse.edt.ide.ui.internal.PluginImages;
import org.eclipse.edt.ide.ui.wizards.EGLFileOperation;
import org.eclipse.edt.ide.ui.wizards.EGLPackageConfiguration;
import org.eclipse.edt.ide.ui.wizards.ExtractInterfaceConfiguration;
import org.eclipse.edt.ide.ui.wizards.ExtractInterfaceOperation;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IWorkbench;

public class ExtractInterfaceWizard extends EGLPartWizard {
	
	public ExtractInterfaceWizard() {
		super();
		
		setDefaultPageImageDescriptor(PluginImages.DESC_WIZBAN_NEWEGLINTERFACE);
	}

	public EGLPackageConfiguration getConfiguration() {
		if (configuration == null)
			configuration = new ExtractInterfaceConfiguration();
		return configuration;
	}	
    
	@Override
	public void init(IWorkbench workbench, IStructuredSelection selection) {
		getConfiguration().init(workbench, selection);
		setWindowTitle(NewWizardMessages.NewEGLPartCreationWizardTitle);
	}	
	
	public void addPages() {
		addPage(new ExtractInterfaceWizardPage(ExtractInterfaceWizardPage.WIZPAGENAME_ExtractInterfaceWizardPage));
	}
		
	public boolean performFinish() {
		if (!super.performFinish())
			return false;
		
		boolean success = runExtractInterfaceOp();
		
		return success;
	}

	protected boolean runExtractInterfaceOp() {
		boolean success = true;
		
		try{
			getContainer().run(canRunForked(), true, getOperation());
		} catch (InterruptedException e) {
			EGLLogger.log(this, e);
			success = false;
		} catch (InvocationTargetException e) {
			if(e.getTargetException() instanceof CoreException) {
				ErrorDialog.openError(
					getContainer().getShell(),
					null,
					null,
					((CoreException) e.getTargetException()).getStatus());
			} else {
				EGLLogger.log(this, e);
			}
			success = false;
		}
				
		//open the file
		openResource(configuration.getFile());
		return success;
	}	
	
	private EGLFileOperation getOperation() {
		ISchedulingRule rule= getCurrentSchedulingRule();
		ExtractInterfaceOperation operation = null;
		if (rule != null){
			operation = new ExtractInterfaceOperation((ExtractInterfaceConfiguration)getConfiguration(), rule);
		}else{
			operation = new ExtractInterfaceOperation((ExtractInterfaceConfiguration)getConfiguration());
		}
		
	    return(operation);
	}
	
}
