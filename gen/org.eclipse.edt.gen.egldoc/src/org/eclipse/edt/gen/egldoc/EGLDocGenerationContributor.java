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
package org.eclipse.edt.gen.egldoc;

import org.eclipse.edt.gen.CommandOption;
import org.eclipse.edt.gen.GenerationRegistry;
import org.eclipse.edt.gen.GenerationContributor;

public class EGLDocGenerationContributor implements GenerationContributor {
	
	static final CommandOption[] commandOptions = {};
	static final String[] templatePath = {"org.eclipse.edt.gen.egldoc.templates.templates"};
	static final String[] nativeTypePath = {};
	static final String[] primitiveTypePath = {};
	static final String[] messagePath = {"org.eclipse.edt.gen.egldoc.EGLMessages"};
	
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
