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
package org.eclipse.edt.ide.ui.internal.handlers.wizards;

import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.jobs.ISchedulingRule;
import org.eclipse.edt.ide.ui.EDTUIPlugin;
import org.eclipse.edt.ide.ui.internal.EGLLogger;
import org.eclipse.edt.ide.ui.internal.PluginImages;
import org.eclipse.edt.ide.ui.internal.wizards.EGLFileWizard;
import org.eclipse.edt.ide.ui.internal.wizards.EGLPartWizardPage;
import org.eclipse.edt.ide.ui.templates.wizards.TemplateWizardNode;
import org.eclipse.edt.ide.ui.wizards.EGLFileConfiguration;
import org.eclipse.edt.ide.ui.wizards.EGLPackageConfiguration;
import org.eclipse.edt.ide.ui.wizards.PartTemplateException;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.IWizardNode;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IEditorDescriptor;
import org.eclipse.ui.IEditorRegistry;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWizard;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.ide.IDE;

public class NewHandlerWizard extends EGLFileWizard {
	private static final String WIZPAGENAME_HandlerWizardPage = "WIZPAGENAME_HandlerWizardPage"; //$NON-NLS-1$
	private static String Visual_Editor_Registered_ID = "org.eclipse.edt.ide.rui.visualeditor.EvEditor";   //$NON-NLS-1$

	private NewHandlerWizardPage mainPage;
	private ISelection selection;
	private Object contentObj; // represents

	public NewHandlerWizard() {
		super();
		setWindowTitle(NewHandlerWizardMessages.NewHandlerWizard_title);
		setDefaultPageImageDescriptor(PluginImages.DESC_WIZBAN_NEWRUIHANDLER);
	}

	/**
	 * Adding the page to the wizard.
	 */

	public void addPages() {
		mainPage = new NewHandlerWizardPage(WIZPAGENAME_HandlerWizardPage);
		addPage(mainPage);
	}
	
	public boolean needsPreviousAndNextButtons() {
		return true;
	}

	@Override
	public boolean performFinish() {
		if (!super.performFinish())
			return false;

		// If a page of the dynamically embedded template wizard is not
		// currently being displayed, the performFinish() on this wizard will
		// not get displayed. This code ensures this happens.		
		IWizardNode node = mainPage.getSelectedNode();
		if (node instanceof TemplateWizardNode) {
			TemplateWizardNode twn = (TemplateWizardNode) node;
			if (twn.getTemplate().hasWizard()) {
				if (!twn.getWizard().performFinish()) {
					return false;
				}
			}
		}
		
		IRunnableWithProgress operation = getOperation();
		if(operation != null){
			try {
				getContainer().run(canRunForked(), true, getOperation());
			} catch (InterruptedException e) {
				boolean dialogResult = false;
				if (e.getMessage().indexOf(':') != -1) {
					PartTemplateException pe = new PartTemplateException(e.getMessage());
					if (pe.getTemplateExcpetion().compareTo(EGLFileConfiguration.TEMPLATE_NOT_FOUND) == 0) {
						dialogResult = ((EGLPartWizardPage) this.getPage(WIZPAGENAME_HandlerWizardPage)).handleTemplateError(pe.getPartType(),
								pe.getPartDescription()); //$NON-NLS-1$
					} else if (pe.getTemplateExcpetion().compareTo(EGLFileConfiguration.TEMPLATE_DISABLED) == 0) {
						// is there a way to tell this?
					} else if (pe.getTemplateExcpetion().compareTo(EGLFileConfiguration.TEMPLATE_CORRUPTED) == 0) {
						dialogResult = ((EGLPartWizardPage) this.getPage(WIZPAGENAME_HandlerWizardPage)).handleTemplateError(pe.getPartType(),
								pe.getPartDescription()); //$NON-NLS-1$
					}

					if (dialogResult)
						return performFinish();
					else
						return false;
				} else {
					EGLLogger.log(this, e);
					return false;
				}
			} catch (InvocationTargetException e) {
				if (e.getTargetException() instanceof CoreException) {
					ErrorDialog.openError(getContainer().getShell(), null, null, ((CoreException) e.getTargetException()).getStatus());
				} else {
					EGLLogger.log(this, e);
				}
				return false;
			}
		}

		// open the file
		openResource(configuration.getFile());

		return true;
	}
	
	public void setContentObj(Object obj) {
		this.contentObj = obj;
	}

	/**
	 * We will accept the selection in the workbench to see if we can initialize
	 * from it.
	 * 
	 * @see IWorkbenchWizard#init(IWorkbench, IStructuredSelection)
	 */
	public void init(IWorkbench workbench, IStructuredSelection selection) {
		this.selection = selection;
		getConfiguration().init(workbench, selection);
	}
	
	public EGLPackageConfiguration getConfiguration() {
		if (configuration == null)
			configuration = new HandlerConfiguration();
		return configuration;
	}
	
	protected IRunnableWithProgress getOperation() {
		String codeTemplateId = null;

		IWizardNode node = mainPage.getSelectedNode();
		if (node instanceof TemplateWizardNode) {
			TemplateWizardNode twn = (TemplateWizardNode) node;
			if (!twn.getTemplate().hasWizard() && twn.getTemplate().getCodeTemplateId() != null) {
				codeTemplateId = twn.getTemplate().getCodeTemplateId();				
			}
		}
		
		HandlerOperation operation = null;
		ISchedulingRule rule= getCurrentSchedulingRule();
		if(null != rule){
			operation = new HandlerOperation((HandlerConfiguration) getConfiguration(), codeTemplateId, contentObj, rule);
		}else{
			operation = new HandlerOperation((HandlerConfiguration) getConfiguration(), codeTemplateId, contentObj);
		}
		
		return (operation);		
	}
	

	protected void openResource(final IFile resource) {
		final IWorkbenchPage activePage;
		IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
		if (window == null)
			activePage = null;
		else
			activePage = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
		
		if (activePage != null) {
			final Display display = getShell().getDisplay();
			if (display != null) {
				display.asyncExec(new Runnable() {
					public void run() {
						try {
							String editorID = null;
							if (((HandlerConfiguration) configuration)
									.getHandlerType() == HandlerConfiguration.HANDLER_HANDLER) {
								editorID = Visual_Editor_Registered_ID;
							} else {
								IEditorDescriptor desc = IDE.getDefaultEditor(resource);
								if (desc == null) {
									editorID = EDTUIPlugin.getDefault().getWorkbench()
											.getEditorRegistry().findEditor(
													IEditorRegistry.SYSTEM_EXTERNAL_EDITOR_ID).getId();
								} else {
									editorID = desc.getId();
								}
							}
							IDE.openEditor(activePage, resource, editorID);
						} catch (PartInitException e) {
							EDTUIPlugin.log(e);
						}
					}
				});
			}
		}
	}

	protected boolean canRunForked() {
		return false;
	}
}
