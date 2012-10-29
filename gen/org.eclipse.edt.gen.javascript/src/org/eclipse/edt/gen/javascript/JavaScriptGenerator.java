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

import java.io.IOException;
import java.io.StringWriter;

import org.eclipse.edt.compiler.core.IEGLConstants;
import org.eclipse.edt.compiler.internal.interfaces.IGenerationMessageRequestor;
import org.eclipse.edt.compiler.internal.util.IGenerationResultsMessage;
import org.eclipse.edt.gen.AbstractGeneratorCommand;
import org.eclipse.edt.gen.GenerationException;
import org.eclipse.edt.gen.Generator;
import org.eclipse.edt.gen.EGLMessages.EGLMessage;
import org.eclipse.edt.gen.javascript.templates.JavaScriptTemplate;
import org.eclipse.edt.mof.codegen.api.TabbedReportWriter;
import org.eclipse.edt.mof.codegen.api.TabbedWriter;
import org.eclipse.edt.mof.codegen.api.TemplateException;
import org.eclipse.edt.mof.egl.Annotation;
import org.eclipse.edt.mof.egl.ExternalType;
import org.eclipse.edt.mof.egl.Part;

public class JavaScriptGenerator extends Generator {

	protected Context context;
	protected TabbedWriter out;
	protected AbstractGeneratorCommand generator;

	public JavaScriptGenerator(AbstractGeneratorCommand processor) {
		this(processor, null);
	}

	public JavaScriptGenerator(AbstractGeneratorCommand processor, IGenerationMessageRequestor requestor) {
		super(processor, requestor);
		generator = processor;
	}

	public String getResult() {
		return out.getWriter().toString();
	}

	@Override
	public TabbedReportWriter getReport() {
		return (out instanceof TabbedReportWriter) ? (TabbedReportWriter) out : null;
	}

	public Context makeContext(AbstractGeneratorCommand processor) {
		context = new Context(processor);
		return context;
	}

	public boolean visit(Part part) {
		try {
			context.invoke(JavaScriptTemplate.genPart, part, context, out);
		}
		catch (TemplateException e) {
			e.printStackTrace();
		}
		return false;
	}

	public void generate(Object part) throws GenerationException {
		makeWriter();
		try {
			context.putAttribute(context.getClass(), Constants.SubKey_partBeingGenerated, part);
			context.invoke(JavaScriptTemplate.preGenPart, part, context);
			if (!context.getMessageRequestor().isError()) {
				out.getWriter().flush();
				
				// Add the header.
				boolean autoIndent = out.getAutoIndent();
				out.setAutoIndent(false);
				if (getHeader() != null && getHeader().length() > 0) {
					out.println(getHeader());
				}
				out.setAutoIndent(autoIndent);
				
				context.invoke(JavaScriptTemplate.genPart, part, context, out);
				out.flush();
			}
		}
		catch (IOException e) {
			throw new GenerationException(e);
		}
		catch (TemplateException e) {
			String[] details1 = new String[] { e.getLocalizedMessage() };
			EGLMessage message1 = EGLMessage.createEGLMessage(context.getMessageMapping(), EGLMessage.EGL_ERROR_MESSAGE,
				Constants.EGLMESSAGE_EXCEPTION_OCCURED, e, details1, context.getLastStatementLocation());
			context.getMessageRequestor().addMessage(message1);
			if (e.getCause() != null) {
				String[] details2 = new String[] { e.getCause().toString() };
				EGLMessage message2 = EGLMessage.createEGLMessage(context.getMessageMapping(), EGLMessage.EGL_ERROR_MESSAGE, Constants.EGLMESSAGE_STACK_TRACE,
					e, details2, context.getLastStatementLocation());
				context.getMessageRequestor().addMessage(message2);
			}
			int startLine = 0;
			Annotation annotation = context.getLastStatementLocation();
			if (annotation != null) {
				if (annotation.getValue(IEGLConstants.EGL_PARTLINE) != null)
					startLine = ((Integer) annotation.getValue(IEGLConstants.EGL_PARTLINE)).intValue();
			}
			System.err.println("generating:" + ((Part) part).getFullyQualifiedName() + "[" + ((Part) part).getFileName() + "]:(" + startLine + ")" );
			// print out the whole stack trace
			e.printStackTrace();
		}
		// close the output
		out.close();
	}

	private void makeWriter() {
		out = Boolean.TRUE.equals(context.getParameter(org.eclipse.edt.gen.Constants.parameter_report)) 
				? new TabbedReportWriter("org.eclipse.edt.gen.javascript.templates.", new StringWriter()) 
				: new TabbedWriter(new StringWriter());
	}

	public void dumpErrorMessages() {
		// dump out all validation and generation messages
		for (IGenerationResultsMessage message : context.getMessageRequestor().getMessages()) {
			System.out.println(message.getBuiltMessage());
		}
	}

	public void processFile(String fileName) {
		// do any post processing once the file has been written
		writeReport(context, fileName, getReport(), Constants.EGLMESSAGE_ENCODING_ERROR, Constants.EGLMESSAGE_GENERATION_REPORT_FAILED);
	}

	public String getRelativeFileName(Part part) {
		StringBuilder buf = new StringBuilder(150);
		String pkg = part.getCaseSensitivePackageName();
		if (pkg.length() > 0) {
			buf.append(JavaScriptAliaser.packageNameAlias(pkg.split("[.]"), '/'));
			buf.append('/');
		}
		String name = JavaScriptAliaser.getAlias(part.getCaseSensitiveName());
		if(part instanceof ExternalType){
			buf.append(JavaScriptAliaser.getAliasForExternalType(name));
		}else{
			buf.append(name);
		}
		buf.append(getFileExtension());
		return buf.toString();
	}

	public String getFileExtension() {
		return ".js";
	}
}
