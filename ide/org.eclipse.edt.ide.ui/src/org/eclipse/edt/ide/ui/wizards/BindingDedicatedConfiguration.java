/*******************************************************************************
 * Copyright © 2012, 2013 IBM Corporation and others.
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

import org.eclipse.core.resources.IProject;
import org.eclipse.edt.ide.ui.internal.deployment.Binding;
import org.eclipse.edt.ide.ui.internal.deployment.Bindings;
import org.eclipse.edt.ide.ui.internal.deployment.DeploymentFactory;
import org.eclipse.edt.ide.ui.internal.deployment.EGLDeploymentRoot;

public class BindingDedicatedConfiguration extends BindingEGLConfiguration {

	public BindingDedicatedConfiguration()
	{
		super();
	}
		
	public BindingDedicatedConfiguration(EGLDeploymentRoot root, IProject proj){
		super(root, proj);
	}

	protected void setDefaultAttributes() {
	}
	
	public Object executeAddBinding(Bindings bindings){
		Binding dedicatedBinding = DeploymentFactory.eINSTANCE.createBinding();
		bindings.getBinding().add(dedicatedBinding);
		
		String bindingName =  getValidBindingName(getBindingName());
		dedicatedBinding.setName(bindingName);
		dedicatedBinding.setType(org.eclipse.edt.javart.resources.egldd.Binding.BINDING_SERVICE_DEDICATED);
		
		return dedicatedBinding;
	}
}
