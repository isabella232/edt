/*******************************************************************************
 * Copyright © 2000, 2011 IBM Corporation and others.
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
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import org.eclipse.edt.ide.core.internal.search.PartDeclarationInfo;
import org.eclipse.edt.ide.ui.internal.deployment.Deployment;
import org.eclipse.edt.ide.ui.internal.deployment.DeploymentFactory;
import org.eclipse.edt.ide.ui.internal.deployment.EGLDeploymentRoot;
import org.eclipse.edt.ide.ui.internal.deployment.Restservice;
import org.eclipse.edt.ide.ui.internal.deployment.Restservices;
import org.eclipse.edt.ide.ui.internal.deployment.StyleTypes;
import org.eclipse.edt.ide.ui.internal.deployment.Webservice;
import org.eclipse.edt.ide.ui.internal.deployment.Webservices;
import org.eclipse.emf.common.util.EList;

public class WebServicesConfiguration extends EGLPartConfiguration {

	/**
	 * list of PartDeclarationInfo 
	 * a list of services can be generated as web services
	 */
	private List fServicesNeed2BeWS;
	
	/**
	 * list of PartDeclarationInfo 
	 * a list of services user selected to be be generated as web services
	 */
	private List fSelectedServices2BeWS;
	
	private EGLDeploymentRoot fDeploymentRoot;
	
	/**
	 * a set of the web services implemenation name in the egldd already
	 */
	private HashSet fWSSet;

	private int fInitialAvailableServicesCnt;
	
	private boolean bGenAsRest = true;
	private boolean bGenAsSOAP = false;
	
	public void init(EGLDeploymentRoot deploymentRoot, List allEGLServicesList){
		fDeploymentRoot = deploymentRoot;
		initWebServicesSet(deploymentRoot);
		calculateServicesNeed2BeWS(deploymentRoot, allEGLServicesList);
		
		bGenAsRest = true;
		bGenAsSOAP = false; 
		fInitialAvailableServicesCnt = getServicesNeed2BeWS().size();
	}
	
	public int getInitialAvailableServicesCount(){
		return fInitialAvailableServicesCnt;
	}
	
	private void initWebServicesSet(EGLDeploymentRoot deploymentRoot) {
		if(fWSSet == null) 
			fWSSet = new HashSet();
		
		Deployment deployment = deploymentRoot.getDeployment();
		Webservices wss = deployment.getWebservices();
		if(wss != null){
			EList wsList = wss.getWebservice();
			for(Iterator it = wsList.iterator(); it.hasNext();){
				Webservice ws = (Webservice)it.next();
				fWSSet.add(ws.getImplementation());
			}
		}
		Restservices rss = deployment.getRestservices();
		if(rss != null){
			EList rsList = rss.getRestservice();
			for(Iterator it = rsList.iterator(); it.hasNext();){
				Restservice rs = (Restservice)it.next();
				fWSSet.add(rs.getImplementation());
			}
		}
	}

	private void calculateServicesNeed2BeWS(EGLDeploymentRoot root, List allEGLServicesList){
		fServicesNeed2BeWS = getServicesNeed2BeWS();
		
		for(Iterator it = allEGLServicesList.iterator(); it.hasNext();){
			PartDeclarationInfo partInfo = (PartDeclarationInfo)it.next();
			String serviceFQName = partInfo.getFullyQualifiedName();
			//check to see if this service implementation already existed egldd
			//if so, no need to add it again
			if(!fWSSet.contains(serviceFQName)){
				fServicesNeed2BeWS.add(partInfo);
			}			
		}		
	}
	
	public List getServicesNeed2BeWS(){
		if(fServicesNeed2BeWS == null)
			fServicesNeed2BeWS = new ArrayList();
		return fServicesNeed2BeWS;
	}
	
	public List getSelectedServices2BeWS(){
		if(fSelectedServices2BeWS == null)
			fSelectedServices2BeWS = new ArrayList();
		return fSelectedServices2BeWS;
	}
	
	public void moveServices2BeWS(List sels){
		getSelectedServices2BeWS().addAll(sels);
		getServicesNeed2BeWS().removeAll(sels);
	}
	
	public void moveAllServices2BeWS(){
		getSelectedServices2BeWS().addAll(getServicesNeed2BeWS());
		getServicesNeed2BeWS().clear();
	}
	
	public void moveServicesNot2BeWS(List sels){
		getServicesNeed2BeWS().addAll(sels);
		getSelectedServices2BeWS().removeAll(sels);
	}
	
	public void moveAllServicesNot2BeWS(){
		getServicesNeed2BeWS().addAll(getSelectedServices2BeWS());
		getSelectedServices2BeWS().clear();
	}
	
	public boolean isGenAsSOAP(){
		return bGenAsSOAP;		
	}
	
	public void setGenAsSOAP(boolean newVal){
		bGenAsSOAP = newVal;
	}
	
	public boolean isGenAsRest(){
		return bGenAsRest;
	}
	
	public void setGenAsRest(boolean newVal){
		bGenAsRest = newVal;
	}
	
	public String executeAddWebServicesOperation(){
		Deployment deployment = fDeploymentRoot.getDeployment();
		DeploymentFactory factory = DeploymentFactory.eINSTANCE;
		Webservices wss = deployment.getWebservices();
		if(wss == null){
			wss = factory.createWebservices();
			deployment.setWebservices(wss);
		}
		Restservices rss = deployment.getRestservices();
		if(rss == null){
			rss = factory.createRestservices();
			deployment.setRestservices(rss);
		}
		
		List sel = getSelectedServices2BeWS();
		Webservice newWS = null;
		Restservice newRS = null;		
		for(Iterator it=sel.iterator(); it.hasNext();){
			PartDeclarationInfo partinfo = (PartDeclarationInfo)it.next();
			char parttype = partinfo.getPartType();
			String fqImpl = partinfo.getFullyQualifiedName();
			{
				newWS = factory.createWebservice();
				newWS.setImplementation(fqImpl);
				newWS.setStyle(StyleTypes.DOCUMENT_WRAPPED);
				newWS.setEnableGeneration(isGenAsSOAP());
				newWS.setImplType(parttype);
				wss.getWebservice().add(newWS);
			}
			
			{
				newRS = factory.createRestservice();
				newRS.setImplementation(fqImpl);
				newRS.setUri(partinfo.getPartName());
				newRS.setEnableGeneration(isGenAsRest());
				newRS.setImplType(parttype);
				rss.getRestservice().add(newRS);
			}
		}				
		 
		return (newWS==null)? newRS.getImplementation() : newWS.getImplementation();
	}
}
