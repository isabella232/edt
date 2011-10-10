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

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.Path;
import org.eclipse.edt.compiler.internal.interfaces.IGenerationMessageRequestor;
import org.eclipse.edt.ide.compiler.gen.EclipseJavaScriptGenerator;
import org.eclipse.edt.ide.compiler.gen.JavaScriptGenerator;
import org.eclipse.edt.mof.egl.Part;
import org.eclipse.edt.mof.serialization.IEnvironment;

public class JavaScriptDevGenerator extends JavaScriptGenerator {

	public void generate(String filePath, Part part, IEnvironment env, IGenerationMessageRequestor msgRequestor) throws Exception {
		IFile file = ResourcesPlugin.getWorkspace().getRoot().getFile(new Path(filePath));
		preprocess(part);
		EclipseJavaScriptDevGenerator cmd = new EclipseJavaScriptDevGenerator(file, part, this);
		cmd.generate(buildArgs(file, part), new EclipseJavaScriptGenerator(cmd, msgRequestor), env, null);
	}

	@Override
	public String getOutputDirectory(IResource resource) {
		return Activator.OUTPUT_DIRECTORY_INTERNAL_PATH;
	}

	@Override
	protected String getRelativeFilePath(IFile eglFile, Part part) {
		return new EclipseJavaScriptGenerator(new EclipseJavaScriptDevGenerator(eglFile, part, this), null).getRelativeFileName(part);
	}
}
