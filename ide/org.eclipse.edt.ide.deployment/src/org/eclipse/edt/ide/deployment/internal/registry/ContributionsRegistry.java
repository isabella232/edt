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

package org.eclipse.edt.ide.deployment.internal.registry;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.Platform;
import org.eclipse.edt.ide.deployment.internal.Logger;
import org.eclipse.edt.ide.deployment.operation.IDeploymentOperation;
import org.eclipse.edt.ide.deployment.solution.IDeploymentSolution;


/**
 *
 */
public class ContributionsRegistry {
	
	private boolean initialized;
	private List solutionsContributions = new ArrayList();
	private HashMap solutionContributionsById = new HashMap();
	private HashMap operationContributions = new HashMap();
	
    /**
     * The singleton to use
     */
    public static final ContributionsRegistry singleton = new ContributionsRegistry();

	/**
	 * 
	 */
	private ContributionsRegistry() {
	}
	
    /**
     * Read the Eclipse extension Registry and find and cache all contributions to the securityEditorContext
     * extension point.
     */
    private void initialize() {
    	this.initialized = true;
    	
		IExtensionRegistry reg = Platform.getExtensionRegistry();
		IConfigurationElement[] conf = reg.getConfigurationElementsFor("org.eclipse.edt.ide.deployment.deploymentSolution"); //$NON-NLS-1$
		IConfigurationElement currentConf;
		  
		for (int i=0; i < conf.length; i++) {
			currentConf = conf[i];
			this.solutionsContributions.add(currentConf);
			this.solutionContributionsById.put(getIdForContribution(currentConf), currentConf);
		}
		
		conf = reg.getConfigurationElementsFor("org.eclipse.edt.ide.deployment.deploymentOperation"); //$NON-NLS-1$
		for (int i=0; i < conf.length; i++) {
			currentConf = conf[i];
			String solutionName = getSolutionNameForContribution(currentConf);
			List operations = (List)this.operationContributions.get( solutionName );
			if ( operations == null ) {
				operations = new ArrayList();
				this.operationContributions.put( solutionName, operations );
			}
			operations.add(currentConf);
		}
    }
    
    public List getContributions() {
    	if (!initialized) {
    		initialize();
    	}
    	return this.solutionsContributions;
    }
  
    public String getIdForContribution(IConfigurationElement contribution) {
    	if (!initialized) {
    		initialize();
    	}
    	return contribution.getAttribute("id"); //$NON-NLS-1$
    }
    
    public String getSolutionNameForContribution(IConfigurationElement contribution) {
    	if (!initialized) {
    		initialize();
    	}
    	return contribution.getAttribute("deploymentSolutionName"); //$NON-NLS-1$
    }
    
    public IConfigurationElement getContributionForId(String id) {
    	if (!initialized) {
    		initialize();
    	}
    	return (IConfigurationElement)this.solutionContributionsById.get(id);
    }
    
    public IDeploymentSolution getDeploymentSolution(IConfigurationElement contribution) {
    	if (!initialized) {
    		initialize();
    	}
    	IDeploymentSolution result = null;
		try {
			result = (IDeploymentSolution)contribution.createExecutableExtension("class"); //$NON-NLS-1$
			List operations = (List)this.operationContributions.get( getIdForContribution( contribution ) );
			if ( operations != null ) {
				for ( int i = 0; i < operations.size(); i ++ ) {
					IConfigurationElement ele = (IConfigurationElement)operations.get( i );
					result.addOperation( (IDeploymentOperation)ele.createExecutableExtension("class") );
				}
			}
		} catch (Exception exc) {
			Logger.logException("Failed to create an instance of IDeploymentSolution", exc); //$NON-NLS-1$
		}
		return result;
    }
}
