/*******************************************************************************
 * Copyright © 2012 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.edt.ide.jtopen.wizards;

import org.eclipse.core.resources.IProject;
import org.eclipse.edt.ide.ui.internal.deployment.Binding;
import org.eclipse.edt.ide.ui.internal.deployment.Bindings;
import org.eclipse.edt.ide.ui.internal.deployment.DeploymentFactory;
import org.eclipse.edt.ide.ui.internal.deployment.EGLDeploymentRoot;
import org.eclipse.edt.ide.ui.internal.deployment.Parameters;
import org.eclipse.edt.ide.ui.internal.deployment.ui.EGLDDRootHelper;
import org.eclipse.edt.ide.ui.wizards.BindingEGLConfiguration;

public class IBMiBindingConnectionConfiguration extends BindingEGLConfiguration {
	
	public static String BINDING_IBMi = "edt.binding.ibmiconnection";
	
	private boolean useUri;
	private String uri;
	
	private String system="";
	private String userId="";
	private String password="";
	private String library="";

	public IBMiBindingConnectionConfiguration() {
		super();
	}
		
	public IBMiBindingConnectionConfiguration(EGLDeploymentRoot root, IProject proj){
		super(root, proj);
	}

	protected void setDefaultAttributes() {
	}

	public String getSystem() {
		return system;
	}

	public void setSystem(String system) {
		this.system = system;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getLibrary() {
		return library;
	}

	public void setLibrary(String library) {
		this.library = library;
	}
	
	public boolean useUri() {
		return this.useUri;
	}
	
	public void setUseUri(boolean useUri) {
		this.useUri = useUri;
	}
	
	public String getUri(){
		return uri;
	}
	
	public void setUri(String uri){
		this.uri = uri;
	}
	
	public Object executeAddBinding(Bindings bindings){
		Binding ibmiBinding = DeploymentFactory.eINSTANCE.createBinding();
		bindings.getBinding().add(ibmiBinding);
		ibmiBinding.setType(BINDING_IBMi);
		String bindingName =  getValidBindingName(getBindingName());
		ibmiBinding.setName(bindingName);
		
		if (useUri()) {
			ibmiBinding.setUseURI(true);
			ibmiBinding.setUri(getUri());
		}
		else {
			ibmiBinding.setUseURI(false);
			Parameters params = DeploymentFactory.eINSTANCE.createParameters();
			ibmiBinding.setParameters(params);
		
			EGLDDRootHelper.addOrUpdateParameter(params, "system", getSystem());
			EGLDDRootHelper.addOrUpdateParameter(params, "userid", getUserId());
			EGLDDRootHelper.addOrUpdateParameter(params, "password", getPassword());
			EGLDDRootHelper.addOrUpdateParameter(params, "library", getLibrary());
		}
		
		return ibmiBinding;
	}
}
