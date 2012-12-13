/*******************************************************************************
 * Copyright © 2012 IBM Corporation and others.
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

import org.eclipse.edt.gen.CommandOption;
import org.eclipse.edt.gen.CommandParameter;
import org.eclipse.edt.gen.GenerationRegistry;
import org.eclipse.edt.gen.GenerationContributor;

public class JavaCoreGenerationContributor implements GenerationContributor {
	static final CommandOption[] commandOptions;
	static final String[] templatePath;
	static final String[] nativeTypePath;
	static final String[] primitiveTypePath;
	static final String[] messagePath;
	static final String[] supportedPartTypes;
	static final String[] supportedStereotypes;
	// define the list of command options for this generator
	static {
		commandOptions = new CommandOption[] { 
			new CommandOption(Constants.parameter_checkOverflow, new String[] { "checkOverflow", "overflow", "co" },
				new CommandParameter(false, new Boolean[] { false, true }, false, "Overflow must be defined as true or false")),
			new CommandOption(org.eclipse.edt.gen.Constants.parameter_report, new String[] { "report" },
				new CommandParameter(false, new Boolean[] { false, true }, false, "Report must be defined as true or false"))
		};
	}
	// define the list of template directories for this generator
	static {
		templatePath = new String[] { 
			"org.eclipse.edt.gen.java.templates.eglx.lang.templates",
			"org.eclipse.edt.gen.java.templates.eglx.persistence.sql.templates", 
			"org.eclipse.edt.gen.java.templates.eglx.persistence.templates",
			"org.eclipse.edt.gen.java.templates.templates"
		};
	}
	// define the list of native type directories for this generator
	static {
		nativeTypePath = new String[] { 
			"org.eclipse.edt.gen.java.nativeTypes", 
			"org.eclipse.edt.gen.java.templates.eglx.persistence.sql.nativeTypes"
		};
	}
	// define the list of primitive type directories for this generator
	static {
		primitiveTypePath = new String[] { 
			"org.eclipse.edt.gen.java.primitiveTypes"
		};
	}
	// define the list of message directories for this generator
	static {
		messagePath = new String[] { 
			"org.eclipse.edt.gen.java.EGLMessages"
		};
	}

	//define the list of part types supported
	static {
		supportedPartTypes = new String[] {
				"org.eclipse.edt.mof.egl.Delegate",
				"org.eclipse.edt.mof.egl.ExternalType",
				"org.eclipse.edt.mof.egl.Record",
				"org.eclipse.edt.mof.egl.Handler",
				"org.eclipse.edt.mof.egl.Interface",
				"org.eclipse.edt.mof.egl.Library",
				"org.eclipse.edt.mof.egl.Program",
				"org.eclipse.edt.mof.egl.Service",
				"org.eclipse.edt.mof.egl.Enumeration",
				"org.eclipse.edt.mof.egl.Class",
				"org.eclipse.edt.mof.egl.AnnotationType",
				"org.eclipse.edt.mof.egl.StereotypeType"
		};
	}
	
	//define the list of stereotypes supported
	static {
		supportedStereotypes = new String[] {
				"eglx.lang.Exception",
				"eglx.lang.NativeType",
				"eglx.lang.BasicProgram",
				"eglx.java.JavaObject",
				"eglx.persistence.Entity"				
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
		// register the array of supported part types for this configuration
		// if you don't have any, then register the empty array
		generator.registerSupportedPartTypes(supportedPartTypes);
		// register the array of supported stereotypes for this configuration
		// if you don't have any, then register the empty array
		generator.registerSupportedStereotypes(supportedStereotypes);
	}
}
