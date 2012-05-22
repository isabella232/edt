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
package org.eclipse.edt.mof.codegen.api;

import java.lang.reflect.InvocationTargetException;
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
		gen(genMethod, (Object)object, ctx, out, args);
	}

	@Override
	public void gen(String genMethod, Object object, TemplateContext ctx, TabbedWriter out, Object...args) throws TemplateException {
		Class<?> impls = object.getClass().getInterfaces()[0];
		Method method;
		try {
			method = getMethod(genMethod, true, impls, ctx.getClass(), out.getClass(), args.getClass());
			if (method != null) {
				method.invoke(this, object, ctx, out, args);
				return;
			}
			if (args.length == 0) {			
				method = getMethod(genMethod, true, impls, ctx.getClass(), out.getClass());
				if (method != null) {
					method.invoke(this, object, ctx, out);
					return;
				}
			}
			if (method == null) {
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
		} 
		catch (TemplateException te) {
			throw te;
		}
		catch (InvocationTargetException itx) {
			Throwable t = itx.getTargetException();
			if (t instanceof TemplateException)
				throw (TemplateException)t;
			else 
				throw new TemplateException(t);
		}
		catch (Exception e) {
			throw new TemplateException(e);
		}
	}
	
	
	@SuppressWarnings({ "unchecked", "unused" })
	@Override
	public List<Object> xlate(String xlateMethod, EObject object, TemplateContext ctx, Object...args) throws TemplateException {
		Method method;
		Class<?> impls = object.getClass().getInterfaces()[0];
		try {
			method = getMethod(xlateMethod, true, impls, ctx.getClass(), args.getClass());
			if (method != null) {
				return (List<Object>)method.invoke(this, object, ctx, args);
			}
			else if (args.length == 0) {			
				method = getMethod(xlateMethod, true, impls, ctx.getClass());
				if (method != null) {
					return (List<Object>)method.invoke(this, object, ctx);
				}
			}
			if (method == null) {
				StringBuilder builder = new StringBuilder();
				builder.append("No such method ");
				builder.append(xlateMethod);
				builder.append("(");
				builder.append(impls.getName());
				builder.append(", ");
				builder.append(ctx.getClass().getName());
				builder.append(") for template " );
				builder.append(this.getClass().getName());
				throw new TemplateException(builder.toString());
			}
		} 
		catch (TemplateException te) {
			throw te;
		} 
		catch (InvocationTargetException itx) {
			Throwable t = itx.getTargetException();
			if (t instanceof TemplateException)
				throw (TemplateException)t;
			else 
				throw new TemplateException(t);
		}
		catch (Exception e) {
			throw new TemplateException(e);
		}
		return null;
	}

	@Override
	public void validate(String validateMethod, EObject object, TemplateContext ctx, Object...args) throws TemplateException {
		Method method;
		Class<?> impls = object.getClass().getInterfaces()[0];
		try {
			method = getMethod(validateMethod, true, impls, ctx.getClass(), args.getClass());
			if (method != null) {
				method.invoke(this, object, ctx, args);
				return;
			}
			else if (args.length == 0) {			
				method = getMethod(validateMethod, true, impls, ctx.getClass());
				if (method != null) {
					method.invoke(this, object, ctx);
					return;
				}
			}
			if (method == null) {
				StringBuilder builder = new StringBuilder();
				builder.append("No such method ");
				builder.append(validateMethod);
				builder.append("(");
				builder.append(impls.getName());
				builder.append(", ");
				builder.append(ctx.getClass().getName());
				builder.append(") for template " );
				builder.append(this.getClass().getName());
				throw new TemplateException(builder.toString());
			}
		} 
		catch (TemplateException te) {
			throw te;
		} 
		catch (InvocationTargetException itx) {
			Throwable t = itx.getTargetException();
			if (t instanceof TemplateException)
				throw (TemplateException)t;
			else 
				throw new TemplateException(t);
		}
		catch (Exception e) {
			throw new TemplateException(e);
		}
	}

	@Override
	public Method getMethod(String methodName, boolean doGet, Class<?>...classes ) {
		Method method = null;
		if (doGet) {
			method = primGetMethod(methodName, classes);
		}
		Class<?> ifaceClass = classes[0];
		if (method == null) {
			for (Class<?> iface : ifaceClass.getInterfaces()) {
				classes[0] = iface;
				method = primGetMethod(methodName, classes);
				if (method != null) break;
			}
		}
		if (method == null) {
			for (Class<?> iface : ifaceClass.getInterfaces()) {
				classes[0] = iface;
				method = getMethod(methodName, false, classes);
				if (method != null) break;
			}
		}
		return method;
	}

	private Method primGetMethod(String methodName, Class<?>...classes) {
		Method method = null;
		try {
			method = this.getClass().getMethod(methodName, classes);
		} catch (SecurityException e) {
			throw new RuntimeException(e);
		} catch (NoSuchMethodException e) {
			// Do nothing to allow search to continue
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
