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
package org.eclipse.edt.gen.javascriptdev.ide;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.Path;
import org.eclipse.edt.compiler.internal.interfaces.IGenerationMessageRequestor;
import org.eclipse.edt.gen.javascriptdev.Constants;
import org.eclipse.edt.ide.compiler.gen.EclipseJavaScriptGenerator;
import org.eclipse.edt.ide.compiler.gen.JavaScriptGenerator;
import org.eclipse.edt.mof.egl.Part;
import org.eclipse.edt.mof.serialization.IEnvironment;

public class JavaScriptDevGenerator extends JavaScriptGenerator {

	public JavaScriptDevGenerator() {
		super();
	}

	public void generate(String filePath, Part part, IEnvironment env, IGenerationMessageRequestor msgRequestor) throws Exception {
		IFile file = ResourcesPlugin.getWorkspace().getRoot().getFile(new Path(filePath));
		preprocess(part);
		EclipseEGL2JavaScriptDev cmd = new EclipseEGL2JavaScriptDev(file, part, this);
		String[] args = buildArgs(file, part);
		if (getEditMode()) {
			List<String> arguments = new ArrayList<String>();
			for (int i = 0; i < args.length; i++) {
				arguments.add(args[i]);
			}
			arguments.add("-" + Constants.PARAMETER_VE_ENABLE_EDITING);
			arguments.add("true");
			args = arguments.toArray(new String[arguments.size()]);
		}
		cmd.generate(args, new EclipseJavaScriptGenerator(cmd, msgRequestor), env, null);
	}

	@Override
	public String getOutputDirectory(IResource resource) {
		return Activator.OUTPUT_DIRECTORY_INTERNAL_PATH;
	}

	@Override
	protected String getRelativeFilePath(IFile eglFile, Part part) {
		return new EclipseJavaScriptGenerator(new EclipseEGL2JavaScriptDev(eglFile, part, this), null).getRelativeFileName(part);
	}

	public boolean getEditMode() {
		return false;
	}
}
