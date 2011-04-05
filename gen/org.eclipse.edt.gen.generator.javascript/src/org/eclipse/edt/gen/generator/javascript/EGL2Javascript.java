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
package org.eclipse.edt.gen.generator.javascript;

import org.eclipse.edt.compiler.core.IEGLConstants;
import org.eclipse.edt.compiler.internal.core.utils.Aliaser;
import org.eclipse.edt.gen.AbstractGeneratorCommand;
import org.eclipse.edt.gen.javascript.Constants;
import org.eclipse.edt.gen.javascript.JavascriptGenerator;
import org.eclipse.edt.mof.egl.Annotation;
import org.eclipse.edt.mof.egl.Part;

public class EGL2Javascript extends AbstractGeneratorCommand {

	public EGL2Javascript() {
		super();
		// define our local command parameters
		this.installParameter(false, Constants.parameter_checkOverflow, new String[] { "checkOverflow", "overflow", "co" }, new Boolean[] { false, true },
			"Overflow must be defined as true or false");
	}

	public static void main(String[] args) {
		// set up the command processor and pass the overriding command line options
		EGL2Javascript genPart = new EGL2Javascript();
		genPart.generate(args, new JavascriptGenerator(genPart), null);
	}

	public String getFileExtention() {
		return ".js";
	}

	public String[] getNativeTypePath() {
		// this defined the locations of the nativeTypes.properties files to be loaded and used
		return new String[] { "org.eclipse.edt.gen.javascript.nativeTypes" };
	}

	public String[] getPrimitiveTypePath() {
		// this defined the locations of the primitiveTypes.properties files to be loaded and used
		return new String[] { "org.eclipse.edt.gen.javascript.primitiveTypes" };
	}

	public String[] getEGLMessagePath() {
		// this defined the locations of the EGLMessages.properties files to be loaded and used
		return new String[] { "org.eclipse.edt.gen.javascript.EGLMessages" };
	}

	public String[] getTemplatePath() {
		return new String[] { "org.eclipse.edt.gen.javascript.templates.egl.lang.templates", "org.eclipse.edt.gen.javascript.templates.egl.core.templates",
			"org.eclipse.edt.gen.javascript.templates.templates" };
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
		if (annot != null)
			nameOrAlias = (String) annot.getValue();
		else
			nameOrAlias = part.getId();
		buf.append(Aliaser.getAlias(nameOrAlias));
		buf.append(getFileExtention());
		return buf.toString();
	}
}
