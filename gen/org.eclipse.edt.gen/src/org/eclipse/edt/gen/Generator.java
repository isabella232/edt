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

import java.util.Map;
import java.util.Map.Entry;

import org.eclipse.edt.mof.codegen.api.TemplateFactory;
import org.eclipse.edt.mof.egl.Part;

public abstract class Generator {

	protected TemplateFactory factory = new TemplateFactory();

	public Generator(AbstractGeneratorCommand processor) {
		// create a context based on the generator being driven
		EglContext context = makeContext(processor);
		// define our template factory to the context
		context.setTemplateFactory(this.factory);
		// add all of the command processor keys to the context
		Map<String, CommandParameter> parameterMapping = processor.parameterMapping;
		for (Entry<String, CommandParameter> entry : parameterMapping.entrySet()) {
			context.put(entry.getKey(), entry.getValue());
		}
		// load the template path and template factories
		this.factory.load(processor.getTemplates(), processor.getClass().getClassLoader());
	}

	public abstract EglContext makeContext(AbstractGeneratorCommand processor);

	public abstract void generate(Part part) throws GenerationException;

	public abstract void processFile(String fileName);

	public abstract String getResult();

	public abstract void dumpErrorMessages();
}
