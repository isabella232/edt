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

import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.edt.compiler.core.IEGLConstants;
import org.eclipse.edt.gen.AbstractGeneratorCommand;
import org.eclipse.edt.gen.EglContext;
import org.eclipse.edt.gen.EGLMessages.EGLMessage;
import org.eclipse.edt.mof.codegen.api.TabbedReportWriter;
import org.eclipse.edt.mof.codegen.api.TabbedWriter;
import org.eclipse.edt.mof.codegen.api.TemplateContext;
import org.eclipse.edt.mof.egl.Annotation;
import org.eclipse.edt.mof.egl.Element;
import org.eclipse.edt.mof.egl.Expression;
import org.eclipse.edt.mof.egl.Function;
import org.eclipse.edt.mof.egl.Statement;
import org.eclipse.edt.mof.egl.Type;

public class Context extends EglContext {

	private static final long serialVersionUID = 6429116299734843162L;

	private TabbedWriter tabbedWriter;

	private boolean smapIsProcessing;
	private boolean smapHasOutstandingLine;
	private int firstEglLineNumber;
	private int lastEglLineNumber;
	private int firstJavaLineNumber;
	private int lastJavaLineNumber;

	private Function currentFunction;
	private String currentFile;

	private StringBuffer smapData = new StringBuffer();
	private StringBuffer smapExtension = new StringBuffer();
	private List<String> smapFiles = new ArrayList<String>();
	
	public Context(AbstractGeneratorCommand processor) {
		super(processor);
		smapData.append(Constants.smap_header);
		smapExtension.append("");
	}

	public TabbedWriter getTabbedWriter() {
		if (tabbedWriter == null) {
			tabbedWriter = Boolean.TRUE.equals(getParameter(org.eclipse.edt.gen.Constants.parameter_report)) ? new TabbedReportWriter("org.eclipse.edt.gen.java.templates.",
				new StringWriter()) : new TabbedWriter(new StringWriter());
			return tabbedWriter;
		} else {
			TabbedWriter newTabbedWriter = new TabbedWriter(new StringWriter());
			newTabbedWriter.setLineNumber(tabbedWriter.getLineNumber());
			return newTabbedWriter;
		}
	}

	public Function getCurrentFunction() {
		return currentFunction;
	}

	public void setCurrentFunction(Function currentFunction) {
		this.currentFunction = currentFunction;
	}

	public String getCurrentFile() {
		return currentFile;
	}

	public void setCurrentFile(String currentFile) {
		this.currentFile = currentFile;
	}

	public StringBuffer getSmapData() {
		return smapData;
	}

	public StringBuffer getSmapExtension() {
		return smapExtension;
	}

	public List<String> getSmapFiles() {
		return smapFiles;
	}

	public boolean isSmapIsProcessing() {
		return smapIsProcessing;
	}

	public void setSmapIsProcessing(boolean smapIsProcessing) {
		this.smapIsProcessing = smapIsProcessing;
	}

	public String getRawPrimitiveMapping(String item) {
		return super.getPrimitiveMapping(item);
	}

	@SuppressWarnings("unchecked")
	public String getPrimitiveMapping(String item) {
		String value = super.getPrimitiveMapping(item);
		if (value != null) {
			// check to see if this is in the list of imported types. If it is, then we can use the short name.
			List<String> typesImported = (List<String>) this.getAttribute(this.getClass(), Constants.SubKey_partTypesImported);
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
			List<String> typesImported = (List<String>) this.getAttribute(this.getClass(), Constants.SubKey_partTypesImported);
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
		List<String> typesImported = (List<String>) this.getAttribute(this.getClass(), Constants.SubKey_partTypesImported);
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
		List<String> typesImported = (List<String>) this.getAttribute(this.getClass(), Constants.SubKey_partTypesImported);
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

	public void invoke(String genMethod, Expression object, TemplateContext ctx, TabbedWriter out) {
		// is this the first time into an expression group
		Annotation annotation = object.getAnnotation(IEGLConstants.EGL_LOCATION);
		if (!smapIsProcessing && annotation != null && annotation.getValue(IEGLConstants.EGL_PARTLINE) != null) {
			smapIsProcessing = true;
			int thisEglLineNumber = ((Integer) annotation.getValue(IEGLConstants.EGL_PARTLINE)).intValue();
			// if we are continuing the same egl line, then skip writing out debug data
			if (thisEglLineNumber != lastEglLineNumber) {
				// if there is an outstanding line, write it
				writeSmapLine();
				smapHasOutstandingLine = true;
				firstEglLineNumber = thisEglLineNumber;
				lastEglLineNumber = thisEglLineNumber;
				firstJavaLineNumber = out.getLineNumber();
			}
			// process the generation
			super.invoke(genMethod, object, ctx, out);
			// if we had some statements as part of this expression, then we already have the corrected ending line number
			if (lastJavaLineNumber == 0)
				lastJavaLineNumber = out.getLineNumber();
			smapIsProcessing = false;
		} else {
			if (smapIsProcessing && annotation != null && annotation.getValue(IEGLConstants.EGL_PARTLINE) != null)
				lastEglLineNumber = ((Integer) annotation.getValue(IEGLConstants.EGL_PARTLINE)).intValue();
			// process the generation
			super.invoke(genMethod, object, ctx, out);
			if (smapIsProcessing)
				lastJavaLineNumber = out.getLineNumber();
		}
	}

	public void invoke(String genMethod, Statement object, TemplateContext ctx, TabbedWriter out) {
		// for statements, we only want to collect the data if this statement is part of a larger expression
		if (smapIsProcessing) {
			Annotation annotation = object.getAnnotation(IEGLConstants.EGL_LOCATION);
			if (annotation != null && annotation.getValue(IEGLConstants.EGL_PARTLINE) != null)
				lastEglLineNumber = ((Integer) annotation.getValue(IEGLConstants.EGL_PARTLINE)).intValue();
			// process the generation
			super.invoke(genMethod, object, ctx, out);
			// the last line is 1 less because this is a statement
			lastJavaLineNumber = out.getLineNumber() - 1;
			if (firstJavaLineNumber > lastJavaLineNumber)
				lastJavaLineNumber = firstJavaLineNumber;
		} else
			// process the generation
			super.invoke(genMethod, object, ctx, out);
	}

	public void genSmapEnd(Function object, TabbedWriter out) {
		// is this the first time into an expression group
		Annotation annotation = object.getAnnotation(IEGLConstants.EGL_LOCATION);
		if (annotation != null && annotation.getValue(IEGLConstants.EGL_PARTLINE) != null) {
			int thisEglLineNumber = ((Integer) annotation.getValue(IEGLConstants.EGL_PARTLINE)).intValue();
			// if there is an outstanding line, write it
			writeSmapLine();
			smapHasOutstandingLine = true;
			firstEglLineNumber = thisEglLineNumber;
			lastEglLineNumber = thisEglLineNumber;
			firstJavaLineNumber = out.getLineNumber();
			lastJavaLineNumber = out.getLineNumber();
			smapIsProcessing = false;
		}
	}

	public void setSmapLastJavaLineNumber(int number) {
		lastJavaLineNumber = number;
	}

	public void writeSmapLine() {
		if (smapHasOutstandingLine) {
			smapData.append("" + firstEglLineNumber);
			if (currentFile != null) {
				if (smapFiles.indexOf(currentFile) < 0)
					smapFiles.add(currentFile);
				smapData.append("#" + (smapFiles.indexOf(currentFile) + 1));
			} else
				smapData.append("#1");
			smapData.append(":" + firstJavaLineNumber);
			if (firstJavaLineNumber != lastJavaLineNumber)
				smapData.append("," + (lastJavaLineNumber - firstJavaLineNumber + 1));
			smapData.append("\n");
			// we need to reset the last java line number
			lastJavaLineNumber = 0;
		}
		smapHasOutstandingLine = false;
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
}
