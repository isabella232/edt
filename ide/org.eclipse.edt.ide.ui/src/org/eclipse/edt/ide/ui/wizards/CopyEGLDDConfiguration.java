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

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.runtime.Path;
import org.eclipse.edt.ide.ui.internal.deployment.Bindings;
import org.eclipse.edt.ide.ui.internal.deployment.Deployment;
import org.eclipse.edt.ide.ui.internal.deployment.DeploymentFactory;
import org.eclipse.edt.ide.ui.internal.deployment.DeploymentPackage;
import org.eclipse.edt.ide.ui.internal.deployment.EGLBinding;
import org.eclipse.edt.ide.ui.internal.deployment.EGLDeploymentRoot;
import org.eclipse.edt.ide.ui.internal.deployment.NativeBinding;
import org.eclipse.edt.ide.ui.internal.deployment.Protocol;
import org.eclipse.edt.ide.ui.internal.deployment.Protocols;
import org.eclipse.edt.ide.ui.internal.deployment.WebBinding;
import org.eclipse.edt.ide.ui.internal.deployment.Webservice;
import org.eclipse.edt.ide.ui.internal.deployment.Webservices;
import org.eclipse.edt.ide.ui.internal.deployment.ui.EGLDDRootHelper;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.Viewer;

public class CopyEGLDDConfiguration extends EGLPartConfiguration {

	private EGLDeploymentRoot fCurrRoot;
	private IProject fCurrProject;
	private Object[] fSelectedElement;
	private IFile fCurrEGLDDFile;

	public static class EGLDeploymentTreeContentProvider implements ITreeContentProvider{

		public Object[] getChildren(Object parentElement) {
			List children = new ArrayList();
			if(parentElement instanceof EGLDeploymentRoot){
				EGLDeploymentRoot root = (EGLDeploymentRoot)parentElement;
				Deployment deployment = root.getDeployment();
				Bindings bindings = deployment.getBindings();
				if(bindings != null)
					children.add(bindings);
				Protocols protocols = deployment.getProtocols();
				if(protocols != null)
					children.add(protocols);
				Webservices wss = deployment.getWebservices();
				if(wss != null)
					children.add(wss);
			}
			else if(parentElement instanceof Bindings){
				Bindings bindings = ((Bindings)parentElement);
				children.addAll(bindings.getEglBinding());
				children.addAll(bindings.getNativeBinding());
				children.addAll(bindings.getWebBinding());
			}
			else if(parentElement instanceof Protocols)
				children.addAll(((Protocols)parentElement).getProtocol());
			else if(parentElement instanceof Webservices)
				children.addAll(((Webservices)parentElement).getWebservice());
			return children.toArray();
		}

		public Object getParent(Object element) {
			// TODO Auto-generated method stub
			return null;
		}

		public boolean hasChildren(Object element) {
			return getChildren(element).length>0;
		}

		public Object[] getElements(Object inputElement) {
			return getChildren(inputElement);
		}

		public void dispose() {
			// TODO Auto-generated method stub
			
		}

		public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
			if(newInput != oldInput)
				viewer.refresh();
		}
		
	}
	
	public static class EGLDeploymentTreeLabelProvider extends LabelProvider{		
		public String getText(Object element) {
			DeploymentPackage pkg = DeploymentPackage.eINSTANCE;
			if(element instanceof Bindings)
				return pkg.getBindings().getName();
			else if(element instanceof Protocols)
				return pkg.getProtocols().getName();
			else if(element instanceof Webservices)
				return pkg.getWebservices().getName();
			else if(element instanceof EGLBinding)
				return ((EGLBinding)element).getName();
			else if(element instanceof NativeBinding)
				return ((NativeBinding)element).getName();
			else if(element instanceof WebBinding)
				return ((WebBinding)element).getName();
			else if(element instanceof Protocol)
				return ((Protocol)element).getName();
			else if(element instanceof Webservice)
				return ((Webservice)element).getImplementation();
			return "";			 //$NON-NLS-1$
		}
	}
	
	public void init(EGLDeploymentRoot root, IFile currEGLDDFile, IProject proj){
		fCurrRoot = root;
		fCurrProject = proj;
		fCurrEGLDDFile = currEGLDDFile;
		fSelectedElement = new Object[]{};
	}
	
	public IProject getCurrentProject(){
		return fCurrProject;
	}
	
	public IFile getCurrentEGLDDFile(){
		return fCurrEGLDDFile;
	}
		
	public IFile getCopyFromEGLDDFile(String filePath){
		IWorkspaceRoot workspaceRoot = fCurrProject.getWorkspace().getRoot();
		IFile file = workspaceRoot.getFile(new Path(filePath));
		return file;
	}

	public Object getSameNameNodeInCurrentEGLDD(Object element, String currLabel) {
		if(element instanceof EGLBinding)
			return EGLDDRootHelper.getEGLBindingByName(fCurrRoot, currLabel);
		else if(element instanceof NativeBinding)
			return EGLDDRootHelper.getNativeBindingByName(fCurrRoot, currLabel);
		else if(element instanceof WebBinding)
			return EGLDDRootHelper.getWebBindingByName(fCurrRoot, currLabel);
		else if(element instanceof Protocol)
			return EGLDDRootHelper.getProtocolByName(fCurrRoot, currLabel);
		else if(element instanceof Webservice)
			return EGLDDRootHelper.getWebserviceByImpl(fCurrRoot, currLabel);
				
		return null;
	}

	public void updateSelectedElements(Object[] checkedElements) {
		fSelectedElement = checkedElements;		
	}
	
	public void executeCopy(){
		Deployment deployment = fCurrRoot.getDeployment();
		DeploymentFactory factory = DeploymentFactory.eINSTANCE;
		for(int i=0; i<fSelectedElement.length; i++){
			EObject eobj = (EObject)fSelectedElement[i];
			if(eobj instanceof EGLBinding || eobj instanceof WebBinding || eobj instanceof NativeBinding){
				Bindings bindings = deployment.getBindings();
				if(bindings == null){
					bindings = factory.createBindings();
					deployment.setBindings(bindings);
				}
				if(eobj instanceof EGLBinding){
					EList list = bindings.getEglBinding();					
					removeSameNameNode(eobj, ((EGLBinding)eobj).getName(), list);
										
					list.add(EcoreUtil.copy(eobj));
				}
				else if(eobj instanceof NativeBinding){
					EList list = bindings.getNativeBinding();
					removeSameNameNode(eobj, ((NativeBinding)eobj).getName(), list);
					
					list.add(EcoreUtil.copy(eobj));
				}
				else if(eobj instanceof WebBinding){
					EList list = bindings.getWebBinding();
					removeSameNameNode(eobj, ((WebBinding)eobj).getName(), list);
					list.add(EcoreUtil.copy(eobj));
				}
			}
			else if(eobj instanceof Protocol){
				Protocols protocols = deployment.getProtocols();
				if(protocols == null){
					protocols = factory.createProtocols();
					deployment.setProtocols(protocols);
				}
				EList list = protocols.getProtocol();
				removeSameNameNode(eobj, ((Protocol)eobj).getName(), list);
				EGLDDRootHelper.setProtocolOnProtocolGroup(protocols.getProtocolGroup(), (Protocol)EcoreUtil.copy(eobj));					
			}
			else if(eobj instanceof Webservice){
				Webservices wss = deployment.getWebservices();
				if(wss == null){
					wss = factory.createWebservices();
					deployment.setWebservices(wss);
				}
				EList list = wss.getWebservice();
				removeSameNameNode(eobj, ((Webservice)eobj).getImplementation(), list);
				list.add(EcoreUtil.copy(eobj));
			}
		}
	}

	private void removeSameNameNode(Object obj, String nameLabel, EList list) {
		Object sameNameNode = getSameNameNodeInCurrentEGLDD(obj, nameLabel);
		if(sameNameNode != null)
			list.remove(sameNameNode);		//override it
	}
}
