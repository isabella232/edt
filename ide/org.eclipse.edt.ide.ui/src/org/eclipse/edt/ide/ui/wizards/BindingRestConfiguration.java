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
import org.eclipse.edt.ide.ui.internal.deployment.RestBinding;

public class BindingRestConfiguration extends BindingEGLConfiguration {

	private String fBaseUri="";
	private String fSessionCookieId="";
	private boolean fPreserveRequestHeader = false;
		
	public BindingRestConfiguration()
	{
		super();
	}
		
	public BindingRestConfiguration(EGLDeploymentRoot root, IProject proj){
		super(root, proj);
	}

	protected void setDefaultAttributes() {
		fBaseUri = "";
		fSessionCookieId = "";
		fPreserveRequestHeader = false;
	}

	protected int getBindingType() {
		return EGLDDBindingConfiguration.BINDINGTYPE_REST;	
	}
	
	public String getBaseUri(){
		return fBaseUri;
	}
	
	public void setBaseUri(String newBaseUri){
		fBaseUri = newBaseUri;
	}
	
	public String getSessionCookieId(){
		return fSessionCookieId;
	}
	
	public void setSessionCookieId(String newSessionCookieId){
		fSessionCookieId = newSessionCookieId;
	}
	
	public boolean isPreserveRequestHeader(){
		return fPreserveRequestHeader;
	}
	
	public void setPreserveRequestHeader(boolean newVal){
		fPreserveRequestHeader = newVal;
	}
	
	public Object executeAddRestBinding(Bindings bindings){
		RestBinding restBinding = DeploymentFactory.eINSTANCE.createRestBinding();
		bindings.getRestBinding().add(restBinding);
		
		restBinding.setName(getBindingName());
		restBinding.setBaseURI(getBaseUri());
		
		String sessionCookieId = getSessionCookieId();
		if(sessionCookieId != null && sessionCookieId.trim().length()>0)
			restBinding.setSessionCookieId(sessionCookieId);
		restBinding.setPreserveRequestHeaders(isPreserveRequestHeader());
		restBinding.setEnableGeneration(true);
		return restBinding;
	}
}
