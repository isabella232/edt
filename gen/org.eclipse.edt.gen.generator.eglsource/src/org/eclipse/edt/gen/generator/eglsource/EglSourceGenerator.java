/*******************************************************************************
 * Copyright © 2005, 2012 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/

package org.eclipse.edt.gen.generator.eglsource;

import java.util.Hashtable;

import org.eclipse.edt.compiler.internal.interfaces.IGenerationMessageRequestor;
import org.eclipse.edt.compiler.internal.util.IGenerationResultsMessage;
import org.eclipse.edt.gen.AbstractGeneratorCommand;
import org.eclipse.edt.gen.GenerationException;
import org.eclipse.edt.gen.Generator;

public class EglSourceGenerator extends Generator {

	protected EglSourceContext context;
	
	protected AbstractGeneratorCommand generator;

	public EglSourceGenerator(AbstractGeneratorCommand processor) {
		this(processor, null);
		generator = processor;
	}

	public EglSourceGenerator(AbstractGeneratorCommand processor, IGenerationMessageRequestor requestor) {
		super(processor, requestor);
		generator = processor;
	}

	public EglSourceContext makeContext(AbstractGeneratorCommand processor) {
		return makeContext(processor, false);
	}
	
	public EglSourceContext makeContext(AbstractGeneratorCommand processor, boolean needTabWritter){
			
		if(context == null){
			context = new EglSourceContext(processor);
			context.makeTabbedWriter();
		}
		return context;
	}

	public void generate(Object part) throws GenerationException {
		try {
			context.invoke("genObject", (Object) part, context);
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
	public Hashtable<String, String> getResult() {
		return context.getSourceFileContentTable();
	}
}
