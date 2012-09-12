/*******************************************************************************
 * Copyright © 2008, 2012 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.edt.ide.ui.internal.project.features;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.Platform;
import org.eclipse.edt.ide.ui.EDTUIPlugin;
import org.eclipse.edt.ide.ui.internal.EGLLogger;

/**
 * This is the registry for all contributed EGL project features. The registry will cache the contributions and is
 * able to answer questions pertaining to the contributions.
 *
 */
public class EGLProjectFeatureContributionsRegistry {
	/**
	 * <code>true</code> if the registry has been initialized and the contributions
	 * cached
	 */
	private boolean initialized;
	/**
	 * A HashMap whose key = contribution id and value = the IConfigurationElement
	 */
	private HashMap contributions = new HashMap();
	/**
	 * A HashMap whose key = contribution id and value = the IEGLProjectFeature instance
	 */
	private HashMap projectFeatures = new HashMap();
	/**
	 * Convenience list of id attribute of every contributed project feature
	 */
	private List ids = new ArrayList();
	
    /**
     * The singleton to use
     */
    public static final EGLProjectFeatureContributionsRegistry singleton = new EGLProjectFeatureContributionsRegistry();

	/**
	 * Class constructor
	 */
	public EGLProjectFeatureContributionsRegistry() {
	}
	
    /**
     * Read the Eclipse extension Registry and find and cache all contributions to the eglProjectFeature
     * extension point.
     */
    private void initialize() {
    	this.initialized = true;
    	
		IExtensionRegistry reg = Platform.getExtensionRegistry();
		IConfigurationElement[] conf = reg.getConfigurationElementsFor(EDTUIPlugin.PLUGIN_ID + ".eglProjectFeature"); //$NON-NLS-1$
		IConfigurationElement currentConf;
		  
		for (int i=0; i < conf.length; i++) {
			currentConf = conf[i];
			String id = getId(currentConf);
			this.ids.add(id);
			this.contributions.put(id, currentConf);
		}
    }

    
    /**
     * returns the id attribute of the contribution
     * 
     * @param contribution
     * @return
     */
    private String getId(IConfigurationElement contribution) {
    	return contribution.getAttribute("id"); //$NON-NLS-1$
    }
    
    /**
     * Returns a list of all the <codeIEGLProjectFeature</code> instances for each
     * contribution
     * 
     * @return
     */
    public List getAllFeatures() {
    	if (!this.initialized) initialize();
    	List result = new ArrayList();
    	for (Iterator iter = this.ids.iterator(); iter.hasNext();) {
			String id = (String) iter.next();
			result.add(getFeature(id));
		}
    	return result;
    }
    
    /**
     * Returns the IEGLProjectFeature for the passed contribution id
     * 
     * @param id
     * @return
     */
    private IEGLProjectFeature getFeature(String id) {
    	IEGLProjectFeature result = null;
    	if (this.projectFeatures.get(id) == null) {
			try {
				IConfigurationElement contribution = (IConfigurationElement)this.contributions.get(id);
				if (contribution != null) {
					result = (IEGLProjectFeature)contribution.createExecutableExtension("projectFeatureClass"); //$NON-NLS-1$
				}
				this.projectFeatures.put(id, result);
			} catch (Exception e) {
				EGLLogger.log("EGLProjectFeatureContributionsRegistry", e.getMessage()); //$NON-NLS-1$
			}
    	} else {
    		result = (IEGLProjectFeature)this.projectFeatures.get(id);
    	}
		return result;
    }

}
