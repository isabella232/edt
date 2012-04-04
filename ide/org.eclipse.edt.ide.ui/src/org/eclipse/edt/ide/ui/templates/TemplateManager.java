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
package org.eclipse.edt.ide.ui.templates;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.Platform;

public class TemplateManager {
	public static final String EXTENSIONPOINT_RECORD_TEMPLATES = org.eclipse.edt.ide.ui.EDTUIPlugin.PLUGIN_ID + ".eglTemplates"; //$NON-NLS-1$
	private static TemplateManager manager;

	static {
		manager = new TemplateManager();
	}

	public static TemplateManager getInstance() {
		return manager;
	}

	private TemplateManager() {
		super();
	}

	/**
	 * Returns all templates for a specified category.
	 * 
	 * @param categoryId
	 * @return
	 */
	public ITemplate[] getTemplates(String categoryId) {
		List<ITemplate> ret = new ArrayList<ITemplate>();
		IExtensionRegistry registry = Platform.getExtensionRegistry();
		IConfigurationElement[] elements = registry.getConfigurationElementsFor(EXTENSIONPOINT_RECORD_TEMPLATES);
		
		for (int i = 0; i < elements.length; i++) {
			try {
				if (elements[i].getName().equals("template") && 
						categoryId.equalsIgnoreCase(elements[i].getAttribute("category"))) {
					Template template = new Template();
					template.init(elements[i]);
					ret.add(template);
				}
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}

		return ret.toArray(new ITemplate[ret.size()]);
	}
}
