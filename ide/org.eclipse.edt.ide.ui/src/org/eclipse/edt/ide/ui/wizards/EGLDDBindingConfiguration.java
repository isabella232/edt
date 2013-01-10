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

import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.resources.IProject;
import org.eclipse.edt.ide.ui.internal.deployment.Bindings;
import org.eclipse.edt.ide.ui.internal.deployment.Deployment;
import org.eclipse.edt.ide.ui.internal.deployment.DeploymentFactory;
import org.eclipse.edt.ide.ui.internal.deployment.EGLDeploymentRoot;
import org.eclipse.edt.ide.ui.internal.wizards.EGLDDBindingProviderRegistry;
import org.eclipse.edt.ide.ui.internal.wizards.EGLDDBindingWizardProvider;
import org.eclipse.jface.operation.IRunnableContext;
import org.eclipse.ui.IWorkbench;


public class EGLDDBindingConfiguration extends EGLPartConfiguration {

	private int fBindingType; //=BINDINGTYPE_REST;

	private BindingEGLConfiguration fBindingEGLConfig;
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
	
	public BindingBaseConfiguration getConfiguration(String providerId){
		EGLDDBindingWizardProvider[] providers = EGLDDBindingProviderRegistry.singleton.getEGLDDBindingWizardProviders();
		for ( int i = 0; i < providers.length; i ++ ) {
			if ( providers[i].id.equals( providerId ) ) {
				return providers[i].bindingconfigurationClass;
			}
		}
		
		return null;
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
	
	public Object executeAddBinding(IRunnableContext runnableContext) throws InvocationTargetException, InterruptedException {
		Bindings bindings = getBindings();
		Object newBinding = null;
		
		EGLDDBindingWizardProvider[] fproviders = EGLDDBindingProviderRegistry.singleton.getEGLDDBindingWizardProviders();
		for ( int i = 0; i < fproviders.length; i ++ ) {
			if ( fproviders[i].bindingId == fBindingType ) {
				newBinding = fproviders[i].bindingconfigurationClass.executeAddBinding( bindings );
				break;
			}
		}

		return newBinding;
	}
	
	
}
