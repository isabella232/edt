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
package org.eclipse.edt.gen.eunit;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.edt.compiler.ICompiler;
import org.eclipse.edt.compiler.internal.interfaces.IGenerationMessageRequestor;
import org.eclipse.edt.gen.AbstractGeneratorCommand;

public class EGL2JavascriptDriver extends EGL2Base {

	private static final String javascriptDriverPartNameAppend = "_rui";
	
	public EGL2JavascriptDriver() {
		super();
	}

	public static void main(String[] args) {
		start(args, null, new NullEUnitGenerationNotifier());
	}

	public static void start(String[] args, ICompiler compiler, IEUnitGenerationNotifier eckGenerationNotifier) {
		EGL2JavascriptDriver genPart = new EGL2JavascriptDriver();
		genPart.startGeneration(args, compiler, eckGenerationNotifier);
	}	

	public String[] getTemplatePath() {
		List<String> templates = new ArrayList<String>();
		templates.add("org.eclipse.edt.gen.eunit.templates.javascript.templates");
		String[] others = super.getTemplatePath();
		for (String other : others) {
			templates.add(other);
		}
		return (String[]) templates.toArray(new String[templates.size()]);
	}


	@Override
	protected EUnitDriverGenerator getEckDriverGenerator(AbstractGeneratorCommand processor, IGenerationMessageRequestor req
			, IEUnitGenerationNotifier eckGenerationNotifier) {
		return new EUnitDriverGenerator(processor, req, javascriptDriverPartNameAppend, eckGenerationNotifier);
	}

	@Override
	protected EUnitRunAllDriverGenerator getEckRunAllDriverGenerator(AbstractGeneratorCommand processor, IGenerationMessageRequestor req
			, IEUnitGenerationNotifier eckGenerationNotifier) {
		return new EUnitRunAllJavascriptDriverGenerator(processor, req, javascriptDriverPartNameAppend, eckGenerationNotifier);				   
	}
	
}
