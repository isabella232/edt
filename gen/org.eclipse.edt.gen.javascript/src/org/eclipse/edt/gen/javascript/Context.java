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
package org.eclipse.edt.gen.javascript;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.edt.compiler.core.IEGLConstants;
import org.eclipse.edt.gen.AbstractGeneratorCommand;
import org.eclipse.edt.gen.EglContext;
import org.eclipse.edt.gen.EGLMessages.EGLMessage;
import org.eclipse.edt.mof.egl.Annotation;
import org.eclipse.edt.mof.egl.Element;
import org.eclipse.edt.mof.egl.Function;
import org.eclipse.edt.mof.egl.Type;

public class Context extends EglContext {

	private static final long serialVersionUID = 6429116299734843162L;

	private Function currentFunction;
	private Map<String, String> namespaceMap = new HashMap<String, String>();

	public Context(AbstractGeneratorCommand processor) {
		super(processor);
	}

	public Function getCurrentFunction() {
		return currentFunction;
	}

	public void setCurrentFunction(Function currentFunction) {
		this.currentFunction = currentFunction;
	}

	public String getRawPrimitiveMapping(String item) {
		return super.getPrimitiveMapping(item);
	}

	public String getRawPrimitiveMapping(Type type) {
		return super.getPrimitiveMapping(type);
	}

	public String getRawNativeImplementationMapping(Type type) {
		return super.getNativeImplementationMapping(type);
	}

	public String getRawNativeInterfaceMapping(Type type) {
		return super.getNativeInterfaceMapping(type);
	}

	public String getNativeImplementationMapping(Type type) {
		return Constants.JSRT_EGL_NAMESPACE + super.getNativeImplementationMapping(type);
	}

	public String getNativeTypeName(Type type) {
		String typeName = getNativeImplementationMapping(type);
		return typeName.substring(typeName.lastIndexOf(".") + 1);
	}

	public void handleValidationError(Element obj) {
		String[] details = new String[] { obj.getEClass().getETypeSignature() };
		EGLMessage message = EGLMessage.createEGLMessage(getMessageMapping(), EGLMessage.EGL_ERROR_MESSAGE, Constants.EGLMESSAGE_MISSING_TEMPLATE_FOR_OBJECT,
			obj, details, obj.getAnnotation(IEGLConstants.EGL_LOCATION));
		getMessageRequestor().addMessage(message);
	}

	public void handleValidationError(Annotation obj) {
		String[] details = new String[] { obj.getEClass().getETypeSignature() };
		EGLMessage message = EGLMessage.createEGLMessage(getMessageMapping(), EGLMessage.EGL_ERROR_MESSAGE,
			Constants.EGLMESSAGE_MISSING_TEMPLATE_FOR_ANNOTATION, obj, details, getLastStatementLocation());
		getMessageRequestor().addMessage(message);
	}

	public void handleValidationError(Type obj) {
		String[] details = new String[] { obj.getEClass().getETypeSignature() };
		EGLMessage message = EGLMessage.createEGLMessage(getMessageMapping(), EGLMessage.EGL_ERROR_MESSAGE, Constants.EGLMESSAGE_MISSING_TEMPLATE_FOR_TYPE,
			obj, details, getLastStatementLocation());
		getMessageRequestor().addMessage(message);
	}

	public void addNamespace(String namespace, String localName, String qualifiedPart) {
		namespaceMap.put(namespace + '{' + localName + '}', qualifiedPart);
	}

	public Map<String, String> getNamespaceMap() {
		return namespaceMap;
	}
	/*
	 * TODO sbg need to revisit support for EGL src line numbers public void gen(String methodName, Annotation type,
	 * EglContext ctx, TabbedWriter out) throws TemplateException { updateEGLLocation(type, out);
	 * super.gen(methodName, type, ctx, out, args); } public void gen(String methodName, Classifier part, EglContext ctx,
	 * TabbedWriter out) throws TemplateException { updateEGLLocation(part, out); super.gen(methodName, part,
	 * ctx, out, args); } public void gen(String methodName, EObject object, TemplateContext ctx, TabbedWriter out, Object...
	 * args) throws TemplateException { if (object instanceof Element){ updateEGLLocation((Element)object, out); }
	 * super.gen(methodName, object, ctx, out, args); } public void gen(String methodName, Object object, TemplateContext
	 * ctx, TabbedWriter out) throws TemplateException { if (object instanceof Element){
	 * updateEGLLocation((Element)object, out); } super.gen(methodName, object, ctx, out, args); } public void gen(String
	 * methodName, Type type, EglContext ctx, TabbedWriter out) throws TemplateException {
	 * updateEGLLocation(type, out); super.gen(methodName, type, ctx, out, args); } private void updateEGLLocation(Element
	 * element, TabbedWriter out) { if (out instanceof TabbedReportWriter){ for (Annotation ann : element.getAnnotations()) {
	 * if ("EGL_Location".equals(ann.getEClass().getName())) { // ((TabbedReportWriter)out).location = ann; break; } } } }
	 */
}
