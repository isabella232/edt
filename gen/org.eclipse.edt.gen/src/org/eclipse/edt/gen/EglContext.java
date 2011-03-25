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
import org.eclipse.edt.mof.egl.EGLClass;
import org.eclipse.edt.mof.egl.Element;
import org.eclipse.edt.mof.egl.IrFactory;
import org.eclipse.edt.mof.egl.Part;
import org.eclipse.edt.mof.egl.Stereotype;
import org.eclipse.edt.mof.egl.TryStatement;
import org.eclipse.edt.mof.egl.Type;

@SuppressWarnings("serial")
public abstract class EglContext extends TemplateContext {

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

	// the message requestor
	private IGenerationMessageRequestor messageRequestor;

	// This is used by nextTempName.
	private int tempIndex;

	public EglContext(AbstractGeneratorCommand processor) {
		factory = IrFactory.INSTANCE;
		tryStack = new Stack<TryStatement>();
		labelStack = new Stack<Label>();
		messageRequestor = new AccumulatingGenerationMessageRequestor();

		ClassLoader loader = processor.getClass().getClassLoader();
		nativeTypeMappings = load(processor.getNativeTypes(), loader);
		primitiveTypeMappings = load(processor.getPrimitiveTypes(), loader);
		messageMappings = load(processor.getEGLMessages(), loader);
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

	public Object getParameter(String internalName) {
		Object value = null;
		if (this.get(internalName) != null)
			value = ((CommandParameter) this.get(internalName)).getValue();
		return value;
	}

	public void foreachAnnotation(List<? extends Annotation> list, char separator, String genMethod, EglContext ctx, TabbedWriter out, Object... args)
		throws TemplateException {
		for (int i = 0; i < list.size(); i++) {
			this.gen(genMethod, list.get(i), ctx, out, args);
			if (i < list.size() - 1) {
				out.print(separator);
				out.print(' ');
			}
		}
	}

	public void foreachType(List<? extends Type> list, char separator, String genMethod, EglContext ctx, TabbedWriter out, Object... args)
		throws TemplateException {
		for (int i = 0; i < list.size(); i++) {
			this.gen(genMethod, list.get(i), ctx, out, args);
			if (i < list.size() - 1) {
				out.print(separator);
				out.print(' ');
			}
		}
	}

	public void gen(String methodName, Annotation type, EglContext ctx, TabbedWriter out, Object... args) throws TemplateException {
		Template template = ctx.getTemplate(((AnnotationType) type.getEClass()).getTypeSignature());
		template.gen(methodName, type, ctx, out, args);
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
		Template template = null;
		try {
			template = ctx.getTemplate(type.getClassifier().getTypeSignature());
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
		if (template != null)
			template.gen(methodName, type, ctx, out, objects);
	}

	public void gen(String methodName, Part part, EglContext ctx, TabbedWriter out, Object... args) throws TemplateException {
		try {
			Stereotype stereotype = part.getStereotype();
			Template template = stereotype != null ? ctx.getTemplate(stereotype.getEClass().getETypeSignature()) : ctx.getTemplate(part.getClassifier()
				.getTypeSignature());
			template.gen(methodName, part, ctx, out, args);
		}
		catch (TemplateException e) {
			gen(methodName, (EObject) part, ctx, out, args);
		}
	}

	public List<Object> xlate(String methodName, Annotation type, EglContext ctx, Object... args) throws TemplateException {
		Template template = ctx.getTemplate(((AnnotationType) type.getEClass()).getTypeSignature());
		return template.xlate(methodName, type, ctx, args);
	}

	public List<Object> xlate(String methodName, Type type, EglContext ctx, Object... args) throws TemplateException {
		Template template = ctx.getTemplate(type.getClassifier().getTypeSignature());
		return template.xlate(methodName, type, ctx, args);
	}

	public void validate(String methodName, Element object, EglContext ctx, Object... args) throws TemplateException {
		try {
			super.validate(methodName, object, ctx, args);
		}
		catch (TemplateException ex) {
			handleValidationError(object);
		}
	}

	public void validate(String methodName, Annotation annotation, EglContext ctx, Object... args) throws TemplateException {
		Template template = null;
		try {
			template = ctx.getTemplate(((AnnotationType) annotation.getEClass()).getTypeSignature());
		}
		catch (TemplateException e) {
			handleValidationError(annotation);
		}
		if (template != null)
			template.validate(methodName, annotation, ctx, args);
	}

	public void validate(String methodName, Type type, EglContext ctx, Object... args) throws TemplateException {
		Template template = null;
		try {
			template = ctx.getTemplate(type.getClassifier().getTypeSignature());
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
		if (template != null)
			template.validate(methodName, type, ctx, args);
	}

	public void validate(String methodName, Part part, EglContext ctx, Object... args) throws TemplateException {
		Template template = null;
		try {
			Stereotype stereotype = part.getStereotype();
			template = stereotype != null ? ctx.getTemplate(stereotype.getEClass().getETypeSignature()) : ctx.getTemplate(part.getClassifier()
				.getTypeSignature());
		}
		catch (TemplateException e) {
			validate(methodName, (EObject) part, ctx, args);
		}
		if (template != null)
			template.validate(methodName, part, ctx, args);
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
}
