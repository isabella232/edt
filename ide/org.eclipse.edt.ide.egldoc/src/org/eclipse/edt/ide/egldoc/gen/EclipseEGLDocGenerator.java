/*******************************************************************************
 * Copyright © 2012, 2013 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.edt.ide.egldoc.gen;

import org.eclipse.edt.compiler.internal.interfaces.IGenerationMessageRequestor;
import org.eclipse.edt.gen.AbstractGeneratorCommand;

/**
 * Subclass of the base EGLDoc generator to do certain things in the Eclipse way.
 */
public class EclipseEGLDocGenerator extends org.eclipse.edt.gen.egldoc.EGLDocGenerator {

	public EclipseEGLDocGenerator(AbstractGeneratorCommand processor, IGenerationMessageRequestor requestor) {
		super(processor, requestor);
	}

	@Override
	public void processFile(String fileName) {
	}
	
	@Override
	public void dumpErrorMessages() {
		// Do nothing.
	}
}
