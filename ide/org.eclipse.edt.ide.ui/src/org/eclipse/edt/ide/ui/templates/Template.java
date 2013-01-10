/*******************************************************************************
 * Copyright © 2011, 2013 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.edt.ide.ui.templates;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.edt.ide.ui.EDTUIPlugin;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.wizard.IWizardNode;
import org.eclipse.edt.ide.ui.templates.wizards.TemplateWizardNode;

public class Template implements ITemplate {
	protected String id;	
	protected String name;
	protected boolean _default;
	protected String description;
	protected String category;
	protected ImageDescriptor icon;
	protected IWizardNode wizardNode;
	protected String codeTemplateID;

	public String getCodeTemplateId() {
		return codeTemplateID;
	}

	public void setCodeTemplateID(String templateID) {
		this.codeTemplateID = templateID;
	}

	public Template() {
		super();
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	public boolean isDefault() {
		return _default;
	}
	
	public void setIsDefault(boolean b) {
		this._default = b;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public ImageDescriptor getIcon() {
		return icon;
	}

	public void setIcon(ImageDescriptor icon) {
		this.icon = icon;
	}

	public String getDescription() {
		return description;
	}

	public IWizardNode getWizardNode() {
		return wizardNode;
	}

	public boolean hasWizard() {
		return wizardNode != null;
	}

	public void init(IConfigurationElement element) {
		id = element.getAttribute("id");
		name = element.getAttribute("name");
		if (element.getAttribute("default") != null) { 
			_default = Boolean.parseBoolean(element.getAttribute("default"));
		}
		description = element.getAttribute("description");
		category = element.getAttribute("category");
		codeTemplateID = element.getAttribute("codeTemplateID");
		if (element.getAttribute("icon") != null) {
			icon = EDTUIPlugin.imageDescriptorFromPlugin(element.getContributor().getName(), element.getAttribute("icon"));
		}
		if (element.getAttribute("wizardClass") != null) {
			wizardNode = new TemplateWizardNode(this, element);
		}
	}
}
