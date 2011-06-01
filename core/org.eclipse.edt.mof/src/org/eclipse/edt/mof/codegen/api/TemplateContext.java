/*******************************************************************************
 * Copyright © 2011 IBM Corporation and others.
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.edt.mof.EClassifier;
import org.eclipse.edt.mof.EObject;

@SuppressWarnings("serial")
public class TemplateContext extends HashMap<Object, Object> {
	TemplateFactory tFactory;
	Map<String, Template> templates = new HashMap<String, Template>();
	
	public class TemplateMethod {
		Method method;
		Template template;
		
		TemplateMethod(Template template, Method method) {
			this.template = template;
			this.method = method;
		}
	}
	
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
	
//	private TemplateMethod getTemplateMethod(String methodName, EClassifier classifier, Class<?>...classes) {
//		TemplateMethod tm = null;
//		Template template = null;
//		Method method = null;
//		try {
//			template = getTemplateFor(classifier);
//		}
//		catch (TemplateException tex) {}
//		if (template != null) {
//			method = primGetMethod(methodName, template.getClass(), classes);
//			if (method != null) {
//				tm = new TemplateMethod(template, method);
//			}
//		}
//		if (tm == null && classifier instanceof EClass) {
//			for (Class<?> clazz : classifier.getClass().getInterfaces()) {
//				classes[0] = clazz;
//			}
//			for (EClass eclazz : ((EClass)classifier).getSuperTypes()) {
////				classes[0] = clazz.getClass().getInterfaces()[0];
//				tm = getTemplateMethod(methodName, clazz, classes);
//				if (tm != null) break;
//			}
//		}
//		return tm;
//	}
	
	private TemplateMethod getTemplateMethod(String methodName, Class<?> objectClass, Object...args) throws TemplateException {
		TemplateMethod tm = null;
		Method method = null;
		Template template = null;
		template = getTemplateForClass(objectClass);
		if (template != null) {
			method = primGetMethod(methodName, template.getClass(), objectClass, args);
			if (method != null) {
				return new TemplateMethod(template, method);
			}
		}
		if (tm == null) {
			for (Class<?> iface : objectClass.getInterfaces()) {
				tm = getTemplateMethod(methodName, iface, args);
				if (tm != null) break;
			}
		}
		if (tm == null) {
			if (objectClass.getSuperclass() != null) {
				Class<?> superClass = objectClass.getSuperclass();
				tm = getTemplateMethod(methodName, superClass, args);
			}
		}
		return tm;
	}
	
	private Template getTemplateForClass(Class<?> clazz) {
		try {
			return getTemplate(clazz.getName());
		}
		catch (TemplateException tex) {}
		return null;
	}
	

	public Object invoke(String methodName, Object object, Object...args) {
		TemplateMethod tm = getTemplateMethod(methodName, object.getClass(), args);
		if (tm != null) {
			return doInvoke(tm.method, tm.template, object, args);
		}
		else {
			StringBuilder builder = new StringBuilder();
			builder.append("No such method ");
			builder.append(methodName);
			builder.append("(");
			builder.append(object.getClass().getName());
			builder.append(", ");
			for (Object arg : args) {
				builder.append(arg.getClass().getName());
				builder.append(", ");
			}
			builder.append(") for any template for Class " );
			builder.append(object.getClass().getName());
			throw new TemplateException(builder.toString());
		}
	}
	
	public Object invoke(String methodName, Class<?> clazz, Object...args) {
		TemplateMethod tm = getTemplateMethod(methodName, clazz, args);
		if (tm != null) {
			return doInvoke(tm.method, tm.template, clazz, args);
		}
		else {
			return invoke(methodName, (Object)clazz, args);
		}
	}

	public Object invokeSuper(String methodName, Object object, Object...args) {
		Class<?> superClass = object.getClass().getSuperclass();
		if (superClass == null) {
			throw new IllegalArgumentException("Class " + object.getClass().getName() + " has no super class");
		}
		TemplateMethod tm = getTemplateMethod(methodName, superClass, args);
		if (tm != null) {
			return doInvoke(tm.method, tm.template, object, args);
		}
		else {
			return invoke(methodName, (Object)object, args);
		}
	}
	
	public Object invokeSuper(String methodName, Class<?> clazz, Object...args) {
		Class<?> superClass = clazz.getSuperclass();
		if (superClass == null) {
			return invokeSuper(methodName, (Object)clazz, args);
		}
		TemplateMethod tm = getTemplateMethod(methodName, superClass, args);
		if (tm != null) {
			return doInvoke(tm.method, tm.template, clazz, args);
		}
		else {
			return invokeSuper(methodName, (Object)clazz, args);
		}

	}

	public Method primGetMethod(String methodName, Class<?> templateClass, Class<?> objectClass, Object...args) {
		Class<?>[] classes = new Class<?>[args.length];
		for (int i=0; i<args.length; i++) {
			if (args[i] instanceof EObject) {
				classes[i] = args[i].getClass().getInterfaces()[0];
			}
			else {
				classes[i] = args[i].getClass();
			}
		}
 		try {
			switch (args.length) {
			case 0: return templateClass.getMethod(methodName, objectClass);
			case 1: return templateClass.getMethod(methodName, objectClass, classes[0]);
			case 2: return templateClass.getMethod(methodName, objectClass, classes[0], classes[1]);
			case 3: return templateClass.getMethod(methodName, objectClass, classes[0], classes[1], classes[2]);
			case 4: return templateClass.getMethod(methodName, objectClass, classes[0], classes[1], classes[2], classes[3]);
			case 5: return templateClass.getMethod(methodName, objectClass, classes[0], classes[1], classes[2], classes[3], classes[4]);
			case 6: return templateClass.getMethod(methodName, objectClass, classes[0], classes[1], classes[2], classes[3], classes[4], classes[5]);
			case 7: return templateClass.getMethod(methodName, objectClass, classes[0], classes[1], classes[2], classes[3], classes[4], classes[5], classes[6]);
			case 8: return templateClass.getMethod(methodName, objectClass, classes[0], classes[1], classes[2], classes[3], classes[4], classes[5], classes[6], classes[7]);
			case 9: return templateClass.getMethod(methodName, objectClass, classes[0], classes[1], classes[2], classes[3], classes[4], classes[5], classes[6], classes[7], classes[8]);
			default: return templateClass.getMethod(methodName, objectClass);
			}
		} catch (SecurityException e) {
			throw new RuntimeException(e);
		} catch (NoSuchMethodException e) {
			// Do nothing to allow search to continue
		}
		return null;
	}	

	public Object doInvoke(Method method, Template template, Object object, Object[] args) {
		try {
			switch (args.length) {
			case 0: return method.invoke(template, object);
			case 1: return method.invoke(template, object, args[0]);
			case 2: return method.invoke(template, object, args[0], args[1]);
			case 3: return method.invoke(template, object, args[0], args[1], args[2]);
			case 4: return method.invoke(template, object, args[0], args[1], args[2], args[3]);
			case 5: return method.invoke(template, object, args[0], args[1], args[2], args[3], args[4]);
			case 6: return method.invoke(template, object, args[0], args[1], args[2], args[3], args[4], args[5]);
			case 7: return method.invoke(template, object, args[0], args[1], args[2], args[3], args[4], args[5], args[6]);
			case 8: return method.invoke(template, object, args[0], args[1], args[2], args[3], args[4], args[5], args[6], args[7]);
			case 9: return method.invoke(template, object, args[0], args[1], args[2], args[3], args[4], args[5], args[6], args[7], args[8]);
			default: return method.invoke(template, object, args);
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
}
