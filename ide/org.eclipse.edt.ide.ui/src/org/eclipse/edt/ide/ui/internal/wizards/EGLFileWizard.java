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
package org.eclipse.edt.ide.ui.internal.wizards;

import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.jobs.ISchedulingRule;
import org.eclipse.edt.ide.ui.EDTUIPlugin;
import org.eclipse.edt.ide.ui.internal.EGLLogger;
import org.eclipse.edt.ide.ui.internal.PluginImages;
import org.eclipse.edt.ide.ui.wizards.EGLFileConfiguration;
import org.eclipse.edt.ide.ui.wizards.EGLFileOperation;
import org.eclipse.edt.ide.ui.wizards.EGLPackageConfiguration;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.ide.IDE;

public class EGLFileWizard extends EGLPackageWizard implements INewWizard {
	
	private static final String WIZPAGENAME_EGLFileWizardPage = "WIZPAGENAME_EGLFileWizardPage"; //$NON-NLS-1$
	
	protected EGLFileConfiguration configuration;

	private boolean openResourceOnFinish;
	
	public EGLFileWizard() {
		this(true);
	}
	
	public EGLFileWizard(boolean openResourceOnFinish) {
		super();
		setDefaultPageImageDescriptor(PluginImages.DESC_WIZBAN_NEWSRCFILE);
		setDialogSettings(EDTUIPlugin.getDefault().getDialogSettings());
		this.openResourceOnFinish = openResourceOnFinish;
	}
	
	public boolean defaultPackageCheckQuestion() {
		//if package name not specified, give warning message
		if (getConfiguration().getFPackage().length() == 0)
			return MessageDialog.openQuestion(getShell(), 
					NewWizardMessages.NewEGLPartWizardPackageNameTitle,
					NewWizardMessages.NewEGLPartWizardDefaultPackageWarning);
		else
			return true;
	}
	
	private EGLFileOperation getOperation() {
		ISchedulingRule rule= getCurrentSchedulingRule();
		EGLFileOperation operation = null;
		if (rule != null){
			operation = new EGLFileOperation((EGLFileConfiguration)getConfiguration(), rule);
		}else{
			operation = new EGLFileOperation((EGLFileConfiguration)getConfiguration());
		}
		
	    return(operation);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.wizard.IWizard#performFinish()
	 */
	public boolean performFinish() {
		if (!defaultPackageCheckQuestion())
			return false;
		
		try{
			getContainer().run(canRunForked(), true, getOperation());
		}
		catch (InterruptedException e) {
			return false;
		}
		catch (InvocationTargetException e) {
			if(e.getTargetException() instanceof CoreException) {
				ErrorDialog.openError(
					getContainer().getShell(),
					null,
					null,
					((CoreException) e.getTargetException()).getStatus());
			}
			else {
				EGLLogger.log(this, e);
			}
			return false;
		}
		
		//open up the file
		if(openResourceOnFinish) {			
			openResource(configuration.getFile());
		}
		
		return true;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.IWorkbenchWizard#init(org.eclipse.ui.IWorkbench, org.eclipse.jface.viewers.IStructuredSelection)
	 */
	public void init(IWorkbench workbench, IStructuredSelection selection) {
		getConfiguration().init(workbench, selection);
		setWindowTitle(NewWizardMessages.NewEGLPartCreationWizardTitle);
	}
	
	public EGLPackageConfiguration getConfiguration() {
		if (configuration == null)
			configuration = new EGLFileConfiguration();
		return configuration;
	}
	
	public void addPages() {
		addPage(new EGLFileWizardPage(WIZPAGENAME_EGLFileWizardPage));
	}
	
	protected void openResource(final IFile resource) {

		final IWorkbenchPage activePage;
		IWorkbenchWindow window= PlatformUI.getWorkbench().getActiveWorkbenchWindow();
		if (window == null)
			activePage = null;
		else
			activePage = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
		
		if (activePage != null) {
			final Display display= getShell().getDisplay();
			if (display != null) {
				display.asyncExec(new Runnable() {
					public void run() {
						try {
							IDE.openEditor(activePage, resource, true);
						} catch (PartInitException e) {
							EDTUIPlugin.log(e);
						}
					}
				});
			}
		}
	}
}
