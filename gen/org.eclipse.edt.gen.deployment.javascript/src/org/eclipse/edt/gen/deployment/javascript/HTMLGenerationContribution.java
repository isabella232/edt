/*******************************************************************************
 * Copyright Â© 2011 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.edt.gen.deployment.javascript;

import org.eclipse.edt.gen.CommandOption;
import org.eclipse.edt.gen.GenerationContributor;
import org.eclipse.edt.gen.GenerationContribution;

public class HTMLGenerationContribution implements GenerationContribution {
	static final CommandOption[] commandOptions;
	static final String[] templatePath;
	static final String[] nativeTypePath;
	static final String[] primitiveTypePath;
	static final String[] messagePath;
	// define the list of command options for this generator
	static {
		commandOptions = new CommandOption[] { 
		};
	}
	// define the list of template directories for this generator
	static {
		templatePath = new String[] { 
			"org.eclipse.edt.gen.deployment.javascript.templates.templates"
		};
	}
	// define the list of native type directories for this generator
	static {
		nativeTypePath = new String[] { 
		};
	}
	// define the list of primitive type directories for this generator
	static {
		primitiveTypePath = new String[] { 
		};
	}
	// define the list of message directories for this generator
	static {
		messagePath = new String[] { 
			"org.eclipse.edt.gen.deployment.javascript.EGLMessages"
		};
	}

	public void configure(GenerationContributor generator) {
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
