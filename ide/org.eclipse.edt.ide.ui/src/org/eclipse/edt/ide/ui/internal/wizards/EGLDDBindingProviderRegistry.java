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
package org.eclipse.edt.ide.ui.internal.wizards;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.Platform;
import org.eclipse.edt.ide.core.Logger;
import org.eclipse.edt.ide.ui.wizards.BindingEGLConfiguration;
import org.eclipse.ui.forms.IDetailsPage;

public class EGLDDBindingProviderRegistry {
	
	private boolean initialized;
	private List contributions = new ArrayList();
	private int counter = 0;
	EGLDDBindingWizardProvider[] providers;
	
    /**
     * The singleton to use
     */
    public static final EGLDDBindingProviderRegistry singleton = new EGLDDBindingProviderRegistry();

	/**
	 * 
	 */
	private EGLDDBindingProviderRegistry() {
	}
	
	public void finish() {
		counter = 0;
		providers = null;
	}
	
    /**
     * Read the Eclipse extension Registry and find and cache all contributions to the securityEditorContext
     * extension point.
     */
    private void initialize() {
    	this.initialized = true;
    	
		IExtensionRegistry reg = Platform.getExtensionRegistry();
		IConfigurationElement[] conf = reg.getConfigurationElementsFor("org.eclipse.edt.ide.ui.eglDDEditorBindingProvider"); //$NON-NLS-1$
		IConfigurationElement currentConf;
		  
		for (int i=0; i < conf.length; i++) {
			currentConf = conf[i];
			this.contributions.add(currentConf);
		}
    }
    
    public List getContributions() {
    	if (!initialized) {
    		initialize();
    	}
    	return this.contributions;
    }
  
    public String getIdForContribution(IConfigurationElement contribution) {
    	if (!initialized) {
    		initialize();
    	}
    	return contribution.getAttribute("id"); //$NON-NLS-1$
    }
    
    public String getDescriptionForContribution(IConfigurationElement contribution) {
    	if (!initialized) {
    		initialize();
    	}
    	return contribution.getAttribute("description"); //$NON-NLS-1$
    }
    
    public String getNameForContribution(IConfigurationElement contribution) {
    	if (!initialized) {
    		initialize();
    	}
    	return contribution.getAttribute("name"); //$NON-NLS-1$
    }
    
    public EGLDDBindingWizardProvider[] getEGLDDBindingWizardProviders() {
    	if (!initialized) {
    		initialize();
    	}
    	if ( this.providers == null ) {
	    	EGLDDBindingWizardProvider[] providers = new EGLDDBindingWizardProvider[contributions.size()];
			try {
				for ( int i = 0; i < contributions.size(); i ++ ) {
					IConfigurationElement contribution = (IConfigurationElement)contributions.get(i);
					providers[i] = new EGLDDBindingWizardProvider();
					providers[i].id = getIdForContribution( contribution );
					providers[i].description = getDescriptionForContribution( contribution );
					providers[i].bindingId = counter ++;
					providers[i].bindingconfigurationClass = (BindingEGLConfiguration)contribution.createExecutableExtension("bindingconfigurationClass");
					IConfigurationElement[] children = contribution.getChildren();
					EGLDDBindingWizardPages[] eglDDBindingWizardPages = new EGLDDBindingWizardPages[children.length];
					for ( int j = 0; j < children.length; j ++ ) {
						eglDDBindingWizardPages[j] = new EGLDDBindingWizardPages();
						eglDDBindingWizardPages[j].name = getNameForContribution( children[j] );
						eglDDBindingWizardPages[j].wizardpage = (EGLDDBindingWizardPage)children[j].createExecutableExtension("class");
					}
					providers[i].eglDDBindingWizardPages = eglDDBindingWizardPages;
				}
			} catch (Exception exc) {
				Logger.log(exc, "Failed to create an instance of IDeploymentSolution"); //$NON-NLS-1$
			}
			this.providers = providers; 
		}
		return this.providers;
    }
    
    public IDetailsPage getDetailsPageFor( String type ) {
    	if (!initialized) {
    		initialize();
    	}
    	
		try {
			for ( int i = 0; i < contributions.size(); i ++ ) {
				IConfigurationElement contribution = (IConfigurationElement)contributions.get(i);
				String id = getIdForContribution( contribution );
				if ( id.equals( type.trim() ) ) {
					return (IDetailsPage)contribution.createExecutableExtension("bindingdetailsPageClass");
				}
			
			}
		} catch (Exception exc) {
			Logger.log(exc, "Failed to create an instance of bindingdetailsPageClass"); //$NON-NLS-1$
		}
		return null;
    }

}
