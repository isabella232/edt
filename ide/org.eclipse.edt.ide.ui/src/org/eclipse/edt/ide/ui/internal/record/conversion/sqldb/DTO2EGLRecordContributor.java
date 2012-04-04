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
package org.eclipse.edt.ide.ui.internal.record.conversion.sqldb;

import org.eclipse.edt.gen.CommandOption;
import org.eclipse.edt.gen.GenerationRegistry;
import org.eclipse.edt.gen.GenerationContributor;

public class DTO2EGLRecordContributor implements GenerationContributor {
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
			"org.eclipse.edt.ide.ui.internal.record.conversion.sqldb.templates"
		};
	}
	// define the list of native type directories for this generator
	static {
		// as we are going to add our own system type, implemented as a java library, we need to provide the details in the
		// nativeTypes.properties file located in our directory. To tell the command processor about it, we need to add the
		// properties file to the nativeTypes list. If we were adding a new base type, or overriding an existing one, this is
		// where we would do that change as well.
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
