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

import org.eclipse.edt.ide.ui.internal.deployment.Deployment;
import org.eclipse.edt.ide.ui.internal.deployment.DeploymentFactory;
import org.eclipse.edt.ide.ui.internal.deployment.EGLDeploymentRoot;
import org.eclipse.edt.ide.ui.internal.deployment.Protocol;
import org.eclipse.edt.ide.ui.internal.deployment.Protocols;
import org.eclipse.edt.ide.ui.internal.deployment.ui.CommTypes;
import org.eclipse.edt.ide.ui.internal.deployment.ui.EGLDDRootHelper;

public class EGLDDProtocolConfiguration extends EGLPartConfiguration {

	private EGLDeploymentRoot fDeploymentRoot;
	private String fName="";	 //$NON-NLS-1$
	private CommTypes fCommType;
	private Protocol[] fNewProtocol;
	
	public void init(EGLDeploymentRoot root){
		fDeploymentRoot = root;
		fCommType = CommTypes.LOCAL_LITERAL;
		initProtocols(null);
	}
	
	public void init(EGLDeploymentRoot root, Protocol initProtocol ){
		fDeploymentRoot = root;
		fCommType = EGLDDRootHelper.getProtocolCommType(initProtocol);
		fName = initProtocol.getName();
		initProtocols(initProtocol);
	}

	private void initProtocols(Protocol initProtocol) {
		List commtypeList = CommTypes.VALUES;
		fNewProtocol = new Protocol[commtypeList.size()];
		
		int i=0;
		for(Iterator it=commtypeList.iterator();it.hasNext(); i++){
			CommTypes commtype = (CommTypes)it.next();
			if( initProtocol != null && (EGLDDRootHelper.getProtocolCommType(initProtocol) == commtype) )
			{
				fNewProtocol[i] = initProtocol;
			}
			else
			{
				fNewProtocol[i] = EGLDDRootHelper.createNewProtocol(commtype);
			}
		}
	}
	
	public Protocol getProtocol(CommTypes commtype){
		return fNewProtocol[commtype.getValue()];
	}

	public String getName() {
		return fName;
	}

	public void setName(String name) {
		fName = name;
	}
	
	public EGLDeploymentRoot getDeploymentRoot(){
		return fDeploymentRoot;
	}
	
	public Protocol executeAddProtocol(){
		Deployment deployment = fDeploymentRoot.getDeployment();
		Protocols protocols = deployment.getProtocols();
		if(protocols == null){
			protocols = DeploymentFactory.eINSTANCE.createProtocols();
			deployment.setProtocols(protocols);
		}
		Protocol addedProtocol = fNewProtocol[fCommType.getValue()];
		addedProtocol.setName(fName);
		EGLDDRootHelper.setProtocolOnProtocolGroup(protocols.getProtocolGroup(), addedProtocol);
		return addedProtocol;
	}

	public CommTypes getCommType() {
		return fCommType;
	}

	public void setCommType(CommTypes commType) {
		fCommType = commType;
	}
}
