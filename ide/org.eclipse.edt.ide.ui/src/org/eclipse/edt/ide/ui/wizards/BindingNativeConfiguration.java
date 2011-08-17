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

import org.eclipse.core.resources.IProject;
import org.eclipse.edt.ide.ui.internal.deployment.Bindings;
import org.eclipse.edt.ide.ui.internal.deployment.DeploymentFactory;
import org.eclipse.edt.ide.ui.internal.deployment.EGLDeploymentRoot;
import org.eclipse.edt.ide.ui.internal.deployment.NativeBinding;
import org.eclipse.edt.ide.ui.internal.deployment.ui.EGLDDRootHelper;
import org.eclipse.emf.ecore.util.FeatureMap;

public class BindingNativeConfiguration extends BindingEGLConfiguration {

	public BindingNativeConfiguration(EGLDeploymentRoot root, IProject proj){
		super(root, proj);
	}
	
	protected int getBindingType() {
		return EGLDDBindingConfiguration.BINDINGTYPE_NATIVE;	
	}

	public Object executeAddNativeBinding(Bindings bindings) {
		NativeBinding nativeBinding = DeploymentFactory.eINSTANCE.createNativeBinding();
		bindings.getNativeBinding().add(nativeBinding);
		
		nativeBinding.setName(getBindingName());
		
		FeatureMap protocolGrp = nativeBinding.getProtocolGroup();
		EGLDDRootHelper.setProtocolOnProtocolGroup(protocolGrp, fNewProtocol[fSelectedCommTypeBtnIndex]);
		return nativeBinding;
	}
}
