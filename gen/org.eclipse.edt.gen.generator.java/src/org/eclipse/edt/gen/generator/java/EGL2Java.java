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

import org.eclipse.edt.compiler.core.IEGLConstants;
import org.eclipse.edt.compiler.internal.core.utils.Aliaser;
import org.eclipse.edt.gen.AbstractGeneratorCommand;
import org.eclipse.edt.gen.java.Constants;
import org.eclipse.edt.gen.java.JavaGenerator;
import org.eclipse.edt.mof.egl.Annotation;
import org.eclipse.edt.mof.egl.Part;

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
		genPart.generate(args, new JavaGenerator(genPart), null);
	}

	public String getFileExtention() {
		return ".java";
	}

	public String[] getNativeTypePath() {
		// this defined the locations of the nativeTypes.properties files to be loaded and used
		return new String[] { "org.eclipse.edt.gen.java.nativeTypes" };
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
		// this defined the locations of the template.properties files to be loaded and used
		return new String[] { "org.eclipse.edt.gen.java.templates.egl.lang.templates", "org.eclipse.edt.gen.java.templates.egl.core.templates",
			"org.eclipse.edt.gen.java.templates.templates" };
	}

	protected String getRelativeFileName(Part part) {
		StringBuilder buf = new StringBuilder(50);
		String pkg = part.getPackageName();
		if (pkg.length() > 0) {
			buf.append(Aliaser.packageNameAlias(pkg.split("[.]"), '/'));
			buf.append('/');
		}

		String nameOrAlias;
		Annotation annot = part.getAnnotation(IEGLConstants.PROPERTY_ALIAS);
		if (annot != null) {
			nameOrAlias = (String) annot.getValue();
		} else {
			nameOrAlias = part.getId();
		}
		buf.append(Aliaser.getAlias(nameOrAlias));

		buf.append(getFileExtention());
		return buf.toString();
	}
}
