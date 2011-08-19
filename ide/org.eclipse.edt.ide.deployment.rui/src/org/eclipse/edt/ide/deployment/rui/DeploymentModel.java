/*******************************************************************************
 * Copyright © 2009, 2011 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.edt.ide.deployment.rui;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.Path;
import org.eclipse.edt.ide.deployment.core.model.DeploymentDesc;
import org.eclipse.edt.ide.deployment.core.model.DeploymentTarget;
import org.eclipse.edt.ide.deployment.core.model.RUIApplication;
import org.eclipse.edt.ide.deployment.core.model.Restservice;
import org.eclipse.edt.ide.deployment.core.model.Webservice;

public class DeploymentModel {

	public static class DeploymentSolution{
		private Object solution;
		private String target;
		
		public DeploymentSolution(Object solution, String target) {
			this.solution = solution;
			this.target = target;
		}	
		
		public String getTarget(){
			return target;
		}
		
		public Object getSolution(){
			return solution;
		}
	}
	
	private IProject sourceProject;
	private List serviceSolutions = new ArrayList();
	private List<String> resourceOmissions = new ArrayList<String>();
	private DeploymentSolution ruiSolution;
	private String name;
	private boolean hasWebBindings;
	protected DeploymentDesc dd;
	private boolean isCMDMode = false;
	
	public boolean isCMDMode() {
		return isCMDMode;
	}

	public void setCMDMode(boolean isCMDMode) {
		this.isCMDMode = isCMDMode;
	}

	public String getName() {
		return name;
	}

	public DeploymentModel(IProject project, String name, DeploymentDesc dd) {
		this.sourceProject = project;
		this.name = name;
		this.dd = dd;
	}

	public void addServiceSolution(Object service, String target){
		serviceSolutions.add(new DeploymentSolution(service, target));
	}
	
	public void addServiceSolutions(List solutions){
		serviceSolutions.addAll(solutions);
	}
	
	public void setRUISolution(RUIApplication application, String target){
		setRUISolution(new DeploymentSolution(application, target));
	}

	public void setRUISolution(DeploymentSolution solution){
		ruiSolution = solution;
	}
	
	public DeploymentSolution getRUISolution() {
		return ruiSolution;		
	}
	
	public List getServiceSolutions(){
		return serviceSolutions;
	}
	
	public IProject getSourceProject(){
		return sourceProject;
	}

	
	public void addResourceOmission(String resource)
	{
		resourceOmissions.add(resource);
	}
	
	public void addResourceOmissions(List<String> resourceOmissions)
	{
		resourceOmissions.addAll(resourceOmissions);
	}
	
	public List<String> getResourceOmissions()
	{
		return resourceOmissions;
	}
	
	public DeploymentTarget getDeploymentTarget(){
		return dd.getDeploymentTarget();
	}
	
	public boolean hasWebBindings() {
		return hasWebBindings;
	}

	public void setHasWebBindings(boolean hasWebBindings) {
		this.hasWebBindings = hasWebBindings;
	}

	public boolean hasParts()
	{
		return getServiceSolutions().size() > 0 ||
				(ruiSolution !=  null &&
						ruiSolution.getSolution() != null &&	
						((RUIApplication)ruiSolution.getSolution()).getRUIHandlers().size() > 0) ||
				importHasServices(dd);
	}
	
	private static boolean importHasServices(DeploymentDesc dd){
		for( Iterator<String> itr = dd.getIncludes().iterator(); itr.hasNext(); ){
			String ddName = itr.next();
			try{
				IFile file = ResourcesPlugin.getWorkspace().getRoot().getFile(new Path(ddName));
				DeploymentDesc ddIncl = DeploymentDesc.createDeploymentDescriptor(file.getLocation().toOSString());
				if( ddHasService(ddIncl) ){
					return true;
				}
				else
				{
					if( importHasServices(ddIncl) ){
						return true;
					}
				}
			}
			catch(Exception e){
				//FIXME
				e.printStackTrace();
			}
		}
		return false;
	}
	
	private static boolean ddHasService(DeploymentDesc dd){
		for( Iterator<Restservice> itr = dd.getRestservices().iterator(); itr.hasNext();){
			if( itr.next().isEnableGeneration() ){
				return true;
			}
		}
		for( Iterator<Webservice> itr = dd.getWebservices().iterator(); itr.hasNext();){
			if( itr.next().isEnableGeneration() ){
				return true;
			}
		}
		return false;
	}
}
