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

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.edt.ide.rui.internal.project.IWidgetLibraryConflict;
import org.eclipse.edt.ide.rui.internal.project.IWidgetLibraryImporter;
import org.eclipse.edt.ide.ui.EDTUIPlugin;
import org.eclipse.jface.resource.ImageDescriptor;

public class WidgetLibProvider implements IWidgetLibProvider {
	protected String id;
	protected String libName;
	protected String provider;
	protected String version;
	protected String version_desc;
	protected ImageDescriptor logo;
	protected String detail;
	protected boolean selected;
	protected String resourcePluginName;
	protected String resourceFolder;
	protected String projectName;	
	protected IWidgetLibraryImporter importer;
	protected IWidgetLibraryConflict conflictClass;

	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getLibName() {
		return libName;
	}

	public void setLibName(String libName) {
		this.libName = libName;
	}

	public String getProvider() {
		return provider;
	}

	public void setProvider(String provider) {
		this.provider = provider;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public String getVersion_desc() {
		return version_desc;
	}

	public void setVersion_desc(String version_desc) {
		this.version_desc = version_desc;
	}

	public String getFullVersion() {
		if(this.version_desc != null && !this.version_desc.isEmpty())
			return this.version + " (" + this.version_desc + ")";
		else
			return this.version;
	}

	public ImageDescriptor getLogo() {
		return logo;
	}

	public void setLogo(ImageDescriptor logo) {
		this.logo = logo;
	}

	public String getDetail() {
		return detail;
	}

	public void setDetail(String detail) {
		this.detail = detail;
	}

	public boolean isSelected() {
		return selected;
	}

	public void setSelected(boolean selected) {
		this.selected = selected;
	}

	public String getResourcePluginName() {
		return resourcePluginName;
	}

	public void setResourcePluginName(String resourcePluginName) {
		this.resourcePluginName = resourcePluginName;
	}

	public String getResourceFolder() {
		return resourceFolder;
	}

	public void setResourceFolder(String resourceFolder) {
		this.resourceFolder = resourceFolder;
	}

	public String getProjectName() {
		return projectName;
	}

	public void setProjectName(String projectName) {
		this.projectName = projectName;
	}

	public IWidgetLibraryImporter getImporter() {
		return importer;
	}

	public void setImporter(IWidgetLibraryImporter importer) {
		this.importer = importer;
	}
	
	public IWidgetLibraryConflict getConflictClass() {
		return conflictClass;
	}	
	
	public void setConflictClass(IWidgetLibraryConflict conflict) {
		conflictClass = conflict;
	}	

	
	public void init(IConfigurationElement element) {
		id = element.getAttribute(PROVIDER_ID);
		libName = element.getAttribute(PROVIDER_LIBRARY_NAME);
		provider = element.getAttribute(PROVIDER_PROVIDER);
		version = element.getAttribute(PROVIDER_VERSION);
		version_desc = element.getAttribute(PROVIDER_VERSION_DESC);
		if (element.getAttribute(PROVIDER_LOGO) != null && !element.getAttribute(PROVIDER_LOGO).equals("")) {
			logo = EDTUIPlugin.imageDescriptorFromPlugin(element.getContributor().getName(), element.getAttribute(PROVIDER_LOGO));
		}
		detail = element.getAttribute(PROVIDER_DETAIL);
		if (element.getAttribute(PROVIDER_SELECTED) != null) { 
			selected = Boolean.parseBoolean(element.getAttribute(PROVIDER_SELECTED));
		}		
		resourcePluginName = element.getAttribute(PROVIDER_RESOURCE_PLUGIN_NAME);
		resourceFolder = element.getAttribute(PROVIDER_RESOURCE_FOLDER);
		projectName = element.getAttribute(PROVIDER_PROJECT_NAME);		
		try {
			importer = (IWidgetLibraryImporter)element.createExecutableExtension( PROVIDER_IMPORT_CLASS );
			if(element.getAttribute(PROVIDER_CONFLICT_CLASS) != null){
				conflictClass = (IWidgetLibraryConflict)element.createExecutableExtension( PROVIDER_CONFLICT_CLASS );
			}
		} catch (CoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	
}
