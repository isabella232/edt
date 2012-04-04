/*******************************************************************************
 * Copyright © 2011, 2012 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.edt.ide.widgetLibProvider;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.edt.ide.rui.internal.project.IWidgetLibraryConflict;
import org.eclipse.edt.ide.rui.internal.project.IWidgetLibraryImporter;
import org.eclipse.jface.resource.ImageDescriptor;

public interface IWidgetLibProvider {
	public static final String PROVIDER = "provider"; //$NON-NLS-1$
	public static final String PROVIDER_ID = "id"; //$NON-NLS-1$
	public static final String PROVIDER_LIBRARY_NAME = "libname"; //$NON-NLS-1$
	public static final String PROVIDER_PROVIDER = "provider"; //$NON-NLS-1$
	public static final String PROVIDER_VERSION = "version"; //$NON-NLS-1$
	public static final String PROVIDER_VERSION_DESC = "version_desc"; //$NON-NLS-1$
	public static final String PROVIDER_LOGO = "logo"; //$NON-NLS-1$
	public static final String PROVIDER_DETAIL = "detail"; //$NON-NLS-1$
	public static final String PROVIDER_SELECTED = "selected"; //$NON-NLS-1$
	public static final String PROVIDER_RESOURCE_PLUGIN_NAME = "resourcePluginName"; //$NON-NLS-1$
	public static final String PROVIDER_RESOURCE_FOLDER = "resourceFolder"; //$NON-NLS-1$
	public static final String PROVIDER_PROJECT_NAME = "projectName"; //$NON-NLS-1$
	public static final String PROVIDER_IMPORT_CLASS = "importClass"; //$NON-NLS-1$	
	public static final String PROVIDER_CONFLICT_CLASS = "conflictClass"; //$NON-NLS-1$	
	
	public String getId();
	/**
	 * Returns the display name of the library.
	 *  
	 * @return
	 */
	public String getLibName();
	
	public String getProvider();
	
	public String getVersion();
//	public String getVersionDesc();
	
	/**
	 * Returns the full version number including the version description.
	 *  
	 * @return
	 */
	public String getFullVersion();
	
	
	/**
	 * Returns a small icon representing the template. 
	 * 
	 * @return
	 */
	public ImageDescriptor getLogo();
	
	public String getDetail();
	
	/**
	 * Indicates whether the project is selected by default
	 * 
	 * @return
	 */
	public boolean isSelected();
	
	
	/**
	 * Returns the path of the widget library located resource plugin.
	 * 
	 * @return
	 */
	public String getResourcePluginName();
	
	/**
	 * Returns the path of the widget library located folder of the resource plugin.
	 * 
	 * @return
	 */
	public String getResourceFolder();
	
	/**
	 * Returns the project name of the widget library.
	 * 
	 * @return
	 */
	public String getProjectName();	
	
	
	public IWidgetLibraryImporter getImporter();
	
	public IWidgetLibraryConflict getConflictClass();
	
	/**
	 * Initializes the template using the supplied IConfigurationElement. 
	 * 
	 * (Templates are typically contributed via an extension point)
	 * 
	 * @param configElement
	 */
	public void init(IConfigurationElement configElement);
}
