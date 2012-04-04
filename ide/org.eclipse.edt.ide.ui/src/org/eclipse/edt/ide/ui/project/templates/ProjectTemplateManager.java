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
package org.eclipse.edt.ide.ui.project.templates;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.Platform;

public class ProjectTemplateManager {
	public static final String EXTENSIONPOINT_RECORD_TEMPLATES = org.eclipse.edt.ide.ui.EDTUIPlugin.PLUGIN_ID + ".projectTemplates"; //$NON-NLS-1$
	private static ProjectTemplateManager manager;

	static {
		manager = new ProjectTemplateManager();
	}

	public static ProjectTemplateManager getInstance() {
		return manager;
	}

	private ProjectTemplateManager() {
		super();
	}

	/**
	 * Returns all templates for a specified category.
	 * 
	 * @param categoryId
	 * @return
	 */
	public IProjectTemplate[] getTemplates(String categoryId) {
		List<IProjectTemplate> ret = new ArrayList<IProjectTemplate>();
		IExtensionRegistry registry = Platform.getExtensionRegistry();
		IConfigurationElement[] elements = registry.getConfigurationElementsFor(EXTENSIONPOINT_RECORD_TEMPLATES);
		
		for (int i = 0; i < elements.length; i++) {
			try {
				if (elements[i].getName().equals("template") && 
						categoryId.equalsIgnoreCase(elements[i].getAttribute("category"))) {
					IProjectTemplate template = new ProjectTemplate();
					template.init(elements[i]);
					ret.add(template);
				}
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}

		return ret.toArray(new IProjectTemplate[ret.size()]);
	}
}
