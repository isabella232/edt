/*******************************************************************************
 * Copyright © 2006, 2012 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.edt.ide.deployment.core.model;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.edt.ide.deployment.core.DeploymentDescParser;
import org.eclipse.edt.javart.resources.egldd.RuntimeDeploymentDesc;




public class DeploymentDesc extends RuntimeDeploymentDesc{

	private List<Service> services;
	
	private RUIApplication ruiApplication;
	
	private ArrayList<String> eglParts;
	
	private List<String> resourceOmissions;

	private DeploymentTarget target;
	
	private String eglddFileName;
	

	public static DeploymentDesc createDeploymentDescriptor(String filepath) throws Exception
	{
		DeploymentDesc desc = new DeploymentDesc();
		desc.setName(getNameFromFilePath(filepath));
		DeploymentDescParser parser = new DeploymentDescParser();
		parser.parse(desc, filepath);	
		return desc;
	}
	
	public static DeploymentDesc createDeploymentDescriptor(String name, InputStream is) throws Exception
	{
		DeploymentDesc desc = new DeploymentDesc();
		desc.setName(name);
		DeploymentDescParser parser = new DeploymentDescParser();
		parser.parse(desc, is);	
		return desc;
	}
	

	private DeploymentDesc()
	{
		services = new ArrayList<Service>();
		resourceOmissions = new ArrayList<String>();
	}
	
	public List<Restservice> getRestservices() {
		List<Restservice> restServices = new ArrayList<Restservice>();
		for(Service service : services){
			if(service instanceof Restservice){
				restServices.add((Restservice)service);
			}
		}
		return restServices;
	}
	
	public void setName( String name ) {
		eglddFileName = name;
		super.setName( name );
	}
	
	public String getEGLDDFileName() {
		return eglddFileName;
	}

	public RUIApplication getRUIApplication() {
		return ruiApplication;
	}

	public void setRUIApplication(RUIApplication application) {
		this.ruiApplication = application;
	}
	
	public void setTarget(DeploymentTarget target)
	{
		this.target = target;
	}
	
	public DeploymentTarget getDeploymentTarget()
	{
		return target;
	}
	
	public void addService(Service service)
	{
		if(Service.SERVICE_REST.equalsIgnoreCase(service.getType())){
			services.add(new Restservice(service));
		}
	}
	
	public void removeResourceOmission(String resource)
	{
		this.resourceOmissions.remove(resource);
	}

	public List<String> getResourceOmissions()
	{
		return resourceOmissions;
	}
	
	public void addResourceOmission(String resource)
	{
		resourceOmissions.add(resource);
	}
	
	public List<Service> getServices() {
		return services;
	}
	
	public ArrayList<String> getEGLServiceParts()
	{
		if (eglParts == null)
		{
			eglParts = new ArrayList<String>();
			for(Service ws : getServices())
			{
				eglParts.add(ws.getImplementation());
			}			
		}
		
		return eglParts;
	}
	
}
