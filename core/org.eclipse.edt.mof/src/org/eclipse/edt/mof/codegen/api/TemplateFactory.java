/*******************************************************************************
 * Copyright © 2010, 2013 IBM Corporation and others.
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
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;

public class TemplateFactory {
	Map<String, Class<? extends Template>> templates = new HashMap<String, Class<? extends Template>>();
	ClassLoader classloader;

	public TemplateFactory() {
		super();
	}

	public Template createTemplate(String key) throws TemplateException {
		Class<? extends Template> clazz = templates.get(key);
		if (clazz == null)
			throw new TemplateException("Template not found: " + key);
		try {
			return (Template) clazz.newInstance();
		}
		catch (IllegalAccessException e) {
			throw new TemplateException(e);
		}
		catch (InstantiationException e2) {
			throw new TemplateException(e2);
		}
	}
	
	public Template createTemplateRaw(String key) throws TemplateException {
		Class<? extends Template> clazz = templates.get(key);
		if (clazz == null)
			return null;
		try {
			return (Template) clazz.newInstance();
		}
		catch (IllegalAccessException e) {
			throw new TemplateException(e);
		}
		catch (InstantiationException e2) {
			throw new TemplateException(e2);
		}
	}
	

	@SuppressWarnings("unchecked")
	public void load(String templateFilePaths, ClassLoader loader) throws TemplateException {
		classloader = loader;
		// the template file path will be 1 or more locations for the templates.properties files involved with this
		// implementation. If more than 1 location is in the list, then it will be separated by a semi-colon. We need to
		// split these out into individual locations and process them in order.
		String[] templateFiles = templateFilePaths.split("[;]");
		for (String templateFile : templateFiles) {
			// process this property file
			ResourceBundle bundle = ResourceBundle.getBundle(templateFile, Locale.getDefault(), loader);
			Enumeration<String> keys = bundle.getKeys();
			while (keys.hasMoreElements()) {
				String key = keys.nextElement();
				String className = bundle.getString(key).trim();
				try {
					// if this entry is already in the template map, don't replace it. This keeps the list such that the 1st
					// entry that we come across remains as the class to use. For example, if the list is this:
					// org.eclipse.edt.gen.myImpl;org.eclipse.edt.gen.java then a class from myImpl would not be overriden by
					// one further down the properties list.
					if (templates.get(key) == null) {
						Class clazz = Class.forName(className, true, loader);
						templates.put(key, clazz);
					}
				}
				catch (ClassNotFoundException e3) {
					templates.put(key, MissingTemplate.class);
				}
				catch (NoClassDefFoundError err) {
					templates.put(key, MissingTemplate.class);
				}
			}
		}
	}
}

