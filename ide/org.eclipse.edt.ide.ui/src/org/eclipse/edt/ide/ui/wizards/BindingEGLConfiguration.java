/*******************************************************************************
 * Copyright © 2008, 2013 IBM Corporation and others.
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

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.edt.compiler.binding.IBinding;
import org.eclipse.edt.compiler.core.IEGLConstants;
import org.eclipse.edt.compiler.core.ast.AbstractASTVisitor;
import org.eclipse.edt.compiler.core.ast.Part;
import org.eclipse.edt.compiler.core.ast.Service;
import org.eclipse.edt.compiler.internal.EGLBasePlugin;
import org.eclipse.edt.ide.core.internal.compiler.workingcopy.IWorkingCopyCompileRequestor;
import org.eclipse.edt.ide.core.internal.compiler.workingcopy.WorkingCopyCompilationResult;
import org.eclipse.edt.ide.core.internal.compiler.workingcopy.WorkingCopyCompiler;
import org.eclipse.edt.ide.core.model.EGLCore;
import org.eclipse.edt.ide.core.model.EGLModelException;
import org.eclipse.edt.ide.core.model.IEGLElement;
import org.eclipse.edt.ide.core.model.IEGLFile;
import org.eclipse.edt.ide.core.model.IPart;
import org.eclipse.edt.ide.core.model.IWorkingCopy;
import org.eclipse.edt.ide.ui.internal.EGLUI;
import org.eclipse.edt.ide.ui.internal.deployment.Binding;
import org.eclipse.edt.ide.ui.internal.deployment.Bindings;
import org.eclipse.edt.ide.ui.internal.deployment.Deployment;
import org.eclipse.edt.ide.ui.internal.deployment.DeploymentFactory;
import org.eclipse.edt.ide.ui.internal.deployment.EGLDeploymentRoot;
import org.eclipse.edt.ide.ui.internal.deployment.ui.EGLDDRootHelper;
import org.eclipse.jdt.core.dom.IAnnotationBinding;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IWorkbench;


public class BindingEGLConfiguration extends BindingBaseConfiguration {
	private String fAlias=""; //$NON-NLS-1$

	protected int fSelectedCommTypeBtnIndex;
	private EGLDeploymentRoot fDeploymentRoot;
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
			setAlias("");
			
			fProj = servicePart.getEGLProject().getProject();
		}
		
	}
	

	protected void setDefaultAttributes() {
		fSelectedCommTypeBtnIndex=1;
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
	
	//TODO EGL bindings not yet supported
//	public Binding executeAddEGLBinding(Bindings bindings) {
//		Binding eglBinding = DeploymentFactory.eINSTANCE.createBinding();
//		bindings.getBinding().add(eglBinding);
//		eglBinding.setType("edt.binding.egl");//TODO need constant
//		
//		Parameters params = DeploymentFactory.eINSTANCE.createParameters();
//		eglBinding.setParameters(params);
//		eglBinding.setName(getBindingName());
//		EGLDDRootHelper.addOrUpdateParameter(params, "serviceName", getEGLServiceOrInterface());//TODO need constant
//		EGLDDRootHelper.addOrUpdateParameter(params, "alias", getAlias());//TODO need constant
//		
//		return eglBinding;
//	}
	
	public Object executeAddBinding( Bindings abindings ){
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
					Binding existingEGLBinding = EGLDDRootHelper.getBindingByName(fDeploymentRoot, getBindingName());
					if(existingEGLBinding != null)
						bindings.getBinding().remove(existingEGLBinding);				
				}
				//TODO EGL bindings not yet supported.
//				executeAddEGLBinding(bindings);
				
				//persist the file if we're the only client 
				if(!EGLDDRootHelper.isWorkingModelSharedByUserClients(fEGLDDFile))
					EGLDDRootHelper.saveEGLDDFile(fEGLDDFile, fDeploymentRoot);
			}
			finally{
				if(fEGLDDFile != null)
					EGLDDRootHelper.releaseSharedWorkingModel(fEGLDDFile, false);
			}
		}

		return null;
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
	
	protected String getValidBindingName(String bindingName) {
		int length = bindingName.length();
		StringBuilder validName = new StringBuilder(length);
		for (int i = 0; i < length; i++) {
			if (Character.isJavaIdentifierPart(bindingName.charAt(i))) {
				validName.append(bindingName.charAt(i));
			}
		}
		
		if (validName.length() > 0) {
			bindingName = validName.toString();
		}
		
		Binding binding = EGLDDRootHelper.getBindingByName(getEGLDeploymentRoot(), bindingName);
		int incrementIndex = 1;
		while(binding != null) {
			binding = EGLDDRootHelper.getBindingByName(getEGLDeploymentRoot(),bindingName + incrementIndex);
			incrementIndex++;
		}
		
		if(incrementIndex > 1) {
			incrementIndex--;
			bindingName = bindingName + incrementIndex;
		}
		
		return bindingName;
	}
}
