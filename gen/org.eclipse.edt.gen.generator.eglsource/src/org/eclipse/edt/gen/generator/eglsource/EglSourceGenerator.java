/*******************************************************************************
 * Copyright (c) 2005, 2011 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/

package org.eclipse.edt.gen.generator.eglsource;

import java.io.StringWriter;

import org.eclipse.edt.compiler.internal.interfaces.IGenerationMessageRequestor;
import org.eclipse.edt.compiler.internal.util.IGenerationResultsMessage;
import org.eclipse.edt.gen.AbstractGeneratorCommand;
import org.eclipse.edt.gen.EglContext;
import org.eclipse.edt.gen.GenerationException;
import org.eclipse.edt.gen.Generator;
import org.eclipse.edt.mof.codegen.api.TabbedWriter;

public class EglSourceGenerator extends Generator {

	protected EglSourceContext context;
	protected TabbedWriter out;
	protected AbstractGeneratorCommand generator;

	public EglSourceGenerator(AbstractGeneratorCommand processor) {
		this(processor, null);
		generator = processor;
		out = new TabbedWriter(new StringWriter());
		out.setAutoIndent(false);
	}

	public EglSourceGenerator(AbstractGeneratorCommand processor, IGenerationMessageRequestor requestor) {
		super(processor, requestor);
		generator = processor;
	}

	public EglContext makeContext(AbstractGeneratorCommand processor) {
		context = new EglSourceContext(processor);
		return context;
	}

	public void generate(Object part) throws GenerationException {
		try {
			context.invoke("genObject", (Object) part, context, out);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void dumpErrorMessages() {
		// dump out all validation and generation messages
		for (IGenerationResultsMessage message : context.getMessageRequestor().getMessages()) {
			System.out.println(message.getBuiltMessage());
		}
	}

	@Override
	public String getFileExtension() {
		return "";
	}

	public void processFile(String fileName) {
	}

	@Override
	public Object getResult() {
		return out.getWriter().toString();
	}
}
