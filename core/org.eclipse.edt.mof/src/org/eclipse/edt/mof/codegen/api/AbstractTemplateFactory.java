/*******************************************************************************
 * Copyright © 2010 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.edt.mof.codegen.api;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

public abstract class AbstractTemplateFactory implements TemplateFactory {
	Map<String, Class> templates = new HashMap<String, Class>();

	public AbstractTemplateFactory() { super(); }
		
	@SuppressWarnings("unchecked")
	public Template createTemplate(String key) throws TemplateException {
		Class<Template> clazz = templates.get(key);
		if (clazz == null) throw new TemplateException("Template not found: " + key);
		try {
			return (Template)clazz.newInstance();
		} catch (IllegalAccessException e) {
			throw new TemplateException(e);
		} catch (InstantiationException e2) {
			throw new TemplateException(e2);
		}
	}
	
	public void load(String propertiesName) throws TemplateException {
		ResourceBundle bundle = ResourceBundle.getBundle(propertiesName);
		Enumeration<String> keys = bundle.getKeys();
		while (keys.hasMoreElements()) {
			String key = keys.nextElement();
			String className = bundle.getString(key);
			try {
				Class clazz = Class.forName(className);
				templates.put(key, clazz);
			} catch (ClassNotFoundException e3) {
				templates.put(key, MissingTemplate.class);
			}
		}
	}
	
	public abstract String getPropertyFileName();
}
