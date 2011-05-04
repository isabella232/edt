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
package org.eclipse.edt.gen;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Stack;

import org.eclipse.edt.gen.EGLMessages.AccumulatingGenerationMessageRequestor;
import org.eclipse.edt.gen.EGLMessages.IGenerationMessageRequestor;
import org.eclipse.edt.mof.EObject;
import org.eclipse.edt.mof.codegen.api.TabbedWriter;
import org.eclipse.edt.mof.codegen.api.Template;
import org.eclipse.edt.mof.codegen.api.TemplateContext;
import org.eclipse.edt.mof.codegen.api.TemplateException;
import org.eclipse.edt.mof.egl.Annotation;
import org.eclipse.edt.mof.egl.AnnotationType;
import org.eclipse.edt.mof.egl.Classifier;
import org.eclipse.edt.mof.egl.EGLClass;
import org.eclipse.edt.mof.egl.Element;
import org.eclipse.edt.mof.egl.IrFactory;
import org.eclipse.edt.mof.egl.Stereotype;
import org.eclipse.edt.mof.egl.StructPart;
import org.eclipse.edt.mof.egl.TryStatement;
import org.eclipse.edt.mof.egl.Type;

@SuppressWarnings("serial")
public abstract class EglContext extends TemplateContext {

	// this class is used within the EglContext to dynamically obtain the template and method for a signature
	private class TemplateMethod {
		Template template;
		Method method;

		public TemplateMethod(Template template, Method method) {
			this.template = template;
			this.method = method;
		}

		public Template getTemplate() {
			return template;
		}

		public Method getMethod() {
			return method;
		}
	}

	// this variable is used by the gen method with the type signature, to indicate where we are in the logic for processing
	// non-eobject and eobject types. The typetemplate will need to use this for each generator
	public enum TypeLogicKind {
		Process, Finish
	}

	private IrFactory factory;

	// The output stream for generators using this Context.
	private TabbedWriter out;

	// this is the nesting of exit/continue labels for various statements
	private Stack<Label> labelStack;

	// This is the nesting of try statements.
	private Stack<TryStatement> tryStack;

	// this is the user overrideable set of properties files values
	private Map<String, String> primitiveTypeMappings;
	private Map<String, String> nativeTypeMappings;
	private Map<String, String> messageMappings;

	// maintain a list of looked up templates and methods, for performance purposes
	private HashMap<String, TemplateMethod> templateMethods;
	private List<String> templateTraceEntries;

	// the message requestor
	private IGenerationMessageRequestor messageRequestor;

	// This is used by nextTempName.
	private int tempIndex;

	public EglContext(AbstractGeneratorCommand processor) {
		factory = IrFactory.INSTANCE;
		tryStack = new Stack<TryStatement>();
		labelStack = new Stack<Label>();
		messageRequestor = new AccumulatingGenerationMessageRequestor();
		// handle the loads
		ClassLoader loader = processor.getClass().getClassLoader();
		nativeTypeMappings = load(processor.getNativeTypes(), loader);
		primitiveTypeMappings = load(processor.getPrimitiveTypes(), loader);
		messageMappings = load(processor.getEGLMessages(), loader);
		// create our hashmap for template/methods loads
		templateMethods = new HashMap<String, TemplateMethod>();
		// maintain a list of method lookup trace entries, only if no tracing was requested. If the -trace option was
		// specified, then all trace messages are written to System.out as they occur. If -trace was not set (the default),
		// then a list of the last 50 entries will be maintained, and dumped out if there is an unhandled exception.
		templateTraceEntries = new ArrayList<String>();
	}

	/**
	 * Returns the factory for creating Egl model element instances.
	 * @return the IrFactory
	 */
	public IrFactory getFactory() {
		return factory;
	}

	/**
	 * Returns the TabbedWriter of this Context.
	 * @return the TabbedWriter of this Context.
	 */
	public TabbedWriter getWriter() {
		return out;
	}

	/**
	 * Sets the TabbedWriter of this Context.
	 * @param out the new TabbedWriter of this Context.
	 */
	public void setWriter(TabbedWriter out, Object... args) {
		this.out = out;
	}

	/**
	 * @param String The exit and continue statement labels we expect to be pushing on the stack
	 */
	public void pushLabelStack(Label label) {
		this.labelStack.push(label);
	}

	/**
	 * @param String The exit and continue statement labels we expect to be popping off the stack
	 */
	public void popLabelStack() {
		this.labelStack.pop();
	}

	/**
	 * try and locate the first entry beginning with the passed string and return the value or null
	 */
	public Label searchLabelStack(int type) {
		int i = this.labelStack.size();
		// we are interested in scanning lifo for the entries
		while (i > 0) {
			Label label = this.labelStack.get(--i);
			// are we trying to match the nearest generic or a specific one
			if (type == Label.LABEL_TYPE_GENERIC || label.getType() == type)
				return label;
		}
		return null;
	}

	/**
	 * Empty the try stack
	 */
	public void clearTryStack() {
		this.tryStack.clear();
	}

	/**
	 * Checks if the stack is empty or not
	 * @return true if the stack has no elements
	 */
	public boolean tryStackIsEmpty() {
		return tryStack.isEmpty();
	}

	/**
	 * @param stmt The TryStatement to push on the stack
	 */
	public void pushTryStack(TryStatement stmt) {
		this.tryStack.push(stmt);
	}

	/**
	 * @param stmt The TryStatement we expect to be popping off the stack
	 */
	public void popTryStack(TryStatement stmt) {
		this.tryStack.pop();
	}

	/**
	 * Returns a String that can be used as the name of a temporary variable. It will be "eze$Temp" plus a number. The number
	 * is 1 the first time this method is called, then 2, then 3, and so on.
	 */
	public int nextTempIndex() {
		tempIndex++;
		return tempIndex;
	}

	/**
	 * Returns a String that can be used as the name of a temporary variable. It will be "eze$Temp" plus a number. The number
	 * is 1 the first time this method is called, then 2, then 3, and so on.
	 */
	public String nextTempName() {
		return Constants.temporaryVariablePrefix + nextTempIndex();
	}

	public IGenerationMessageRequestor getMessageRequestor() {
		return messageRequestor;
	}

	public Map<String, String> getMessageMapping() {
		return messageMappings;
	}

	public boolean mapsToPrimitiveType(Type type) {
		return primitiveTypeMappings.containsKey(type.getClassifier().getTypeSignature());
	}

	public String getPrimitiveMapping(String item) {
		String value = primitiveTypeMappings.get(item);
		if (value != null)
			return value;
		else
			return null;
	}

	public String getPrimitiveMapping(Type type) {
		String value = primitiveTypeMappings.get(type.getClassifier().getTypeSignature());
		if (value != null)
			return value;
		else {
			handleValidationError(type);
			throw new TemplateException("", type);
		}
	}

	public boolean mapsToNativeType(Type type) {
		return nativeTypeMappings.containsKey(type.getClassifier().getTypeSignature());
	}

	public String getNativeMapping(String item) {
		String value = nativeTypeMappings.get(item);
		if (value != null)
			return value;
		else
			return null;
	}

	public String getNativeImplementationMapping(Type type) {
		// the nativetypes mapping file works like this:
		// if there are no entries in the table, then the type signature will be used for both interface and implementation
		// names.
		// if there is 1 entry in the table, then it is defined as interfacename=implementationname (where the interface name
		// and the model name are the same)
		// if there are 2 entries in the table, then it is defined as modelname=interfacename and
		// interfacename=implementationname(where the interface name and the model name are not the same)
		//
		// in this case, we are looking for the implementation name. The model name is type.getTypeSignature(). Check to see
		// if there is an entry in the table. If there is, then it either points at the interface name (if there is a 2nd
		// entry), or the implementation name (if there is no 2nd entry)
		String value = nativeTypeMappings.get(type.getClassifier().getTypeSignature());
		if (value != null) {
			// there was an entry, so we need to see if there is a 2nd entry. Return the target of either the 2nd entry (if
			// exists) or the 1st entry as the implementation name
			String impl = nativeTypeMappings.get(value);
			if (impl != null)
				return impl;
			else
				return value;
		} else
			// there was no entry, so default interface name to type signature (model name)
			return type.getClassifier().getTypeSignature();
	}

	public String getNativeInterfaceMapping(Type type) {
		// the nativetypes mapping file works like this:
		// if there are no entries in the table, then the type signature will be used for both interface and implementation
		// names.
		// if there is 1 entry in the table, then it is defined as interfacename=implementationname (where the interface name
		// and the model name are the same)
		// if there are 2 entries in the table, then it is defined as modelname=interfacename and
		// interfacename=implementationname(where the interface name and the model name are not the same)
		//
		// in this case, we are looking for the interface name. The model name is type.getTypeSignature(). Check to see if
		// there is an entry in the table. If there is, then it either points at the interface name (if there is a 2nd
		// entry), or the implementation name (if there is no 2nd entry)
		String value = nativeTypeMappings.get(type.getClassifier().getTypeSignature());
		if (value != null) {
			// there was an entry, so we need to see if there is a 2nd entry. If there is only 1 entry, then return interface
			// name as type signature (model name). If there is a 2nd name, then return interface name from the
			// modelname=interfacename
			if (nativeTypeMappings.get(value) != null)
				return value;
			else
				return type.getClassifier().getTypeSignature();
		} else
			// there was no entry, so default interface name to type signature (model name)
			return type.getClassifier().getTypeSignature();
	}

	public List<String> getTemplateTraceEntries() {
		return templateTraceEntries;
	}

	public Object getParameter(String internalName) {
		Object value = null;
		if (this.get(internalName) != null)
			value = ((CommandParameter) this.get(internalName)).getValue();
		return value;
	}

	public void foreachAnnotation(List<? extends Annotation> list, char separator, String methodName, EglContext ctx, TabbedWriter out, Object... args)
		throws TemplateException {
		for (int i = 0; i < list.size(); i++) {
			this.gen(methodName, list.get(i), ctx, out, args);
			if (i < list.size() - 1) {
				out.print(separator);
				out.print(' ');
			}
		}
	}

	public void foreachType(List<? extends Type> list, char separator, String methodName, EglContext ctx, TabbedWriter out, Object... args)
		throws TemplateException {
		for (int i = 0; i < list.size(); i++) {
			this.gen(methodName, list.get(i), ctx, out, args);
			if (i < list.size() - 1) {
				out.print(separator);
				out.print(' ');
			}
		}
	}

	public void gen(String methodName, Annotation type, EglContext ctx, TabbedWriter out, Object... args) throws TemplateException {
		TemplateMethod templateMethod = getMethodAndTemplate(methodName, type, type.getClass(), ((AnnotationType) type.getEClass()).getTypeSignature(),
			type.getClass(), ctx.getClass(), out.getClass(), args.getClass());
		try {
			templateMethod.getMethod().invoke(templateMethod.getTemplate(), type, ctx, out, args);
		}
		catch (Exception e) {
			throw new TemplateException(e);
		}
	}

	public void gen(String methodName, Classifier part, EglContext ctx, TabbedWriter out, Object... args) throws TemplateException {
		try {
			Stereotype stereotype = part.getStereotype();
			TemplateMethod templateMethod = stereotype != null ? getMethodAndTemplate(methodName, stereotype, part.getClass(), stereotype.getEClass()
				.getETypeSignature(), part.getClass(), ctx.getClass(), out.getClass(), args.getClass()) : getMethodAndTemplate(methodName, part,
				part.getClass(), part.getClassifier().getTypeSignature(), part.getClass(), ctx.getClass(), out.getClass(), args.getClass());
			templateMethod.getMethod().invoke(templateMethod.getTemplate(), part, ctx, out, args);
		}
		catch (TemplateException e) {
			gen(methodName, (EObject) part, ctx, out, args);
		}
		catch (Exception e) {
			throw new TemplateException(e);
		}
	}

	public void gen(String methodName, Type type, EglContext ctx, TabbedWriter out, Object... args) throws TemplateException {
		boolean found = false;
		for (int i = 0; i < args.length; i++) {
			if (args[i] instanceof TypeLogicKind) {
				found = true;
				break;
			}
		}
		Object[] objects = new Object[args.length + (found == true ? 0 : 1)];
		for (int i = 0; i < args.length; i++) {
			objects[i] = args[i];
		}
		if (!found)
			objects[args.length] = TypeLogicKind.Process;
		try {
			if (type instanceof Classifier && ((Classifier) type).getStereotype() != null) {
				// if there is a stereotype, then we need to try it first, and if not found (exception), then try the
				// standard type instead
				try {
					Stereotype stereotype = ((Classifier) type).getStereotype();
					TemplateMethod templateMethod = getMethodAndTemplate(methodName, stereotype, stereotype.getClass(), stereotype.getEClass()
						.getETypeSignature(), type.getClass(), ctx.getClass(), out.getClass(), args.getClass());
					templateMethod.getMethod().invoke(templateMethod.getTemplate(), type, ctx, out, objects);
				}
				catch (TemplateException e) {
					TemplateMethod templateMethod = getMethodAndTemplate(methodName, type, type.getClass(), type.getClassifier().getTypeSignature(),
						type.getClass(), ctx.getClass(), out.getClass(), args.getClass());
					templateMethod.getMethod().invoke(templateMethod.getTemplate(), type, ctx, out, objects);
				}
			} else {
				TemplateMethod templateMethod = getMethodAndTemplate(methodName, type, type.getClass(), type.getClassifier().getTypeSignature(),
					type.getClass(), ctx.getClass(), out.getClass(), args.getClass());
				templateMethod.getMethod().invoke(templateMethod.getTemplate(), type, ctx, out, objects);
			}
		}
		catch (TemplateException e) {
			Object[] objectsExtended = new Object[objects.length + 1];
			for (int i = 0; i < objects.length; i++) {
				objectsExtended[i] = objects[i];
			}
			objectsExtended[objects.length] = type;
			if (type instanceof EGLClass && ((EGLClass) type).getSuperTypes() != null && ((EGLClass) type).getSuperTypes().size() > 0)
				gen(methodName, (Type) ((EGLClass) type).getSuperTypes().get(0), ctx, out, objectsExtended);
			else
				gen(methodName, (EObject) type, ctx, out, objectsExtended);
		}
		catch (Exception e) {
			throw new TemplateException(e);
		}
	}

	public void gen(String methodName, EObject object, TemplateContext ctx, TabbedWriter out, Object... args) throws TemplateException {
		TemplateMethod templateMethod = getMethodAndTemplate(methodName, object, object.getClass(), object.getEClass().getETypeSignature(), object.getClass(),
			ctx.getClass(), out.getClass(), args.getClass());
		try {
			templateMethod.getMethod().invoke(templateMethod.getTemplate(), object, ctx, out, args);
		}
		catch (Exception e) {
			throw new TemplateException(e);
		}
	}

	public void gen(String methodName, Object object, TemplateContext ctx, TabbedWriter out, Object... args) throws TemplateException {
		TemplateMethod templateMethod = getMethodAndTemplate(methodName, object, object.getClass(), object.getClass().getName(), object.getClass(),
			ctx.getClass(), out.getClass(), args.getClass());
		try {
			templateMethod.getMethod().invoke(templateMethod.getTemplate(), object, ctx, out, args);
		}
		catch (Exception e) {
			throw new TemplateException(e);
		}
	}

	public void genSuper(String methodName, Class<?> thisClass, EObject object, TemplateContext ctx, TabbedWriter out, Object... args) throws TemplateException {
		TemplateMethod templateMethod = getSuperMethodAndTemplate(methodName, thisClass, object.getClass(), ctx.getClass(), out.getClass(), args.getClass());
		try {
			templateMethod.getMethod().invoke(templateMethod.getTemplate(), object, ctx, out, args);
		}
		catch (Exception e) {
			throw new TemplateException(e);
		}
	}

	@SuppressWarnings("unchecked")
	public List<Object> xlate(String methodName, Annotation type, EglContext ctx, Object... args) throws TemplateException {
		TemplateMethod templateMethod = getMethodAndTemplate(methodName, type, type.getClass(), ((AnnotationType) type.getEClass()).getTypeSignature(),
			type.getClass(), ctx.getClass(), args.getClass());
		try {
			return (List<Object>) templateMethod.getMethod().invoke(templateMethod.getTemplate(), type, ctx, args);
		}
		catch (Exception e) {
			throw new TemplateException(e);
		}
	}

	@SuppressWarnings("unchecked")
	public List<Object> xlate(String methodName, Type type, EglContext ctx, Object... args) throws TemplateException {
		TemplateMethod templateMethod = getMethodAndTemplate(methodName, type, type.getClass(), type.getClassifier().getTypeSignature(), type.getClass(),
			ctx.getClass(), args.getClass());
		try {
			return (List<Object>) templateMethod.getMethod().invoke(templateMethod.getTemplate(), type, ctx, args);
		}
		catch (Exception e) {
			throw new TemplateException(e);
		}
	}

	@SuppressWarnings("unchecked")
	public List<Object> xlate(String methodName, EObject object, TemplateContext ctx, Object... args) throws TemplateException {
		TemplateMethod templateMethod = getMethodAndTemplate(methodName, object, object.getClass(), object.getEClass().getETypeSignature(), object.getClass(),
			ctx.getClass(), args.getClass());
		try {
			return (List<Object>) templateMethod.getMethod().invoke(templateMethod.getTemplate(), object, ctx, args);
		}
		catch (Exception e) {
			throw new TemplateException(e);
		}
	}

	public void validate(String methodName, Annotation annotation, EglContext ctx, Object... args) throws TemplateException {
		try {
			TemplateMethod templateMethod = getMethodAndTemplate(methodName, annotation, annotation.getClass(),
				((AnnotationType) annotation.getEClass()).getTypeSignature(), annotation.getClass(), ctx.getClass(), args.getClass());
			templateMethod.getMethod().invoke(templateMethod.getTemplate(), annotation, ctx, args);
		}
		catch (TemplateException e) {
			handleValidationError(annotation);
		}
		catch (Exception e) {
			throw new TemplateException(e);
		}
	}

	public void validate(String methodName, Element object, EglContext ctx, Object... args) throws TemplateException {
		try {
			validate(methodName, (EObject) object, ctx, args);
		}
		catch (TemplateException ex) {
			handleValidationError(object);
		}
		catch (Exception e) {
			throw new TemplateException(e);
		}
	}

	public void validate(String methodName, Classifier part, EglContext ctx, Object... args) throws TemplateException {
		try {
			Stereotype stereotype = part.getStereotype();
			TemplateMethod templateMethod = stereotype != null ? getMethodAndTemplate(methodName, stereotype, part.getClass(), stereotype.getEClass()
				.getETypeSignature(), part.getClass(), ctx.getClass(), args.getClass()) : getMethodAndTemplate(methodName, part, part.getClass(), part
				.getClassifier().getTypeSignature(), part.getClass(), ctx.getClass(), args.getClass());
			templateMethod.getMethod().invoke(templateMethod.getTemplate(), part, ctx, args);
		}
		catch (TemplateException e) {
			validate(methodName, (EObject) part, ctx, args);
		}
		catch (Exception e) {
			throw new TemplateException(e);
		}
	}

	public void validate(String methodName, Type type, EglContext ctx, Object... args) throws TemplateException {
		try {
			if (type instanceof Classifier && ((Classifier) type).getStereotype() != null) {
				// if there is a stereotype, then we need to try it first, and if not found (exception), then try the
				// standard type instead
				try {
					Stereotype stereotype = ((Classifier) type).getStereotype();
					TemplateMethod templateMethod = getMethodAndTemplate(methodName, stereotype, stereotype.getClass(), stereotype.getEClass()
						.getETypeSignature(), type.getClass(), ctx.getClass(), args.getClass());
					templateMethod.getMethod().invoke(templateMethod.getTemplate(), type, ctx, args);
				}
				catch (TemplateException e) {
					TemplateMethod templateMethod = getMethodAndTemplate(methodName, type, type.getClass(), type.getClassifier().getTypeSignature(),
						type.getClass(), ctx.getClass(), args.getClass());
					templateMethod.getMethod().invoke(templateMethod.getTemplate(), type, ctx, args);
				}
			} else {
				TemplateMethod templateMethod = getMethodAndTemplate(methodName, type, type.getClass(), type.getClassifier().getTypeSignature(),
					type.getClass(), ctx.getClass(), args.getClass());
				templateMethod.getMethod().invoke(templateMethod.getTemplate(), type, ctx, args);
			}
		}
		catch (TemplateException e) {
			if (type instanceof EGLClass && ((EGLClass) type).getSuperTypes() != null && ((EGLClass) type).getSuperTypes().size() > 0) {
				Object[] objects = new Object[args.length + 1];
				for (int i = 0; i < args.length; i++) {
					objects[i] = args[i];
				}
				objects[args.length] = type;
				validate(methodName, (Type) ((EGLClass) type).getSuperTypes().get(0), ctx, objects);
			} else {
				Object[] objects = new Object[args.length + 1];
				for (int i = 0; i < args.length; i++) {
					objects[i] = args[i];
				}
				objects[args.length] = type;
				validate(methodName, (EObject) type, ctx, objects);
			}
		}
		catch (Exception e) {
			throw new TemplateException(e);
		}
	}

	public void validate(String methodName, EObject object, TemplateContext ctx, Object... args) throws TemplateException {
		TemplateMethod templateMethod = getMethodAndTemplate(methodName, object, object.getClass(), object.getEClass().getETypeSignature(), object.getClass(),
			ctx.getClass(), args.getClass());
		try {
			templateMethod.getMethod().invoke(templateMethod.getTemplate(), object, ctx, args);
		}
		catch (Exception e) {
			throw new TemplateException(e);
		}
	}

	public void validate(String methodName, Object object, TemplateContext ctx, Object... args) throws TemplateException {
		TemplateMethod templateMethod = getMethodAndTemplate(methodName, object, object.getClass(), object.getClass().getName(), object.getClass(),
			ctx.getClass(), args.getClass());
		try {
			templateMethod.getMethod().invoke(templateMethod.getTemplate(), object, ctx, args);
		}
		catch (Exception e) {
			throw new TemplateException(e);
		}
	}

	public void validateSuper(String methodName, Class<?> thisClass, EObject object, TemplateContext ctx, Object... args) throws TemplateException {
		TemplateMethod templateMethod = getSuperMethodAndTemplate(methodName, thisClass, object.getClass(), ctx.getClass(), args.getClass());
		try {
			templateMethod.getMethod().invoke(templateMethod.getTemplate(), object, ctx, args);
		}
		catch (Exception e) {
			throw new TemplateException(e);
		}
	}

	// these methods allows you to add annotation data to any object. we cannot alter the IR's by adding annotations, so this
	// allows us to collect data associated with any object and maintain that through the generation
	@SuppressWarnings("unchecked")
	public Object getAttribute(Object key, String value) {
		// check to see if the key exists. if it does, then it will be associated with a list object. that list object will
		// be searched for the value. if found, then that value is returned
		List<Annotation> list = (List<Annotation>) this.get(key);
		if (list == null)
			return null;
		// we need to search the list of annotations looking for the string value, then return the associated data value
		for (int i = 0; i < list.size(); i++) {
			Annotation annotation = list.get(i);
			if (value.equalsIgnoreCase(annotation.getEClass().getName()))
				return annotation.getValue();
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	public void putAttribute(Object key, String value, Object entry) {
		// check to see if the key exists. if this doesn't, then create the list object
		List<Annotation> list = (List<Annotation>) this.get(key);
		if (list == null) {
			list = new ArrayList<Annotation>();
			this.put(key, list);
		}
		// create the annotation
		Annotation annotation = factory.createAnnotation(value);
		annotation.setValue(entry);
		// add the annotation to the list
		list.add(annotation);
	}

	public Map<String, String> load(String fileList, ClassLoader loader) {
		Map<String, String> map = new HashMap<String, String>();
		// the fileList will be 1 or more locations for the properties files involved with this implementation. If more than
		// 1 location is in the list, then it will be separated by a semi-colon. We need to split these out into individual
		// locations and process them in order.
		String[] files = fileList.split("[;]");
		for (String file : files) {
			// process this property file
			ResourceBundle bundle = ResourceBundle.getBundle(file, Locale.getDefault(), loader);
			Enumeration<String> keys = bundle.getKeys();
			while (keys.hasMoreElements()) {
				String key = keys.nextElement();
				String data = bundle.getString(key);
				// if this entry is already in the map, don't replace it. This keeps the list such that the 1st
				// entry that we come across remains as the entry to use.
				if (map.get(key) == null)
					map.put(key, data);
			}
		}
		return map;
	}

	public abstract void handleValidationError(Element ex);

	public abstract void handleValidationError(Annotation ex);

	public abstract void handleValidationError(Type ex);

	private TemplateMethod getMethodAndTemplate(String methodName, Object object, Class<?> ifaceClass, String signature, Class<?>... classes)
		throws TemplateException {
		String hashKey = methodName + "/" + signature + "/" + ifaceClass.getName();
		TemplateMethod templateMethod = templateMethods.get(hashKey);
		addMethodTrace("gen for method name: " + methodName + " with signature: " + signature + " for argument: " + ifaceClass.getName());
		if (templateMethod != null) {
			addMethodTrace("Found in hash table, no lookup required. Template: " + templateMethod.getTemplate().toString());
			return templateMethod;
		}
		// see if we can find the method directly
		templateMethod = findSupertypeMethodAndTemplate(methodName, object, signature, classes);
		if (templateMethod != null) {
			templateMethods.put(hashKey, templateMethod);
			addMethodTrace("Found directly in template: " + templateMethod.getTemplate().toString());
			return templateMethod;
		}
		// we need to look un the interface chain
		addMethodTrace("Not found directly, doing lookup");
		templateMethod = findInterfaceMethodAndTemplate(methodName, ifaceClass, classes);
		if (templateMethod != null) {
			templateMethods.put(hashKey, templateMethod);
			addMethodTrace("Found from lookup in template: " + templateMethod.getTemplate().toString());
			return templateMethod;
		} else {
			addMethodTrace("Not found from lookup");
			throw new TemplateException(methodName);
		}
	}

	private TemplateMethod findSupertypeMethodAndTemplate(String methodName, Object object, String signature, Class<?>... classes) throws TemplateException {
		Class<?> ifaceClass = classes[0];
		// we might need to look up the external type chain, before checking the interface classes. This is because external
		// types might extend other external types.
		try {
			Template template = getTemplate(signature);
			Method method = template.getMethod(methodName, true, classes);
			if (method != null)
				return new TemplateMethod(template, method);
		}
		catch (TemplateException e) {}
		classes[0] = ifaceClass;
		if (object instanceof StructPart) {
			for (StructPart iface : ((StructPart) object).getSuperTypes()) {
				TemplateMethod templateMethod = findSupertypeMethodAndTemplate(methodName, iface, iface.getFullyQualifiedName(), classes);
				if (templateMethod != null)
					return templateMethod;
			}
		}
		return null;
	}

	private TemplateMethod getSuperMethodAndTemplate(String methodName, Class<?> thisClass, Class<?>... classes) throws TemplateException {
		addMethodTrace("genSuper lookup for method name: " + methodName + " with class: " + thisClass.getName());
		TemplateMethod templateMethod = findInterfaceMethodAndTemplate(methodName, thisClass, classes);
		if (templateMethod != null) {
			addMethodTrace("Found from lookup in template: " + templateMethod.getTemplate().toString());
			return templateMethod;
		} else {
			addMethodTrace("Not found from lookup");
			throw new TemplateException(methodName);
		}
	}

	private TemplateMethod findInterfaceMethodAndTemplate(String methodName, Class<?> ifaceClass, Class<?>... classes) throws TemplateException {
		for (Class<?> iface : ifaceClass.getInterfaces()) {
			try {
				addMethodTrace("Looking for method in: " + iface.getName());
				Template template = getTemplateFor(iface);
				classes[0] = iface;
				Method method = template.getMethod(methodName, true, classes);
				if (method != null)
					return new TemplateMethod(template, method);
			}
			catch (TemplateException e) {}
		}
		for (Class<?> iface : ifaceClass.getInterfaces()) {
			classes[0] = iface;
			TemplateMethod templateMethod = findInterfaceMethodAndTemplate(methodName, iface, classes);
			if (templateMethod != null)
				return templateMethod;
		}
		return null;
	}

	private void addMethodTrace(String entry) {
		if ((Boolean) getParameter(Constants.parameter_trace))
			System.out.println(entry);
		else {
			while (templateTraceEntries.size() >= 200)
				templateTraceEntries.remove(0);
			templateTraceEntries.add(entry);
		}
	}
}
