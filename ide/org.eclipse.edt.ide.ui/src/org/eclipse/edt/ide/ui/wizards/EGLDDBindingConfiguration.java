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

import org.eclipse.core.resources.IProject;
import org.eclipse.edt.ide.ui.internal.deployment.Bindings;
import org.eclipse.edt.ide.ui.internal.deployment.Deployment;
import org.eclipse.edt.ide.ui.internal.deployment.DeploymentFactory;
import org.eclipse.edt.ide.ui.internal.deployment.EGLDeploymentRoot;
import org.eclipse.jface.operation.IRunnableContext;
import org.eclipse.ui.IWorkbench;


public class EGLDDBindingConfiguration extends EGLPartConfiguration {

	public static final int BINDINGTYPE_WEB = 0;
	public static final int BINDINGTYPE_REST = 1;	
	public static final int BINDINGTYPE_EGL = 2;
	public static final int BINDINGTYPE_NATIVE = 3;
	public static final int BINDINGTYPE_SQL = 4;
	
	private int fBindingType=BINDINGTYPE_REST;

	private BindingEGLConfiguration fBindingEGLConfig;
	private BindingRestConfiguration fBindingRestConfig;
	private BindingSQLDatabaseConfiguration fBindingSQLConfig;
	private IWorkbench fWorkbench;
	private IProject fProj;
	private EGLDeploymentRoot fDeploymentRoot;

	public void init(IWorkbench workbench, IProject proj, EGLDeploymentRoot root) {
		fWorkbench = workbench;
		fProj = proj;
		fDeploymentRoot = root;
	}

	public int getBindingType() {
		return fBindingType;
	}

	public void setBindingType(int bindingType) {
		fBindingType = bindingType;
	}

	public EGLDeploymentRoot getDeploymentRoot(){
		return fDeploymentRoot;
	}

	public BindingEGLConfiguration getBindingEGLConfiguration() {
		if(fBindingEGLConfig == null)
			fBindingEGLConfig = new BindingEGLConfiguration(fDeploymentRoot, fProj);
		return fBindingEGLConfig;
	}
	
	public BindingRestConfiguration getBindingRestConfiguration(){
		if(fBindingRestConfig == null)
			fBindingRestConfig = new BindingRestConfiguration(fDeploymentRoot, fProj);
		return fBindingRestConfig;
	}
	
	public BindingSQLDatabaseConfiguration getBindingSQLConfiguration(){
		if(fBindingSQLConfig == null)
			fBindingSQLConfig = new BindingSQLDatabaseConfiguration(fDeploymentRoot, fProj);
		return fBindingSQLConfig;
	}

	private Bindings getBindings(){
		Deployment deployment = fDeploymentRoot.getDeployment();
		Bindings bindings = deployment.getBindings();
		
		if(bindings == null){
			bindings = DeploymentFactory.eINSTANCE.createBindings();
			deployment.setBindings(bindings);
		}
		return bindings;
	}
	
	public Object executeAddBinding(IRunnableContext runnableContext) throws InvocationTargetException, InterruptedException{
		Bindings bindings = getBindings();
		Object newBinding = null;
		if(fBindingType == BINDINGTYPE_REST){
			newBinding = fBindingRestConfig.executeAddRestBinding(bindings);
		}
		else if(fBindingType == BINDINGTYPE_SQL){
			newBinding = fBindingSQLConfig.executeAddSQLDatabaseBinding(bindings);
		}
		return newBinding;
	}
	
	
}
