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

import java.lang.reflect.Method;
import java.util.List;

import org.eclipse.edt.mof.EClass;
import org.eclipse.edt.mof.EClassifier;
import org.eclipse.edt.mof.EObject;
import org.eclipse.edt.mof.MofFactory;
import org.eclipse.edt.mof.serialization.Environment;



public abstract class AbstractTemplate implements Template {
	public static MofFactory factory = MofFactory.INSTANCE;
	
			
	@Override
	public void gen(String genMethod, EObject object, TemplateContext ctx, TabbedWriter out, Object...args) throws TemplateException {
		Class impls = object.getClass().getInterfaces()[0];
		Method method;
		try {
			method = getMethod(genMethod, impls, ctx.getClass(), out.getClass(), true, args);
			if (method != null) {
				if (args == null || args.length == 0) {
					method.invoke(this, object, ctx, out);
				}
				else {
					method.invoke(this, object, ctx, out, args);
				}
			}
			else {
				StringBuilder builder = new StringBuilder();
				builder.append("No such method ");
				builder.append(genMethod);
				builder.append("(");
				builder.append(impls.getName());
				builder.append(", ");
				builder.append(ctx.getClass().getName());
				builder.append(", ");
				builder.append(out.getClass().getName());
				builder.append(") for template " );
				builder.append(this.getClass().getName());
				throw new TemplateException(builder.toString());
			}
		} catch (Exception e) {
			throw new TemplateException(e);
		}
	}
	
	
	@SuppressWarnings("unchecked")
	@Override
	public final List<Object> xlate(String genMethod, EObject object, TemplateContext ctx, Object...args) throws TemplateException {
		Method method;
		Class impls = object.getClass().getInterfaces()[0];
		try {
			method = this.getMethod(genMethod, impls, ctx.getClass(), true, args);
			if (args == null || args.length == 0) {
				return (List<Object>)method.invoke(this, object, ctx);
			}
			else {
				return (List<Object>)method.invoke(this, object, ctx, args);
			}
		} catch (Exception e) {
			throw new TemplateException(e);
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public final void validate(String validateMethod, EObject object, TemplateContext ctx, Object...args) throws TemplateException {
		Method method;
		Class impls = object.getClass().getInterfaces()[0];
		try {
			method = this.getMethod(validateMethod, impls, ctx.getClass(), true, args);
			if (args == null || args.length == 0) {
				method.invoke(this, object, ctx);
			}
			else {
				method.invoke(this, object, ctx, args);
			}
		} catch (Exception e) {
			throw new TemplateException(e);
		}
	}


	@SuppressWarnings("unchecked")
	private Method primGetMethod(String methodName, Class clazz, Class ctx, Class out, Object[] args) {
		Method method = null;
		try {
			if (args == null || args.length == 0) {
				method = this.getClass().getMethod(methodName, clazz, ctx, out);
			}
			else {
				method = this.getClass().getMethod(methodName, clazz, ctx, out, Object[].class);
			}
		} catch (SecurityException e) {
			throw new RuntimeException(e);
		} catch (NoSuchMethodException e) {
			// Do nothing to allow search to continue
		}
		return method;
	}
	
	@SuppressWarnings("unchecked")
	private Method getMethod(String methodName, Class ifaceClass, Class ctx, Class out, boolean doGet, Object[] args) {
		Method method = null;
		if (doGet) {
			method = primGetMethod(methodName, ifaceClass, ctx, out, args);
		}
		if (method == null) {
			for (Class iface : ifaceClass.getInterfaces()) {
				method = primGetMethod(methodName, iface, ctx, out, args);
				if (method != null) break;
			}
		}
		if (method == null) {
			if (ifaceClass.getInterfaces().length > 0) {
				method = getMethod(methodName, ifaceClass.getInterfaces()[0], ctx, out, false, args);
			}
		}
		return method;
	}
	
	@SuppressWarnings("unchecked")
	private Method primGetMethod(String methodName, Class clazz, Class ctx, Object[] args) {
		Method method = null;
		try {
			if (args == null || args.length == 0) {
				method = this.getClass().getMethod(methodName, clazz, ctx);
			}
			else {
				method = this.getClass().getMethod(methodName, clazz, ctx, Object[].class);
			}
		} catch (SecurityException e) {
			throw new RuntimeException(e);
		} catch (NoSuchMethodException e) {
			// Do nothing to allow search to continue
		}
		return method;
	}
	
	@SuppressWarnings("unchecked")
	private Method getMethod(String methodName, Class ifaceClass, Class ctx, boolean doGet, Object[] args) {
		Method method = null;
		if (doGet) {
			method = primGetMethod(methodName, ifaceClass, ctx, args);
		}
		if (method == null) {
			for (Class iface : ifaceClass.getInterfaces()) {
				method = primGetMethod(methodName, iface, ctx, args);
				if (method != null) break;
			}
		}
		if (method == null) {
			if (ifaceClass.getInterfaces().length > 0) {
				method = getMethod(methodName, ifaceClass.getInterfaces()[0], ctx, false, args);
			}
		}
		return method;
	}


	
	
	public EObject newInstance(String typeSignature) throws TemplateException {
		EClass eClass = (EClass)getEClassifier(typeSignature);
		return eClass.newInstance();
	}
	
	public EClassifier getEClassifier(String typeSignature) throws TemplateException {
		try {
			return (EClassifier)Environment.INSTANCE.findType(typeSignature);
		} catch (Exception e) {
			throw new TemplateException(e);
		} 
	}
}
