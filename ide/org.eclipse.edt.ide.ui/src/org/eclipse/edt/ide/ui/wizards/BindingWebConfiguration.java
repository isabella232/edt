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
package org.eclipse.edt.ide.ui.wizards;

import java.lang.reflect.InvocationTargetException;
import java.util.Hashtable;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.edt.ide.core.model.EGLCore;
import org.eclipse.edt.ide.core.model.EGLModelException;
import org.eclipse.edt.ide.core.model.IEGLProject;
import org.eclipse.edt.ide.ui.internal.deployment.Bindings;
import org.eclipse.edt.ide.ui.internal.deployment.DeploymentFactory;
import org.eclipse.edt.ide.ui.internal.deployment.WebBinding;
import org.eclipse.edt.ide.ui.internal.wizards.NewWizardMessages;
import org.eclipse.jface.operation.IRunnableContext;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.ui.IWorkbench;

public class BindingWebConfiguration extends BindingBaseConfiguration {
	
//	protected IWorkbench fWorkbench;	
//	//WS type
//	protected String fWSDLLocation;
//	
//	protected String fWSDLUri;
//
//	private boolean fBGenEGLInterfaceFrWSDL;
//	private WSDLConfiguration fWSDL2EGLConfig;
//	
	public BindingWebConfiguration(IWorkbench workbench, IProject proj){
//		fWorkbench = workbench;
//		fProj = proj;
//		setDefaultAttributes();
	}
//	
//	private void setDefaultAttributes()
//	{
//		fWSDLLocation = "";		 //$NON-NLS-1$
//		fWSDLUri = ""; //$NON-NLS-1$
//		
//		setGenEGLInterfaceFrWSDL(true);		//init to be true
//		fWSDL2EGLConfig = new WSDLConfiguration();
//	}	
//	
//	public void setGenEGLInterfaceFrWSDL(boolean genEGLInterfaceFrWSDL) {
//		fBGenEGLInterfaceFrWSDL = genEGLInterfaceFrWSDL;
//	}
//
//	public boolean isGenEGLInterfaceFrWSDL() {
//		return fBGenEGLInterfaceFrWSDL;
//	}
//	
//	public String getWSDLLocation() { return fWSDLLocation;}	
//	
//	public void setWSDLLocation(String location) {
//		fWSDLLocation = location;
//		
//		if(fBGenEGLInterfaceFrWSDL && fWSDLLocation.length()>0)		//for external service only
//		{
//			//get the workspace Root
//			IFile wsdlFile = getWSDLFile(fWSDLLocation);//this location is a full path to the workspace root
//			if(wsdlFile != null && wsdlFile.exists())
//				initWSDLConfiguration(wsdlFile);
//		}		
//	}
//
//	/**
//	 * 
//	 * @param wsdlLocation - this location is a full path to the workspace root
//	 * @return
//	 */
//	public IFile getWSDLFile(String wsdlLocation){
//		IWorkspaceRoot root = getProject().getWorkspace().getRoot();
//		IFile wsdlFile = (IFile)root.findMember(new Path(wsdlLocation)); //this location is a full path to the workspace root
//		return wsdlFile;
//	}
//	
//	private void initWSDLConfiguration(IFile wsdlFile)
//	{
//		StructuredSelection ssel = new StructuredSelection(wsdlFile);
//		fWSDL2EGLConfig.init(fWorkbench, ssel);
//		//set overwrite the exsiting file by default
//		fWSDL2EGLConfig.setOverwrite(true);
//		
//		try{
//			IProject wsdlProject = wsdlFile.getProject();
//			String wsdlProjectName = wsdlProject.getName();
//			//is the wsdlProject one of the current project's referenced projects?
//			IEGLProject eglProj = EGLCore.create(fProj);
//			String[] refProjNames = eglProj.getRequiredProjectNames();
//			boolean wsdlIsRefProj = false;
//			for(int i=0; i<refProjNames.length && !wsdlIsRefProj; i++){
//				if(wsdlProjectName.equals(refProjNames[i]))
//					wsdlIsRefProj = true;
//			}		
//			
//			String currProjName = fProj.getName();
//			//set the wsdl configuration's container name to be the current project's container name
//			//so by default, the interface will be generated to the current project
//			if(!wsdlProjectName.equals(currProjName) && !wsdlIsRefProj)	{
//				IResource fragRootResource = eglProj.getPackageFragmentRoots()[0].getUnderlyingResource();
//				IPath path = new Path(currProjName);
//				path = path.append(fragRootResource.getProjectRelativePath());
//				fWSDL2EGLConfig.setContainerName(path.toOSString());
//			}
//		} catch (EGLModelException e) {
//			e.printStackTrace();
//		}
//				
//	}
//	
//	public WSDLConfiguration getWSDL2EGLConfig() {
//		if(fBGenEGLInterfaceFrWSDL)
//			return fWSDL2EGLConfig;
//		return null;
//	}
//
	public WebBinding executeAddWebBinding(IRunnableContext runnableContext, Bindings bindings) throws InvocationTargetException, InterruptedException{		
		WebBinding webBinding = DeploymentFactory.eINSTANCE.createWebBinding();
		bindings.getWebBinding().add(webBinding);
		
//		executeGenInterfaceFrWSDL(runnableContext, webBinding);
		return webBinding;
	}
//
//	private void executeGenInterfaceFrWSDL(IRunnableContext runnableContext, WebBinding webBinding) throws InvocationTargetException, InterruptedException {
//		//get the wsdl file
//		String wsdlLocation = getWSDLLocation();			
//		IWorkspaceRoot root = getProject().getWorkspace().getRoot();
//		IFile wsdlFile = root.getFile(new Path(wsdlLocation));			//this location is a full path to the workspace root
//				
//		String eglInterface="";		 //$NON-NLS-1$
//		String wsdlPort =""; //$NON-NLS-1$
//		String wsdlService=""; //$NON-NLS-1$
//		if(isGenEGLInterfaceFrWSDL()){
//			WSDLConfiguration wsdlConfig = getWSDL2EGLConfig();
//			String EGLInterfacePkgName = wsdlConfig.getFPackage();
//			WSDL2EGLOperation opWSDL2EGL = new WSDL2EGLOperation(wsdlConfig);
//			runnableContext.run(false, true, opWSDL2EGL);
//			
//			//generate any necessary additional data files
//			Hashtable hash = opWSDL2EGL.getAdditionalDataFile();
//			WSDL2EGLInterfaceWizard.runWSDL2EGLAddtionalFileOperation(runnableContext, wsdlConfig, hash);
//			
//			//try to get the generated egl interface's fully qualified name
//			String[] eglInterfaceResult = getGeneratedEGLInterfaceAndBindingName(wsdlConfig, EGLInterfacePkgName);
//			eglInterface = eglInterfaceResult[0];
//			wsdlPort = eglInterfaceResult[1];
//			wsdlService = eglInterfaceResult[2];
//		}
//		else{
//			eglInterface = getEGLServiceOrInterface();
//			if(wsdlFile.exists()){
//				String[] result = getWSDLBindingInfo(wsdlFile);
//				wsdlPort = result[0];
//				wsdlService = result[1];
//			}
//		}
//		
//		//also we need to copy wsdl file to the EGLSource location if the wsdl file is not on the EGL path
//		//use the new wsdlLocation path
//		try{
//			wsdlLocation = WSDLParseUtil.copyWSDL2ProjectPath(getProject(), wsdlFile, new NullProgressMonitor());
//		}catch (CoreException e) {
//			e.printStackTrace();
//			throw new InvocationTargetException(e);
//		}
//		
//		webBinding.setName(getBindingName());
//		webBinding.setInterface(eglInterface);
//		webBinding.setWsdlLocation(wsdlLocation);
//		webBinding.setWsdlPort(wsdlPort);
//		webBinding.setWsdlService(wsdlService);
//		webBinding.setUri(getWSDLUri());	
//		webBinding.setEnableGeneration(true);
//	}
//	
//	/**
//	 * 
//	 * @param wsdlConfig
//	 * @return 1st is the interface fully qaulified name, 2nd is the port name , 3rd is the service name
//	 */
//	public static String[] getGeneratedEGLInterfaceAndBindingName(WSDLConfiguration wsdlConfig, String EGLInterfacePkgName){		
//        //get all teh selected wsdl Ports for the selected interfaces
//		boolean[] portsSelectionState = wsdlConfig.getWSDLPortsSelectionState();
//		String[] interfaceAndBinding = {"", "", ""}; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
//		for(int i=0; i<portsSelectionState.length; i++)
//        {
//			if(portsSelectionState[i]){	    					
//            	WSDLPort keyWSDLPort = wsdlConfig.getWSDLPortInWSDL(i);
//            	EInterface einterface = keyWSDLPort.getInterface();
//            	int interfaceindex = wsdlConfig.getInterfaceElementIndex(einterface);
//            	InterfaceConfiguration interfaceconfig = wsdlConfig.getInterfaceConfiguration(interfaceindex);
//            	
//	            //for each interface, get its fully qualified name
//	            String FQEGLInterfaceName = EGLInterfacePkgName;
//	            if(FQEGLInterfaceName.length()>0)
//	            	FQEGLInterfaceName += '.';
//	            FQEGLInterfaceName += interfaceconfig.getInterfaceName();
//	            interfaceAndBinding[0] = FQEGLInterfaceName;
//	            interfaceAndBinding[1] = keyWSDLPort.getName();
//	            interfaceAndBinding[2] = keyWSDLPort.getServiceName();
//			}
//        }
//		return interfaceAndBinding;
//	}
//	
//	/**
//	 * 
//	 * @param wsdlLocation
//	 * @param wsdlFile
//	 * @return the 1st is the port name
//	 * 			   2nd is the service name
//	 * 
//	 * @throws InvocationTargetException
//	 */
//	public static String[] getWSDLBindingInfo(IFile wsdlFile) throws InvocationTargetException {
//		String wsdlLocation = wsdlFile.getFullPath().toOSString();
//		String[] result = {"", ""}; //$NON-NLS-1$ //$NON-NLS-2$
//
//		WSDLPort[] ports;
//		try {
//			UIModel uiModel = new UIModel(wsdlFile);
//			ports = uiModel.createWSDLPorts();
//			if(ports.length>0){
//				result[0] = ports[0].getName();	//port name 
//				result[1] = ports[0].getServiceName();			//service value				
//			}
//			else{
//				String errMsg = NewWizardMessages.bind(NewWizardMessages.ValidateWSDLLocationNoBindingInfo, new String[]{wsdlLocation});
//				IStatus status = new Status(Status.ERROR, EGLUIPlugin.PLUGIN_ID, 0, errMsg, null);
//				CoreException coreException = new CoreException(status);
//				throw new InvocationTargetException(coreException);
//			}
//		} 
//		catch(UIModelException wsdlexp){
//			IStatus status = new Status(Status.ERROR, EGLUIPlugin.PLUGIN_ID, 0, wsdlexp.getMessage(), wsdlexp);
//			CoreException coreException = new CoreException(status);
//			throw new InvocationTargetException(coreException);
//		}
//		catch (Exception e) {
//			e.printStackTrace();
//			
//			String errMsg = NewWizardMessages.bind(NewWizardMessages.OpenWSDL2EGLWizardActionParsingErrorMessage, new String[]{wsdlLocation});
//			IStatus status = new Status(Status.ERROR, EGLUIPlugin.PLUGIN_ID, 0, errMsg, e);
//			CoreException coreException = new CoreException(status);
//			throw new InvocationTargetException(coreException);
//		}
//		return result;
//	}
//
//	public String getWSDLUri() {
//		return fWSDLUri;
//	}
//
//	public void setWSDLUri(String uri) {
//		fWSDLUri = uri;
//	}	
//	
//	public String getDefaultCalculatedName(){
//		String eglInterface=""; //$NON-NLS-1$
//		if(isGenEGLInterfaceFrWSDL())
//			eglInterface = getGeneratedEGLInterfaceAndBindingName(getWSDL2EGLConfig(), "")[0]; //$NON-NLS-1$
//		else
//			eglInterface = getEGLServiceOrInterface();
//		
//		int lastDot = eglInterface.lastIndexOf('.');
//		return eglInterface.substring(lastDot+1);
//	}
}
