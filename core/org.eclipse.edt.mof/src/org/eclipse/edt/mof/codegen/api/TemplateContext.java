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
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.edt.mof.EClass;
import org.eclipse.edt.mof.EClassifier;
import org.eclipse.edt.mof.EObject;
import org.eclipse.edt.mof.EType;
import org.eclipse.edt.mof.serialization.Environment;
import org.eclipse.edt.mof.serialization.IEnvironment;

@SuppressWarnings("serial")
public class TemplateContext extends HashMap<Object, Object> {
	TemplateFactory tFactory;
	IEnvironment env;
	Map<String, Template> templates = new HashMap<String, Template>();

	public class TemplateMethod {
		Method method;
		Template template;

		public TemplateMethod(Template template, Method method) {
			this.template = template;
			this.method = method;
		}

		public Method getMethod() {
			return method;
		}

		public Template getTemplate() {
			return template;
		}

	}

	public TemplateContext() {
		if (Environment.getCurrentEnv() != null)
			this.env = Environment.getCurrentEnv();
		else
			this.env = Environment.INSTANCE;
	}

	public TemplateContext(IEnvironment env) {
		this.env = env;
	}

	public void setEnvironment(IEnvironment env) {
		this.env = env;
	}
	
	public IEnvironment getEnvironment() {
		return this.env;
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

	/**
	 * Returns template instance associated with given key. Only attempt creation of a new template instance if a Template
	 * class for the given key exists.
	 * @param key
	 * @return
	 */
	public Template getTemplateRaw(String key) {
		Template template = templates.get(key);
		if (template == null && tFactory.templates.containsKey(key)) {
			template = tFactory.createTemplateRaw(key);
			templates.put(key, template);
		}
		return template;
	}

	public String getTemplateKey(Template template) {
		for (java.util.Map.Entry<String, Class<? extends Template>> entry : tFactory.templates.entrySet()) {
			if (entry.getValue().equals(template.getClass())) {
				return entry.getKey();
			}
		}
		return null;
	}

	public Template getTemplateFor(EClassifier eClassifier) throws TemplateException {
		return getTemplate(eClassifier.getETypeSignature());
	}

	@SuppressWarnings("rawtypes")
	public Template getTemplateFor(Class javaClass) throws TemplateException {
		return getTemplate(javaClass.getName());
	}

	public final void foreach(List<? extends EObject> list, char separator, String genMethod, Object... args)
		throws TemplateException {
		for (int i = 0; i < list.size(); i++) {
			this.invoke(genMethod, list.get(i), args);
			if (i < list.size() - 1) {
				((TabbedWriter) args[1]).print(separator);
				((TabbedWriter) args[1]).print(' ');
			}
		}
	}

	private TemplateMethod getTemplateMethod(String methodName, Class<?> objectClass, Object... args) throws TemplateException {
		TemplateMethod tm = null;
		Method method = null;
		Template template = null;
		template = getTemplateForClass(objectClass);
		if (template != null) {
			method = primGetMethod(methodName, template.getClass(), objectClass, args);
			if (method != null) {
				tm = new TemplateMethod(template, method);
			}
		}
		if (tm == null) {
			for (Class<?> iface : objectClass.getInterfaces()) {
				tm = getTemplateMethod(methodName, iface, args);
				if (tm != null)
					break;
			}
			if (tm == null && objectClass.getSuperclass() != null) {
				Class<?> superClass = objectClass.getSuperclass();
				tm = getTemplateMethod(methodName, superClass, args);
			}
		}
		return tm;
	}

	public TemplateMethod getTemplateMethod(String methodName, EClassifier eClass, Object... args) throws TemplateException {
		TemplateMethod tm = null;
		Method method = null;
		Template template = null;
		template = getTemplateForEClassifier(eClass);
		if (template != null) {
			method = primGetMethod(methodName, template.getClass(), eClass, args);
			if (method != null) {
				tm = new TemplateMethod(template, method);
			}
		}
		if (tm == null && eClass instanceof EClass) {
			for (EClass part : ((EClass) eClass).getSuperTypes()) {
				tm = getTemplateMethod(methodName, part, args);
				if (tm != null)
					break;
			}
		}
		return tm;
	}

	public Template getTemplateForClass(Class<?> clazz) {
		return getTemplateRaw(clazz.getName());
	}

	public Template getTemplateForEClassifier(EClassifier clazz) {
		return getTemplateRaw(clazz.getETypeSignature());
	}

	public Class<?> getClassForTemplate(Template template) {
		String signature = getTemplateKey(template);
		try {
			return Class.forName(signature, true, tFactory.classloader);
		}
		catch (Exception tex) {}
		return null;
	}

	public EClassifier getEClassifierForTemplate(Template template) {
		String signature = getTemplateKey(template);
		try {
			return (EClassifier) env.find(signature);
		}
		catch (Exception tex) {}
		return null;
	}

	public Object invoke(String methodName, Object object, Object... args) {
		TemplateMethod tm = getTemplateMethod(methodName, object.getClass(), args);
		if (tm != null) {
			return doInvoke(tm.method, tm.template, object, args);
		} else {
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
			builder.append(") for any template for Class ");
			builder.append(object.getClass().getName());
			throw new TemplateException(builder.toString());
		}
	}

	public Object invoke(String methodName, Class<?> clazz, Object... args) {
		TemplateMethod tm = getTemplateMethod(methodName, clazz, args);
		if (tm != null) {
			return doInvoke(tm.method, tm.template, clazz, args);
		} else {
			return invoke(methodName, (Object) clazz, args);
		}
	}

	public Object invokeSuper(Template template, String methodName, Object object, Object... args) {
		// Get Class associated with the given template
		Class<?> clazz = getClassForTemplate(template);
		Class<?> superClass = clazz.getSuperclass();
		if (superClass == null) {
			throw new IllegalArgumentException("Class " + object.getClass().getName() + " has no super class");
		}
		TemplateMethod tm = getTemplateMethod(methodName, superClass, args);
		if (tm != null) {
			return doInvoke(tm.method, tm.template, object, args);
		} else {
			return invoke(methodName, (Object) object, args);
		}
	}

	public Object invokeSuper(Template template, String methodName, Class<?> clazz, Object... args) {
		// Get Class associated with the given template
		Class<?> baseclass = getClassForTemplate(template);
		Class<?> superClass = baseclass.getSuperclass();
		if (superClass == null) {
			return invokeSuper(template, methodName, (Object) clazz, args);
		}
		TemplateMethod tm = getTemplateMethod(methodName, superClass, args);
		if (tm != null) {
			return doInvoke(tm.method, tm.template, clazz, args);
		} else {
			return invokeSuper(template, methodName, (Object) clazz, args);
		}

	}

	public Object invoke(String methodName, EObject object, Object... args) {
		TemplateMethod tm = getTemplateMethod(methodName, object.getEClass(), args);
		if (tm != null) {
			return doInvoke(tm.method, tm.template, object, args);
		} else {
			return invoke(methodName, (Object) object, args);
		}
	}

	public Object invoke(String methodName, EType etype, Object... args) {
		TemplateMethod tm = getTemplateMethod(methodName, etype.getEClassifier(), args);
		if (tm == null) {
			tm = getTemplateMethod(methodName, etype.getEClassifier().getEClass(), args);
		}
		if (tm != null) {
			return doInvoke(tm.method, tm.template, etype, args);
		} else {
			return invoke(methodName, (Object) etype, args);
		}
	}

	public Object invokeSuper(Template template, String methodName, EObject object, Object... args) {
		// Get EClassifier associated with the given template
		EClass clazz = (EClass) getEClassifierForTemplate(template);
		if (clazz != null) {
			EClass superClass = clazz.getSuperTypes().isEmpty() ? null : clazz.getSuperTypes().get(0);
			if (superClass == null) {
				throw new IllegalArgumentException("EClass " + object.getEClass().getETypeSignature() + " has no super class");
			}
			TemplateMethod tm = getTemplateMethod(methodName, superClass, args);
			if (tm != null) {
				return doInvoke(tm.method, tm.template, object, args);
			}
		}
		return invoke(methodName, (Object) object, args);
	}

	public Object invokeSuper(Template template, String methodName, EClass clazz, Object... args) {
		// Get Class associated with the given template
		EClass baseclass = (EClass) getEClassifierForTemplate(template);
		EClass superClass = baseclass.getSuperTypes().isEmpty() ? null : baseclass.getSuperTypes().get(0);
		if (superClass == null) {
			return invokeSuper(template, methodName, (EObject) clazz, args);
		}
		TemplateMethod tm = getTemplateMethod(methodName, superClass, args);
		if (tm != null) {
			return doInvoke(tm.method, tm.template, clazz, args);
		} else {
			return invokeSuper(template, methodName, (EObject) clazz, args);
		}

	}

	public Method primGetMethod(String methodName, Class<?> templateClass, EClassifier eClassifier, Object... args) {
		Class<?> clazz = null;
		try {
			clazz = Class.forName(eClassifier.getETypeSignature(), true, templateClass.getClassLoader());
		}
		catch (Exception x) {}
		if (clazz == null)
			clazz = eClassifier.getClass();
		return primGetMethod(methodName, templateClass, clazz, args);
	}

	public Method primGetMethod(String methodName, Class<?> templateClass, Class<?> objectClass, Object... args) {
		Method method = null;
	    do {
	        for (Method m : templateClass.getDeclaredMethods()) {
	            if (Modifier.isPublic(m.getModifiers())) {
					boolean matches = true;
					if (m.getName().equals(methodName)) {
						if (m.getParameterTypes().length == args.length + 1) {
							Class<?>[] pTypes = m.getParameterTypes();
							if (pTypes[0].isAssignableFrom(objectClass)) {
								for (int i = 0; i < args.length; i++) {
									if (args[i] != null && !pTypes[i + 1].isAssignableFrom(args[i].getClass())) {
										matches = false;
										break;
									}
								}
							} else
								matches = false;
						} else
							matches = false;
					} else
						matches = false;
					if (matches) {
						method = m;
						break;
					}
				}
	        }
        } while (method == null && (templateClass = templateClass.getSuperclass()) != null);
		return method;
	}

	public Object doInvoke(Method method, Template template, Object object, Object[] args) {
		try {
			switch (args.length) {
				case 0:
					return method.invoke(template, object);
				case 1:
					return method.invoke(template, object, args[0]);
				case 2:
					return method.invoke(template, object, args[0], args[1]);
				case 3:
					return method.invoke(template, object, args[0], args[1], args[2]);
				case 4:
					return method.invoke(template, object, args[0], args[1], args[2], args[3]);
				case 5:
					return method.invoke(template, object, args[0], args[1], args[2], args[3], args[4]);
				case 6:
					return method.invoke(template, object, args[0], args[1], args[2], args[3], args[4], args[5]);
				case 7:
					return method.invoke(template, object, args[0], args[1], args[2], args[3], args[4], args[5], args[6]);
				case 8:
					return method.invoke(template, object, args[0], args[1], args[2], args[3], args[4], args[5], args[6], args[7]);
				case 9:
					return method.invoke(template, object, args[0], args[1], args[2], args[3], args[4], args[5], args[6], args[7], args[8]);
				case 10:
					return method.invoke(template, object, args[0], args[1], args[2], args[3], args[4], args[5], args[6], args[7], args[8], args[9]);
				case 11:
					return method.invoke(template, object, args[0], args[1], args[2], args[3], args[4], args[5], args[6], args[7], args[8], args[9], args[10]);
				case 12:
					return method.invoke(template, object, args[0], args[1], args[2], args[3], args[4], args[5], args[6], args[7], args[8], args[9], args[10],
							args[11]);
				case 13:
					return method.invoke(template, object, args[0], args[1], args[2], args[3], args[4], args[5], args[6], args[7], args[8], args[9], args[10],
							args[11], args[12]);
				case 14:
					return method.invoke(template, object, args[0], args[1], args[2], args[3], args[4], args[5], args[6], args[7], args[8], args[9], args[10],
							args[11], args[12], args[13]);
				case 15:
					return method.invoke(template, object, args[0], args[1], args[2], args[3], args[4], args[5], args[6], args[7], args[8], args[9], args[10],
							args[11], args[12], args[13], args[14]);
				case 16:
					return method.invoke(template, object, args[0], args[1], args[2], args[3], args[4], args[5], args[6], args[7], args[8], args[9], args[10],
							args[11], args[12], args[13], args[14], args[15]);
				case 17:
					return method.invoke(template, object, args[0], args[1], args[2], args[3], args[4], args[5], args[6], args[7], args[8], args[9], args[10],
							args[11], args[12], args[13], args[14], args[15], args[16]);
				case 18:
					return method.invoke(template, object, args[0], args[1], args[2], args[3], args[4], args[5], args[6], args[7], args[8], args[9], args[10],
							args[11], args[12], args[13], args[14], args[15], args[16], args[17]);
				case 19:
					return method.invoke(template, object, args[0], args[1], args[2], args[3], args[4], args[5], args[6], args[7], args[8], args[9], args[10],
							args[11], args[12], args[13], args[14], args[15], args[16], args[17], args[18]);
				case 20:
					return method.invoke(template, object, args[0], args[1], args[2], args[3], args[4], args[5], args[6], args[7], args[8], args[9], args[10],
							args[11], args[12], args[13], args[14], args[15], args[16], args[17], args[18], args[19]);
				default:
					return method.invoke(template, object, args);
			}
		}
		catch (TemplateException te) {
			throw te;
		}
		catch (InvocationTargetException itx) {
			Throwable t = itx.getTargetException();
			if (t instanceof TemplateException)
				throw (TemplateException) t;
			else
				throw new TemplateException(t);
		}
		catch (Exception e) {
			throw new TemplateException(e);
		}

	}
}
