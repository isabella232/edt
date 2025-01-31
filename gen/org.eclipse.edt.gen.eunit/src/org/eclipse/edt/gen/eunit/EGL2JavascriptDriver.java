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
package org.eclipse.edt.gen.eunit;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.edt.compiler.ICompiler;
import org.eclipse.edt.compiler.internal.interfaces.IGenerationMessageRequestor;
import org.eclipse.edt.gen.AbstractGeneratorCommand;

public class EGL2JavascriptDriver extends EGL2Base {

	protected static final String javascriptDriverPartNameAppend = "_rui";
	
	public EGL2JavascriptDriver() {
		super();
	}

	public static void main(String[] args) {
		start(args, null, new NullEUnitGenerationNotifier());
	}

	public static void start(String[] args, ICompiler compiler, IEUnitGenerationNotifier eckGenerationNotifier) {
		List<String> arguments = new ArrayList<String>();
		for (int i = 0; i < args.length; i++) {
			arguments.add(args[i]);
		}
		arguments.add("-c");
		arguments.add("org.eclipse.edt.gen.eunit.EUnitJavascriptDriverGenerationContributor");
		arguments.add("org.eclipse.edt.gen.eunit.EUnitDriverGenerationContributor");
		EGL2JavascriptDriver genPart = new EGL2JavascriptDriver();
		genPart.startGeneration(arguments.toArray(new String[arguments.size()]), compiler, eckGenerationNotifier);
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
