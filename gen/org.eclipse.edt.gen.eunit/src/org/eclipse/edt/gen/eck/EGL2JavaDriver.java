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
package org.eclipse.edt.gen.eck;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.edt.compiler.ICompiler;
import org.eclipse.edt.compiler.internal.interfaces.IGenerationMessageRequestor;
import org.eclipse.edt.gen.AbstractGeneratorCommand;

public class EGL2JavaDriver extends EGL2Base {

	private static final String javaDriverPartNameAppend = "_pgm";
	
	public EGL2JavaDriver() {
		super();
	}

	public static void main(String[] args) {		
		start(args, null, new NullEckGenerationNotifier());
	}
	
	public static void start(String[] args, ICompiler compiler, IEckGenerationNotifier eckGenerationNotifier) {
		EGL2JavaDriver genPart = new EGL2JavaDriver();
		genPart.startGeneration(args, compiler, eckGenerationNotifier);
	}

	public String[] getTemplatePath() {
		List<String> templates = new ArrayList<String>();
		templates.add("org.eclipse.edt.gen.eck.templates.java.templates");
		String[] others = super.getTemplatePath();
		for (String other : others) {
			templates.add(other);
		}
		return (String[]) templates.toArray(new String[templates.size()]);
	}

	@Override
	protected EckDriverGenerator getEckDriverGenerator(AbstractGeneratorCommand processor, IGenerationMessageRequestor req
			, IEckGenerationNotifier eckGenerationNotifier) {
		return new EckDriverGenerator(processor, req, javaDriverPartNameAppend, eckGenerationNotifier);
	}

	@Override
	protected EckRunAllDriverGenerator getEckRunAllDriverGenerator(AbstractGeneratorCommand processor, IGenerationMessageRequestor req
			, IEckGenerationNotifier eckGenerationNotifier) {
		return new EckRunAllJavaDriverGenerator(processor, req, javaDriverPartNameAppend, eckGenerationNotifier);
	}
	
	
}
