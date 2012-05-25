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
package org.eclipse.edt.ide.deployment.gen;

import org.eclipse.core.resources.IFile;
import org.eclipse.edt.gen.Generator;
import org.eclipse.edt.gen.generator.deployment.javascript.EGL2HTML;
import org.eclipse.edt.ide.core.utils.EclipseUtilities;
import org.eclipse.edt.mof.egl.Part;

public class EclipseEGL2HTML extends EGL2HTML {
	
	private final IFile eglFile;

	public EclipseEGL2HTML(IFile eglFile) {
		super();
		this.eglFile = eglFile;
	}

	protected void writeFile(Part part, Generator generator) throws Exception {
		String outputFolder = "WebContent";		
		if (EclipseUtilities.shouldWriteFileInEclipse(outputFolder)) {
			IFile outputFile = EclipseUtilities.writeFileInEclipse(outputFolder, eglFile, generator.getResult().toString(), generator.getRelativeFileName(part));
			// call back to the generator, to see if it wants to do any supplementary tasks
			generator.processFile(outputFile.getFullPath().toString());
		} else {
			// super's method handles writing to an absolute file system path.
			super.writeFile(part, generator);
		}
	}
}
