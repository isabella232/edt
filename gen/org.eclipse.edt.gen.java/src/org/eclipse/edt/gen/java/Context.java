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
package org.eclipse.edt.gen.java;

import java.util.List;

import org.eclipse.edt.compiler.core.IEGLConstants;
import org.eclipse.edt.gen.AbstractGeneratorCommand;
import org.eclipse.edt.gen.EglContext;
import org.eclipse.edt.gen.EGLMessages.EGLMessage;
import org.eclipse.edt.mof.codegen.api.TabbedWriter;
import org.eclipse.edt.mof.codegen.api.TemplateContext;
import org.eclipse.edt.mof.egl.Annotation;
import org.eclipse.edt.mof.egl.Element;
import org.eclipse.edt.mof.egl.Expression;
import org.eclipse.edt.mof.egl.Statement;
import org.eclipse.edt.mof.egl.Type;

public class Context extends EglContext {

	private static final long serialVersionUID = 6429116299734843162L;

	private boolean debugIsProcessing;
	private boolean debugHasOutstandingLine;
	private int firstEglLineNumber;
	private int lastEglLineNumber;
	private int firstJavaLineNumber;
	private int lastJavaLineNumber;

	private String currentFunction;
	private StringBuffer debugData = new StringBuffer();
	private StringBuffer debugExtension = new StringBuffer();

	public Context(AbstractGeneratorCommand processor) {
		super(processor);
		debugData.append(Constants.smap_header);
		debugExtension.append("");
	}

	public String getCurrentFunction() {
		return currentFunction;
	}

	public void setCurrentFunction(String currentFunction) {
		this.currentFunction = currentFunction;
	}

	public StringBuffer getDebugData() {
		return debugData;
	}

	public StringBuffer getDebugExtension() {
		return debugExtension;
	}

	public String getRawPrimitiveMapping(String item) {
		return super.getPrimitiveMapping(item);
	}

	@SuppressWarnings("unchecked")
	public String getPrimitiveMapping(String item) {
		String value = super.getPrimitiveMapping(item);
		if (value != null) {
			// check to see if this is in the list of imported types. If it is, then we can use the short name.
			List<String> typesImported = (List<String>) this.getAttribute(this.getClass(), Constants.Annotation_partTypesImported);
			for (String imported : typesImported) {
				if (value.equalsIgnoreCase(imported)) {
					// it was is the table, so use the short name
					if (value.indexOf('.') >= 0)
						value = value.substring(value.lastIndexOf('.') + 1);
					break;
				}
			}
		}
		return value;
	}

	public String getRawPrimitiveMapping(Type type) {
		return super.getPrimitiveMapping(type);
	}

	@SuppressWarnings("unchecked")
	public String getPrimitiveMapping(Type type) {
		String value = super.getPrimitiveMapping(type);
		if (value != null) {
			// check to see if this is in the list of imported types. If it is, then we can use the short name.
			List<String> typesImported = (List<String>) this.getAttribute(this.getClass(), Constants.Annotation_partTypesImported);
			for (String imported : typesImported) {
				if (value.equalsIgnoreCase(imported)) {
					// it was is the table, so use the short name
					if (value.indexOf('.') >= 0)
						value = value.substring(value.lastIndexOf('.') + 1);
					break;
				}
			}
		}
		return value;
	}

	public String getRawNativeImplementationMapping(Type type) {
		return super.getNativeImplementationMapping(type);
	}

	@SuppressWarnings("unchecked")
	public String getNativeImplementationMapping(Type type) {
		String value = super.getNativeImplementationMapping(type);
		// check to see if this is in the list of imported types. If it is, then we can use the short name.
		List<String> typesImported = (List<String>) this.getAttribute(this.getClass(), Constants.Annotation_partTypesImported);
		for (String imported : typesImported) {
			if (value.equalsIgnoreCase(imported)) {
				// it was is the table, so use the short name
				if (value.indexOf('.') >= 0)
					value = value.substring(value.lastIndexOf('.') + 1);
				break;
			}
		}
		return value;
	}

	public String getRawNativeInterfaceMapping(Type type) {
		return super.getNativeInterfaceMapping(type);
	}

	@SuppressWarnings("unchecked")
	public String getNativeInterfaceMapping(Type type) {
		String value = super.getNativeInterfaceMapping(type);
		// check to see if this is in the list of imported types. If it is, then we can use the short name.
		List<String> typesImported = (List<String>) this.getAttribute(this.getClass(), Constants.Annotation_partTypesImported);
		for (String imported : typesImported) {
			if (value.equalsIgnoreCase(imported)) {
				// it was is the table, so use the short name
				if (value.indexOf('.') >= 0)
					value = value.substring(value.lastIndexOf('.') + 1);
				break;
			}
		}
		return value;
	}

	public void gen(String genMethod, Expression object, TemplateContext ctx, TabbedWriter out, Object... args) {
		// is this the first time into an expression group
		Annotation annotation = object.getAnnotation(IEGLConstants.EGL_LOCATION);
		if (!debugIsProcessing && annotation != null && annotation.getValue(IEGLConstants.EGL_PARTLINE) != null) {
			debugIsProcessing = true;
			int thisEglLineNumber = ((Integer) annotation.getValue(IEGLConstants.EGL_PARTLINE)).intValue();
			// if we are continuing the same egl line, then skip writing out debug data
			if (thisEglLineNumber != lastEglLineNumber) {
				// if there is an outstanding line, write it
				writeDebugLine();
				debugHasOutstandingLine = true;
				firstEglLineNumber = thisEglLineNumber;
				lastEglLineNumber = thisEglLineNumber;
				firstJavaLineNumber = out.getLineNumber();
			}
			// process the generation
			super.gen(genMethod, object, ctx, out, args);
			lastJavaLineNumber = out.getLineNumber();
			debugIsProcessing = false;
		} else {
			if (debugIsProcessing && annotation != null && annotation.getValue(IEGLConstants.EGL_PARTLINE) != null)
				lastEglLineNumber = ((Integer) annotation.getValue(IEGLConstants.EGL_PARTLINE)).intValue();
			// process the generation
			super.gen(genMethod, object, ctx, out, args);
			if (debugIsProcessing)
				lastJavaLineNumber = out.getLineNumber();
		}
	}

	public void gen(String genMethod, Statement object, TemplateContext ctx, TabbedWriter out, Object... args) {
		// for statements, we only want to collect the data if this statement is part of a larger expression
		if (debugIsProcessing) {
			Annotation annotation = object.getAnnotation(IEGLConstants.EGL_LOCATION);
			if (annotation != null && annotation.getValue(IEGLConstants.EGL_PARTLINE) != null)
				lastEglLineNumber = ((Integer) annotation.getValue(IEGLConstants.EGL_PARTLINE)).intValue();
			// process the generation
			super.gen(genMethod, object, ctx, out, args);
			// the last line is 1 less because this is a statement
			lastJavaLineNumber = out.getLineNumber() - 1;
			if (firstJavaLineNumber > lastJavaLineNumber)
				lastJavaLineNumber = firstJavaLineNumber;
		} else
			// process the generation
			super.gen(genMethod, object, ctx, out, args);
	}

	public void writeDebugLine() {
		if (debugHasOutstandingLine) {
			debugData.append("" + firstEglLineNumber);
			debugData.append(":" + firstJavaLineNumber);
			if (firstJavaLineNumber != lastJavaLineNumber)
				debugData.append("," + (lastJavaLineNumber - firstJavaLineNumber + 1));
			debugData.append("\n");
		}
		debugHasOutstandingLine = false;
	}

	public void handleValidationError(Element obj) {
		int startLine = 0;
		int startOffset = 0;
		int endLine = 0;
		int endOffset = 0;
		String[] details = new String[] { obj.getEClass().getETypeSignature() };
		Annotation annotation = obj.getAnnotation(IEGLConstants.EGL_LOCATION);
		if (annotation != null) {
			if (annotation.getValue(IEGLConstants.EGL_PARTLINE) != null)
				startLine = ((Integer) annotation.getValue(IEGLConstants.EGL_PARTLINE)).intValue();
			if (annotation.getValue(IEGLConstants.EGL_PARTOFFSET) != null)
				startOffset = ((Integer) annotation.getValue(IEGLConstants.EGL_PARTOFFSET)).intValue();
		}
		EGLMessage message = EGLMessage.createEGLMessage(getMessageMapping(), EGLMessage.EGL_ERROR_MESSAGE, Constants.EGLMESSAGE_MISSING_TEMPLATE_FOR_OBJECT,
			obj, details, startLine, startOffset, endLine, endOffset);
		getMessageRequestor().addMessage(message);
	}

	public void handleValidationError(Annotation obj) {
		String[] details = new String[] { obj.getEClass().getETypeSignature() };
		EGLMessage message = EGLMessage.createEGLMessage(getMessageMapping(), EGLMessage.EGL_ERROR_MESSAGE,
			Constants.EGLMESSAGE_MISSING_TEMPLATE_FOR_ANNOTATION, obj, details, 0, 0, 0, 0);
		getMessageRequestor().addMessage(message);
	}

	public void handleValidationError(Type obj) {
		String[] details = new String[] { obj.getEClass().getETypeSignature() };
		EGLMessage message = EGLMessage.createEGLMessage(getMessageMapping(), EGLMessage.EGL_ERROR_MESSAGE, Constants.EGLMESSAGE_MISSING_TEMPLATE_FOR_TYPE,
			obj, details, 0, 0, 0, 0);
		getMessageRequestor().addMessage(message);
	}
}
