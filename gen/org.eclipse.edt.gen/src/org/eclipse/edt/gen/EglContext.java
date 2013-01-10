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
package org.eclipse.edt.gen;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.Stack;

import org.eclipse.edt.compiler.internal.interfaces.IGenerationMessageRequestor;
import org.eclipse.edt.gen.EGLMessages.AccumulatingGenerationMessageRequestor;
import org.eclipse.edt.mof.EObject;
import org.eclipse.edt.mof.codegen.api.TabbedWriter;
import org.eclipse.edt.mof.codegen.api.Template;
import org.eclipse.edt.mof.codegen.api.TemplateContext;
import org.eclipse.edt.mof.codegen.api.TemplateException;
import org.eclipse.edt.mof.egl.Annotation;
import org.eclipse.edt.mof.egl.Classifier;
import org.eclipse.edt.mof.egl.Element;
import org.eclipse.edt.mof.egl.IrFactory;
import org.eclipse.edt.mof.egl.Stereotype;
import org.eclipse.edt.mof.egl.StructPart;
import org.eclipse.edt.mof.egl.TryStatement;
import org.eclipse.edt.mof.egl.Type;
import org.eclipse.edt.mof.egl.utils.TypeUtils;

@SuppressWarnings("serial")
public abstract class EglContext extends TemplateContext {

	private IrFactory factory;

	// this is the nesting of exit/continue labels for various statements
	private Stack<Label> labelStack;

	// This is the nesting of try statements.
	private Stack<TryStatement> tryStack;

	// this is the user overrideable set of properties files values
	private Map<String, String> primitiveTypeMappings;
	private Map<String, String> nativeTypeMappings;
	private Map<String, String> messageMappings;
	private List<String> supportedPartTypes;
	private List<String> supportedStereotypes;

	// the message requestor
	private IGenerationMessageRequestor messageRequestor;

	// This is used by nextTempName.
	private int tempIndex;

	private Annotation lastStatementLocation;
	
	private final Set<String> requiredRuntimeContainers = new HashSet<String>();

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
		supportedPartTypes = processor.getSupportedPartTypes();
		supportedStereotypes = processor.getSupportedStereotypes();
	}

	/**
	 * Returns the factory for creating Egl model element instances.
	 * @return the IrFactory
	 */
	public IrFactory getFactory() {
		return factory;
	}

	/**
	 * @param String The exit and continue statement labels we expect to be pushing on the stack
	 */
	public void pushLabelStack(Label label) {
		this.labelStack.push(label);
	}

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
	 * try and locate the first entry beginning with the passed string and return the value or null
	 */
	public Label searchLabelStack(int type, int ignore) {
		int i = this.labelStack.size();
		// we are interested in scanning lifo for the entries
		while (i > 0) {
			Label label = this.labelStack.get(--i);
			// are we trying to match the nearest generic, ignoring a specific type
			if (type == Label.LABEL_TYPE_GENERIC && label.getType() != ignore)
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

	/**
	 * Returns a String that can be used as the name of a temporary variable. It will be "eze$LNNTemp" plus a number. The number
	 * is 1 the first time this method is called, then 2, then 3, and so on.
	 */
	public String nextLogicallyNotNullableTempName() {
		return Constants.temporaryVariableLogicallyNotNullablePrefix + nextTempIndex();
	}

	public IGenerationMessageRequestor getMessageRequestor() {
		return messageRequestor;
	}

	public void setMessageRequestor(IGenerationMessageRequestor requestor) {
		this.messageRequestor = requestor;
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

	public void foreachAnnotation(List<? extends Annotation> list, char separator, String methodName, EglContext ctx, TabbedWriter out, Object... args)
		throws TemplateException {
		for (int i = 0; i < list.size(); i++) {
			this.invoke(methodName, list.get(i), ctx, out, args);
			if (i < list.size() - 1) {
				out.print(separator);
				out.print(' ');
			}
		}
	}

	public void foreachType(List<? extends Type> list, char separator, String methodName, EglContext ctx, TabbedWriter out, Object... args)
		throws TemplateException {
		for (int i = 0; i < list.size(); i++) {
			this.invoke(methodName, list.get(i), ctx, out, args);
			if (i < list.size() - 1) {
				out.print(separator);
				out.print(' ');
			}
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
		Annotation annotation = findAnnotation(value, list);
		if (annotation != null) {
			return annotation.getValue();
		} else {
			return null;
		}
	}

	private Annotation findAnnotation(String value, List<Annotation> list) {
		// we need to search the list of annotations looking for the string value, then return the associated data value
		for (int i = 0; i < list.size(); i++) {
			Annotation annotation = list.get(i);
			if (value.equalsIgnoreCase(annotation.getEClass().getName()))
				return annotation;
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
		Annotation annotation = findAnnotation(value, list);
		if (annotation == null) {
			annotation = factory.createAnnotation(value);
			// add the annotation to the list
			list.add(annotation);
		}
		annotation.setValue(entry);
	}

	public Map<String, String> load(String fileList, ClassLoader loader) {
		Map<String, String> map = new HashMap<String, String>();
		// the fileList will be 1 or more locations for the properties files involved with this implementation. If more than
		// 1 location is in the list, then it will be separated by a semi-colon. We need to split these out into individual
		// locations and process them in order.
		String[] files = fileList.length() == 0 ? new String[0] : fileList.split("[;]");
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

	public Object invoke(String methodName, Type type, Object... args) {
		TemplateMethod tm = getTemplateMethod(methodName, type, args);
		if (tm == null) {
			tm = getTemplateMethod(methodName, type.getEClass(), args);
		}
		if (tm != null) {
			return doInvoke(tm.getMethod(), tm.getTemplate(), type, args);
		} else {
			return invoke(methodName, (Object) type, args);
		}
	}

	public Object invokeSuper(Template template, String methodName, Type type, Object... args) {
		// Get Classifier associated with the given template
		Classifier classifier = getClassifierForTemplate(template);
		StructPart superType = null;
		if (classifier instanceof StructPart && !((StructPart) classifier).getSuperTypes().isEmpty()) {
			superType = ((StructPart) classifier).getSuperTypes().get(0);
		}
		if (superType == null) {
			return invokeSuper(template, methodName, (EObject) type, args);
		}
		TemplateMethod tm = getTemplateMethod(methodName, superType, args);
		// if the method points back at itself, we have a recursive loop. In that case type 1 more level of supertype
		if (tm != null && tm.getTemplate().getClass().toString().equals(template.getClass().toString()))
			tm = getTemplateMethod(methodName, ((StructPart) superType.getClassifier()).getSuperTypes().get(0), args);
		if (tm != null) {
			return doInvoke(tm.getMethod(), tm.getTemplate(), type, args);
		} else {
			return invokeSuper(template, methodName, (EObject) type, args);
		}

	}

	public TemplateMethod getTemplateMethod(String methodName, Type type, Object... args) throws TemplateException {
		TemplateMethod tm = null;
		Method method = null;
		Template template = null;
		template = getTemplateForClassifier(type.getClassifier());
		// If no template found try Stereotype based lookup
		if (template == null) {
			Stereotype stereotype = type.getClassifier().getStereotype();
			if (stereotype != null) {
				template = getTemplateForEClassifier(stereotype.getEClass());
			}
		}
		if (template == null && type instanceof StructPart) {
			for (StructPart part : ((StructPart) type).getSuperTypes()) {
				tm = getTemplateMethod(methodName, part, args);
				if (tm != null)
					break;
			}
		} else if (template != null) {
			Class<?> classifierClass = type.getClass();
			method = primGetMethod(methodName, template.getClass(), classifierClass, args);
			if (method != null) {
				return new TemplateMethod(template, method);
			}
			if (tm == null && type instanceof StructPart) {
				for (StructPart part : ((StructPart) type).getSuperTypes()) {
					tm = getTemplateMethod(methodName, part, args);
					if (tm != null)
						break;
				}
			}
		}
		return tm;
	}

	public Classifier getClassifierForTemplate(Template template) throws TemplateException {
		Classifier result = null;
		String signature = getTemplateKey(template);
		result = (Classifier) TypeUtils.getEGLType(signature);
		return result;
	}

	public Template getTemplateForClassifier(Classifier clazz) {
		return getTemplateRaw(clazz.getTypeSignature());
	}

	public Annotation getLastStatementLocation() {
		return lastStatementLocation;
	}

	public void setLastStatementLocation(Annotation lastStatementLocation) {
		this.lastStatementLocation = lastStatementLocation;
	}
	
	/**
	 * Adds a runtime container to be added to the build path when generation is finished.
	 * @param id  The runtime container id to be added.
	 */
	public void requireRuntimeContainer(String id) {
		requiredRuntimeContainers.add(id);
	}
	
	public Set<String> getRequiredRuntimeContainers() {
		return requiredRuntimeContainers;
	}

	public List<String> getSupportedPartTypes() {
		return supportedPartTypes;
	}

	public List<String> getSupportedStereotypes() {
		return supportedStereotypes;
	}
	
}
