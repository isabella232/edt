/*******************************************************************************
 * Copyright © 2008, 2011 IBM Corporation and others.
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

	protected final int PATH_WS_GEN = 1;	
	protected final int PATH_WS_USE = 2;
	protected final int PATH_EGL = 3;
	protected final int PATH_NATIVE = 4;
	protected final int PATH_REST = 5;
	
	protected int fPagePath = PATH_WS_GEN;
	
	protected IWizardPage[] PAGES_WS_GEN;
	protected IWizardPage[] PAGES_WS_USE;
	protected IWizardPage[] PAGES_EGL;
	protected IWizardPage[] PAGES_NATIVE;
	protected IWizardPage[] PAGES_REST;
	
	private Object fNewBinding;
	
	public EGLDDBindingWizard(){
		super();
		setDefaultPageImageDescriptor(PluginImages.DESC_WIZBAN_EXTERNALSERVICE);
	}
	
	public void init(IWorkbench workbench, IProject proj, EGLDeploymentRoot root) {
		getEGLDDBindingConfiguration().init(workbench, proj, root);
		setWindowTitle(NewWizardMessages.EGLDDBindingWizTitle);
	}
	
	public EGLPackageConfiguration getConfiguration() {
		if(configuration == null)
			configuration = new EGLDDBindingConfiguration();
		return configuration;
	}
	
	public EGLPackageConfiguration getConfiguration(String pageName){
		EGLDDBindingConfiguration eglddConfig = getEGLDDBindingConfiguration();
//		if(pageName.equals(WebBindingWizardPage.WIZPAGENAME_WebBindingWizardPage))
//			return eglddConfig.getBindingWebConfiguration();
//		else if(pageName.equals(WSDL2EGLBindingWizardPage.WIZPAGENAME_WSDL2EGLBindingWizardPage) ||
//				pageName.equals(WSDLInterfaceWizardPage.WIZPAGENAME_WSDLInterfaceWizardPage))
//			return eglddConfig.getBindingWebConfiguration().getWSDL2EGLConfig();
//		else 
		if(pageName.equals(EGLBindingWizardPage.WIZPAGENAME_EGLBindingWizardPage))
			return eglddConfig.getBindingEGLConfiguration();
//		else if(pageName.equals(NativeBindingWizardPage.WIZPAGENAME_NativeBindingWizardPage))
//			return eglddConfig.getBindingNativeConfiguration();
		else if(pageName.equals(RestBindingWizardPage.WIZPAGENAME_RestBindingWizardPage))
			return eglddConfig.getBindingRestConfiguration();
		
		return super.getConfiguration(pageName);
	}
	
	private EGLDDBindingConfiguration getEGLDDBindingConfiguration()
	{
		return (EGLDDBindingConfiguration)getConfiguration();
	}
	
    public boolean canFinish() {
     	switch(fPagePath){
    	case(PATH_WS_GEN):
    		return canPagePathFinish(PAGES_WS_GEN);		//only care about web service related pages
    	case(PATH_WS_USE):
    		return canPagePathFinish(PAGES_WS_USE);		//only care about web service related pages    	
    	case(PATH_EGL):
    		return canPagePathFinish(PAGES_EGL);	//only care about tcpip related pages
    	case(PATH_NATIVE):
    		return canPagePathFinish(PAGES_NATIVE);
    	case(PATH_REST):
    		return canPagePathFinish(PAGES_REST);
    	}
    	return super.canFinish();
    }
	
    public void addPages() {
//		IWizardPage page1 = new EGLDDBindingWizardPage(EGLDDBindingWizardPage.WIZPAGENAME_EGLDDBindingWizardPage);
//		IWizardPage page2 = new WebBindingWizardPage(WebBindingWizardPage.WIZPAGENAME_WebBindingWizardPage);
//		IWizardPage page3 = new WSDL2EGLBindingWizardPage(WSDL2EGLBindingWizardPage.WIZPAGENAME_WSDL2EGLBindingWizardPage);
//		IWizardPage page4 = new WSDLInterfaceWizardPage(WSDLInterfaceWizardPage.WIZPAGENAME_WSDLInterfaceWizardPage);
//		IWizardPage page5 = new EGLBindingWizardPage(EGLBindingWizardPage.WIZPAGENAME_EGLBindingWizardPage);
//		IWizardPage page6 = new NativeBindingWizardPage(NativeBindingWizardPage.WIZPAGENAME_NativeBindingWizardPage);
		IWizardPage page7 = new RestBindingWizardPage(RestBindingWizardPage.WIZPAGENAME_RestBindingWizardPage);
		
//		PAGES_WS_GEN = new IWizardPage[]{page1, page2, page3, page4};
//		PAGES_WS_USE = new IWizardPage[]{page1, page2};
//		PAGES_EGL = new IWizardPage[]{page1, page5};
//		PAGES_NATIVE = new IWizardPage[]{page1, page6};
//		PAGES_REST = new IWizardPage[]{page1, page7};

//		addPage(page1);
//		addPage(page2);
//		addPage(page3);		
//		addPage(page4);
//		addPage(page5);		
//		addPage(page6);
		addPage(page7);
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
			if(bindingType == EGLDDBindingConfiguration.BINDINGTYPE_WEB){
				//TODO to be added later
//				nextPage = getPage(WebBindingWizardPage.WIZPAGENAME_WebBindingWizardPage);
//				fPagePath = PATH_WS_GEN;
			}
			else if(bindingType == EGLDDBindingConfiguration.BINDINGTYPE_EGL){
				nextPage = getPage(EGLBindingWizardPage.WIZPAGENAME_EGLBindingWizardPage);
				fPagePath = PATH_EGL;
			}		
			else if(bindingType == EGLDDBindingConfiguration.BINDINGTYPE_NATIVE){
				//TODO to be added later
//				nextPage = getPage(NativeBindingWizardPage.WIZPAGENAME_NativeBindingWizardPage);
//				fPagePath = PATH_NATIVE;
			}
			else if(bindingType == EGLDDBindingConfiguration.BINDINGTYPE_REST){
				nextPage = getPage(RestBindingWizardPage.WIZPAGENAME_RestBindingWizardPage);
				fPagePath = PATH_REST;
			}
		}
//		else if(currPageName.equals(WebBindingWizardPage.WIZPAGENAME_WebBindingWizardPage))
//		{			
//			if(getEGLDDBindingConfiguration().getBindingWebConfiguration().isGenEGLInterfaceFrWSDL()){
//				nextPage = (WSDL2EGLBindingWizardPage)getPage(WSDL2EGLBindingWizardPage.WIZPAGENAME_WSDL2EGLBindingWizardPage);
//				fPagePath = PATH_WS_GEN;
//			}
//			else{
//				nextPage = null;		//no more next page
//				fPagePath = PATH_WS_USE;
//			}
//		}
//		else if(currPageName.equals(WSDL2EGLBindingWizardPage.WIZPAGENAME_WSDL2EGLBindingWizardPage)){
//			nextPage = (WSDLInterfaceWizardPage)getPage(WSDLInterfaceWizardPage.WIZPAGENAME_WSDLInterfaceWizardPage);
//			fPagePath = PATH_WS_GEN;
//		}
//		else if(currPageName.equals(WSDLInterfaceWizardPage.WIZPAGENAME_WSDLInterfaceWizardPage)){
//			nextPage = null;
//			fPagePath = PATH_WS_GEN;
//		}
		else if(currPageName.equals(EGLBindingWizardPage.WIZPAGENAME_EGLBindingWizardPage)){
			nextPage = null;
			fPagePath = PATH_EGL;
		}
//		else if(currPageName.equals(NativeBindingWizardPage.WIZPAGENAME_NativeBindingWizardPage)){
//			nextPage = null;
//			fPagePath = PATH_NATIVE;
//		}
		else if(currPageName.equals(RestBindingWizardPage.WIZPAGENAME_RestBindingWizardPage)){
			nextPage = null;
			fPagePath = PATH_REST;
		}
		else
			nextPage = super.getNextPage(currentPage);
		
		if(nextPage instanceof EGLElementWizardPage)
		{
			((EGLElementWizardPage)nextPage).updateControlValues();		//reset the UI value based on the up2date configuration
		}
		
		return nextPage;		
			
	}
	
	
	public boolean performFinish() {		
		//create a new external service node, add it to the EGLModuleRoot
		try {
			executeFinishOperations();
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
