/*******************************************************************************
 * Copyright © 2006, 2010 IBM Corporation and others.
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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.edt.mof.EClassifier;
import org.eclipse.edt.mof.EObject;

@SuppressWarnings("serial")
public class TemplateContext extends HashMap<Object, Object> {
	TemplateFactory tFactory;
	Map<String, Template> templates = new HashMap<String, Template>();
	
	public void setTemplateFactory(TemplateFactory factory) {
		this.tFactory = factory;
	}
	
	public Template getTemplate(String key) throws TemplateException {
		Template template = templates.get(key);
		if (template == null) {
			template = tFactory.createTemplate(key);
			templates.put(key, template);
		}
		return template;
	}
	
	public Template getTemplateFor(EClassifier eClassifier) throws TemplateException {
		return getTemplate(eClassifier.getETypeSignature());
	}
	
	@SuppressWarnings("rawtypes")
	public Template getTemplateFor(Class javaClass) throws TemplateException {
		return getTemplate(javaClass.getName());
	}
	
	public void gen(String genMethod, Object object, TemplateContext ctx, TabbedWriter out, Object...args) throws TemplateException {
		Template template = getTemplateFor(object.getClass());
		template.gen(genMethod, object, ctx, out, args);
	}

	public void gen(String genMethod, EObject object, TemplateContext ctx, TabbedWriter out, Object...args) throws TemplateException {
		Template template = getTemplateFor(object.getEClass());
		template.gen(genMethod, object, ctx, out, args);
	}
	
	public final void foreach(List<? extends EObject> list, char separator, String genMethod, TemplateContext ctx, TabbedWriter out, Object...args) throws TemplateException {
		for (int i=0; i<list.size(); i++) {
			this.gen(genMethod, list.get(i), ctx, out, args);
			if (i<list.size()-1) {
				out.print(separator);
				out.print(' ');
			}
		}
	}

	public List<Object> xlate(String genMethod, EObject object, TemplateContext ctx, Object...args) throws TemplateException {
		Template template = getTemplateFor(object.getEClass());
		return template.xlate(genMethod, object, ctx, args);
	}

	public void validate(String genMethod, EObject object, TemplateContext ctx, Object...args) throws TemplateException {
		Template template = getTemplateFor(object.getEClass());
		template.validate(genMethod, object, ctx, args);
	}

}
