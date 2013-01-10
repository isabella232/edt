/*******************************************************************************
 * Copyright © 2011, 2013 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.edt.ide.compiler.gen;

import org.eclipse.edt.compiler.internal.interfaces.IGenerationMessageRequestor;
import org.eclipse.edt.gen.AbstractGeneratorCommand;
import org.eclipse.edt.gen.java.JavaCoreGenerator;

/**
 * Subclass of the base Java generator to do certain things in the Eclipse way.
 */
public class EclipseJavaCoreGenerator extends JavaCoreGenerator {

	public EclipseJavaCoreGenerator(AbstractGeneratorCommand processor, IGenerationMessageRequestor requestor) {
		super(processor, requestor);
	}
	
	/**
	 * Override how the smap file is generated. This needs to use Eclipse API to write the file so that
	 * the Eclipse filesystem stays in sync.
	 */
	@Override
	public void processFile(String fileName) {
		// do any post processing once the file has been written
	}
	
	public void dumpErrorMessages() {
		// Do nothing. Errors will be reported in the IDE after generation is complete.
	}
}
