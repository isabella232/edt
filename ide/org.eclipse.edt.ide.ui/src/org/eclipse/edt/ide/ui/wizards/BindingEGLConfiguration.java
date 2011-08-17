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

import java.util.Iterator;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.edt.compiler.binding.IAnnotationBinding;
import org.eclipse.edt.compiler.binding.IBinding;
import org.eclipse.edt.compiler.core.IEGLConstants;
import org.eclipse.edt.compiler.core.ast.AbstractASTVisitor;
import org.eclipse.edt.compiler.core.ast.Part;
import org.eclipse.edt.compiler.internal.EGLBasePlugin;
import org.eclipse.edt.compiler.internal.core.lookup.SystemEnvironmentPackageNames;
import org.eclipse.edt.ide.core.internal.compiler.workingcopy.IWorkingCopyCompileRequestor;
import org.eclipse.edt.ide.core.internal.compiler.workingcopy.WorkingCopyCompilationResult;
import org.eclipse.edt.ide.core.internal.compiler.workingcopy.WorkingCopyCompiler;
import org.eclipse.edt.ide.core.internal.utils.Util;
import org.eclipse.edt.ide.core.model.EGLCore;
import org.eclipse.edt.ide.core.model.EGLModelException;
import org.eclipse.edt.ide.core.model.IEGLElement;
import org.eclipse.edt.ide.core.model.IEGLFile;
import org.eclipse.edt.ide.core.model.IPart;
import org.eclipse.edt.ide.core.model.IWorkingCopy;
import org.eclipse.edt.ide.ui.internal.EGLUI;
import org.eclipse.edt.ide.ui.internal.deployment.Bindings;
import org.eclipse.edt.ide.ui.internal.deployment.Deployment;
import org.eclipse.edt.ide.ui.internal.deployment.DeploymentFactory;
import org.eclipse.edt.ide.ui.internal.deployment.EGLBinding;
import org.eclipse.edt.ide.ui.internal.deployment.EGLDeploymentRoot;
import org.eclipse.edt.ide.ui.internal.deployment.Protocol;
import org.eclipse.edt.ide.ui.internal.deployment.ui.CommTypes;
import org.eclipse.edt.ide.ui.internal.deployment.ui.EGLDDRootHelper;
import org.eclipse.edt.mof.egl.Service;
import org.eclipse.emf.ecore.util.FeatureMap;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IWorkbench;


public class BindingEGLConfiguration extends BindingBaseConfiguration {
	private String fAlias=""; //$NON-NLS-1$

	protected int fSelectedCommTypeBtnIndex;
	private EGLDeploymentRoot fDeploymentRoot;
	protected Protocol[] fNewProtocol;
	private IFile fEGLDDFile =null;

	public BindingEGLConfiguration()
	{
		setDefaultAttributes();
	}
	
	public BindingEGLConfiguration(EGLDeploymentRoot root, IProject proj){
		init(root, proj);
	}
	
	public void init(EGLDeploymentRoot root, IProject proj){
		fDeploymentRoot = root;
		fProj = proj;
		setDefaultAttributes();
	}
	
	public void init(IWorkbench workbench, IStructuredSelection selection){
		super.init(workbench, selection);
		IPart servicePart = null;		
		IEGLFile serviceFile = null;
		Object selectedElement= selection.getFirstElement();
		if(selectedElement instanceof IFile)
		{
		    IEGLElement eglElem = EGLCore.create((IFile)selectedElement);	
		    if(eglElem instanceof IEGLFile){
		        serviceFile = (IEGLFile)eglElem;
		    }
		}
		else if(selectedElement instanceof IEGLFile)
		{
		    serviceFile = (IEGLFile)selectedElement;
		}
		
		if(serviceFile != null){
			String eglFileName = serviceFile.getElementName();
			int dot = eglFileName.indexOf('.');
			String partSimpleName = eglFileName.substring(0, dot);
	        servicePart = serviceFile.getPart(partSimpleName);			
		}
		
		if(selectedElement instanceof IPart)
			servicePart = (IPart)selectedElement;
				
		if(servicePart != null){
			setBindingName(servicePart.getElementName());
			setEGLServiceOrInterface(servicePart.getFullyQualifiedName());
			setAlias(getAliasFrServicePart(servicePart));
			
			fProj = servicePart.getEGLProject().getProject();
		}
		
	}
	
	protected int getBindingType(){
		return EGLDDBindingConfiguration.BINDINGTYPE_EGL;
	}

	protected void setDefaultAttributes() {
		fSelectedCommTypeBtnIndex=1;
		
		List commtypeList = CommTypes.getSupportedProtocol(getBindingType());
		fNewProtocol = new Protocol[commtypeList.size()+1];
		
		fNewProtocol[0] = DeploymentFactory.eINSTANCE.createReferenceProtocol();
		
		int i=1;
		for(Iterator it=commtypeList.iterator();it.hasNext(); i++){
			CommTypes commtype = (CommTypes)it.next();
			fNewProtocol[i] = EGLDDRootHelper.createNewProtocol(commtype);
		}
	}
	
	public Protocol getProtocol(){
		return fNewProtocol[fSelectedCommTypeBtnIndex];
	}
	
	public String getAlias() {
		return fAlias;
	}

	public void setAlias(String alias) {
		fAlias = alias;
	}

	public int getSelectedCommTypeBtnIndex() {
		return fSelectedCommTypeBtnIndex;
	}

	public void setSelectedCommTypeBtnIndex(int index) {
		fSelectedCommTypeBtnIndex = index;
	}
		
	public EGLBinding executeAddEGLBinding(Bindings bindings) {
		EGLBinding eglBinding = DeploymentFactory.eINSTANCE.createEGLBinding();
		bindings.getEglBinding().add(eglBinding);
		
		eglBinding.setName(getBindingName());
		eglBinding.setServiceName(getEGLServiceOrInterface());
		eglBinding.setAlias(getAlias());
		
		FeatureMap protocolGrp = eglBinding.getProtocolGroup();
		EGLDDRootHelper.setProtocolOnProtocolGroup(protocolGrp, fNewProtocol[fSelectedCommTypeBtnIndex]);
		return eglBinding;
	}
	
	public void executeAddEGLBinding(){
		if(fEGLDDFile != null && !fEGLDDFile.exists()){
			//need to create egl deployment descriptor
			String encodingName = EGLBasePlugin.getPlugin().getPreferenceStore().getString(EGLBasePlugin.OUTPUT_CODESET);
			EGLDDRootHelper.createNewEGLDDFile(fEGLDDFile, encodingName);
		}
		
		fDeploymentRoot = EGLDDRootHelper.getEGLDDFileSharedWorkingModel(fEGLDDFile, false);
		
		if(fDeploymentRoot != null){
			try{
				Deployment deployment = fDeploymentRoot.getDeployment();
				Bindings bindings = deployment.getBindings();
				
				if(bindings == null){
					bindings = DeploymentFactory.eINSTANCE.createBindings();
					deployment.setBindings(bindings);
				}
				//try to see if user wants to override the existing one
				if(isOverwrite()){
					//if there is an existing one, remove it
					EGLBinding existingEGLBinding = EGLDDRootHelper.getEGLBindingByName(fDeploymentRoot, getBindingName());
					if(existingEGLBinding != null)
						bindings.getEglBinding().remove(existingEGLBinding);				
				}
				executeAddEGLBinding(bindings);
				
				//persist the file if we're the only client 
				if(!EGLDDRootHelper.isWorkingModelSharedByUserClients(fEGLDDFile))
					EGLDDRootHelper.saveEGLDDFile(fEGLDDFile, fDeploymentRoot);
			}
			finally{
				if(fEGLDDFile != null)
					EGLDDRootHelper.releaseSharedWorkingModel(fEGLDDFile, false);
			}
		}

	}
	
	public EGLDeploymentRoot getEGLDeploymentRoot(){
		return fDeploymentRoot;
	}
		
	public void setEGLDeploymentDescriptor(IFile eglddFile, EGLDeploymentRoot deploymentRoot){
		fEGLDDFile = eglddFile;		//eglddFile may not exist
		fDeploymentRoot = deploymentRoot;
	}
		
	public String getFileExtension() {
		return EGLDDRootHelper.EXTENSION_EGLDD;
	}
	
	public String getAliasFrServicePart(IPart servicePart) //throws CoreException
	{
		final String[] alias = new String[]{""}; //$NON-NLS-1$
		try{
			String servicePartName = servicePart.getElementName();
			
			//bind the ast tree with live env and scope
			IWorkingCopy[] currRegedWCs = EGLCore.getSharedWorkingCopies(EGLUI.getBufferFactory());
			
			IProject proj = servicePart.getEGLProject().getProject();		
			IEGLFile eglFile = servicePart.getEGLFile();
			IFile file = (IFile)(eglFile.getCorrespondingResource());
			
			String packageName = servicePart.getPackageFragment().getElementName();
			Path pkgPath = new Path(packageName.replace('.', IPath.SEPARATOR)); 					
			String[] pkgName = Util.pathToStringArray(pkgPath);
		
			//visit AST part tree(already bound)		
			WorkingCopyCompiler.getInstance().compilePart(proj, pkgName, file, currRegedWCs, servicePartName, 		
						new IWorkingCopyCompileRequestor(){			
							public void acceptResult(WorkingCopyCompilationResult result) {
								Part boundPart = (Part)result.getBoundPart();
								final IBinding partBinding = result.getPartBinding();							
								boundPart.accept(new AbstractASTVisitor(){
									public boolean visit(Service service){
										//make sure it is the one we care about
										IAnnotationBinding aliasAnnotationBinding = partBinding.getAnnotation(SystemEnvironmentPackageNames.EGL_CORE, IEGLConstants.PROPERTY_ALIAS);
										if(aliasAnnotationBinding != null)
										{
											alias[0] = aliasAnnotationBinding.getValue().toString();
										}									
										return false;											
									}								
								});
							}
					}
			);	
		} catch (EGLModelException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
		return alias[0];
	}
	
}
