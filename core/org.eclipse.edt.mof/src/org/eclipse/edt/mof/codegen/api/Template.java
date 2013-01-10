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
package org.eclipse.edt.mof.codegen.api;

import java.lang.reflect.Method;
import java.util.List;

import org.eclipse.edt.mof.EObject;

public interface Template {
	void gen(String genMethod, Object eObject, TemplateContext ctx, TabbedWriter out, Object...args) throws TemplateException;
	void gen(String genMethod, EObject eObject, TemplateContext ctx, TabbedWriter out, Object...args) throws TemplateException;
	List<Object> xlate(String xlateMethod, EObject eObject, TemplateContext ctx, Object...args) throws TemplateException;
	void validate(String xlateMethod, EObject eObject, TemplateContext ctx, Object...args) throws TemplateException;
	Method getMethod(String methodName, boolean doGet, Class<?>...classes);
}
