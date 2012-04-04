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
package org.eclipse.edt.ide.ui.internal.wizards;

import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.edt.ide.ui.internal.EGLLogger;
import org.eclipse.edt.ide.ui.internal.PluginImages;
import org.eclipse.edt.ide.ui.internal.deployment.EGLDeploymentRoot;
import org.eclipse.edt.ide.ui.wizards.EGLDDBindingConfiguration;
import org.eclipse.edt.ide.ui.wizards.EGLPackageConfiguration;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.ui.IWorkbench;

public class EGLDDBindingWizard extends EGLPartWizard {


	protected int currentPageIndex = 0;
	protected IWizardPage[] PAGES;
	
	private Object fNewBinding;
	
	public EGLDDBindingWizard(){
		super();
		setDefaultPageImageDescriptor(PluginImages.DESC_WIZBAN_EXTERNALSERVICE);
	}
	
	public void init(IWorkbench workbench, IProject proj, EGLDeploymentRoot root) {
		getEGLDDBindingConfiguration().init(workbench, proj, root);
		initBindingConfiguration(proj,root);
		setWindowTitle(NewWizardMessages.EGLDDBindingWizTitle);
	}
	
	private void initBindingConfiguration(IProject proj, EGLDeploymentRoot root){
		EGLDDBindingWizardProvider[] providers = EGLDDBindingProviderRegistry.singleton.getEGLDDBindingWizardProviders();
		for ( int i = 0; i < providers.length; i ++ ) {
			providers[i].bindingconfigurationClass.init(root, proj);
		}
	}
	
	public EGLPackageConfiguration getConfiguration() {
		if(configuration == null)
			configuration = new EGLDDBindingConfiguration();
		return configuration;
	}
	
	public EGLPackageConfiguration getConfiguration(String pageName){
		EGLDDBindingWizardProvider[] providers = EGLDDBindingProviderRegistry.singleton.getEGLDDBindingWizardProviders();
		for ( int i = 0; i < providers.length; i ++ ) {
			EGLDDBindingWizardPages[] pages = providers[i].eglDDBindingWizardPages;
			for ( int j = 0; j < pages.length; j ++ ) {
				if ( pages[j].name.equals( pageName ) ) {
					return providers[i].bindingconfigurationClass;
				}
			}
		}
		
		return super.getConfiguration(pageName);
	}
	
	private EGLDDBindingConfiguration getEGLDDBindingConfiguration()
	{
		return (EGLDDBindingConfiguration)getConfiguration();
	}
	
    public boolean canFinish() {
    	return PAGES != null && canPagePathFinish( PAGES );
    }
	
    public void addPages() {
		IWizardPage page1 = new EGLDDBindingWizardPage(EGLDDBindingWizardPage.WIZPAGENAME_EGLDDBindingWizardPage);
		addPage(page1);
		
		EGLDDBindingWizardProvider[] providers = EGLDDBindingProviderRegistry.singleton.getEGLDDBindingWizardProviders();
		for ( int i = 0; i < providers.length; i ++ ) {
			EGLDDBindingWizardPages[] pages = providers[i].eglDDBindingWizardPages;
			for ( int j = 0; j < pages.length; j ++ ) {
				addPage( pages[j].wizardpage );
			}
		}
	}
	
	public IWizardPage getNextPage(IWizardPage page) {
		return updatePagePathAndNextPage(page);
	}
	
	public IWizardPage updatePagePathAndNextPage(IWizardPage currentPage) {
		String currPageName = currentPage.getName();
		IWizardPage nextPage = null;
		
		if(currPageName.equals(EGLDDBindingWizardPage.WIZPAGENAME_EGLDDBindingWizardPage)){
			//check for the binding type
			int bindingType = getEGLDDBindingConfiguration().getBindingType();
			
			EGLDDBindingWizardProvider[] providers = EGLDDBindingProviderRegistry.singleton.getEGLDDBindingWizardProviders();
			for ( int i = 0; i < providers.length; i ++ ) {
				if ( providers[i].bindingId == bindingType ) {
					IWizardPage[] pages = new IWizardPage[ providers[i].eglDDBindingWizardPages.length + 1 ];
					for ( int j = 0; j < providers[i].eglDDBindingWizardPages.length; j ++  ) {
						pages[j+1] = providers[i].eglDDBindingWizardPages[j].wizardpage;
					}
					pages[0] = this.getPage( EGLDDBindingWizardPage.WIZPAGENAME_EGLDDBindingWizardPage ); 
					PAGES = pages;
					nextPage = pages[1];
					currentPageIndex++;
					break;
//					configuration = providers[i].bindingconfigurationClass;
				}
			}
		}else if(currPageName.equals(EGLBindingWizardPage.WIZPAGENAME_EGLBindingWizardPage)){
			if ( currentPageIndex < PAGES.length ) {
				nextPage = PAGES[++currentPageIndex];
			} else {
				nextPage = null;
			}
		}
		
		return nextPage;		
			
	}
	
	
	public boolean performFinish() {		
		//create a new external service node, add it to the EGLModuleRoot
		try {
			executeFinishOperations();
			IWizardPage[] pages = getPages();
			for(IWizardPage page : pages) {
				if(page instanceof EGLDDBindingWizardPage) {
					EGLDDBindingWizardPage thePage = (EGLDDBindingWizardPage)page;
					thePage.finish();
				}
			}
		} catch (InvocationTargetException e) {
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
		} catch (InterruptedException e) {
			e.printStackTrace();
			EGLLogger.log(this, e);
			return false;			
		}
		
		return true;
	}	

	private void executeFinishOperations() throws InvocationTargetException, InterruptedException{
		fNewBinding = getEGLDDBindingConfiguration().executeAddBinding(getContainer());		
	}
	
	public Object getNewBinding(){
		return fNewBinding;
	}
}
