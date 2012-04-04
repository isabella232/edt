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
package org.eclipse.edt.gen.eunit;

import org.eclipse.edt.gen.CommandOption;
import org.eclipse.edt.gen.CommandParameter;
import org.eclipse.edt.gen.GenerationRegistry;
import org.eclipse.edt.gen.GenerationContributor;

public class EUnitDriverGenerationContributor implements GenerationContributor {
	static final CommandOption[] commandOptions;
	static final String[] templatePath;
	static final String[] nativeTypePath;
	static final String[] primitiveTypePath;
	static final String[] messagePath;
	// define the list of command options for this generator
	static {
		commandOptions = new CommandOption[] { 
			new CommandOption(EGL2Base.ARG_PARM_GENPARTS, new String[] { EGL2Base.ARG_PARM_GENPARTS },
				new CommandParameter(false, new String[] { null }, false, "Use this file to load parts to be generated"))
		};
	}
	// define the list of template directories for this generator
	static {
		templatePath = new String[] { 
			"org.eclipse.edt.gen.eunit.templates.templates"
		};
	}
	// define the list of native type directories for this generator
	static {
		nativeTypePath = new String[] { 
			"org.eclipse.edt.gen.eunit.nativeTypes"
		};
	}
	// define the list of primitive type directories for this generator
	static {
		primitiveTypePath = new String[] { 
			"org.eclipse.edt.gen.eunit.primitiveTypes"
		};
	}
	// define the list of message directories for this generator
	static {
		messagePath = new String[] { 
			"org.eclipse.edt.gen.eunit.EGLMessages"
		};
	}

	public void contribute(GenerationRegistry generator) {
		// register the array of command options for this configuration
		// if you don't have any, then register the empty array
		generator.registerCommandOptions(commandOptions);
		// register the array of template path directories for this configuration
		// if you don't have any, then register the empty array
		generator.registerTemplatePath(templatePath);
		// register the array of native type path directories for this configuration
		// if you don't have any, then register the empty array
		generator.registerNativeTypePath(nativeTypePath);
		// register the array of template path directories for this configuration
		// if you don't have any, then register the empty array
		generator.registerPrimitiveTypePath(primitiveTypePath);
		// register the array of template path directories for this configuration
		// if you don't have any, then register the empty array
		generator.registerMessagePath(messagePath);
	}
}
