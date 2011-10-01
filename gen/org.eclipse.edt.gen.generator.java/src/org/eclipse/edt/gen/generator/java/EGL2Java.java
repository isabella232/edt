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
package org.eclipse.edt.gen.generator.java;

import org.eclipse.edt.gen.AbstractGeneratorCommand;
import org.eclipse.edt.gen.java.Constants;
import org.eclipse.edt.gen.java.JavaGenerator;

public class EGL2Java extends AbstractGeneratorCommand {

	public EGL2Java() {
		super();
		// define our local command parameters
		this.installParameter(false, Constants.parameter_checkOverflow, new String[] { "checkOverflow", "overflow", "co" }, new Boolean[] { false, true },
			"Overflow must be defined as true or false");
	}

	public static void main(String[] args) {
		// set up the command processor and pass the overriding command line options
		EGL2Java genPart = new EGL2Java();
		genPart.generate(args, new JavaGenerator(genPart), null, null);
	}

	public String[] getNativeTypePath() {
		// this defined the locations of the nativeTypes.properties files to be loaded and used
		return new String[] { "org.eclipse.edt.gen.java.nativeTypes", "org.eclipse.edt.gen.java.templates.eglx.persistence.sql.nativeTypes" };
	}

	public String[] getPrimitiveTypePath() {
		// this defined the locations of the primitiveTypes.properties files to be loaded and used
		return new String[] { "org.eclipse.edt.gen.java.primitiveTypes" };
	}

	public String[] getEGLMessagePath() {
		// this defined the locations of the EGLMessages.properties files to be loaded and used
		return new String[] { "org.eclipse.edt.gen.java.EGLMessages" };
	}

	public String[] getTemplatePath() {
		// this defined the locations of the templates.properties files to be loaded and used
		return new String[] { "org.eclipse.edt.gen.java.templates.eglx.lang.templates",
				"org.eclipse.edt.gen.java.templates.eglx.persistence.sql.templates",
				"org.eclipse.edt.gen.java.templates.eglx.persistence.templates",
				"org.eclipse.edt.gen.java.templates.templates" };
	}

}
