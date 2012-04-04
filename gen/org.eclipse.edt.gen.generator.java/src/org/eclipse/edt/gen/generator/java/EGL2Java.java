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
package org.eclipse.edt.gen.generator.java;

import org.eclipse.edt.gen.AbstractGeneratorCommand;
import org.eclipse.edt.gen.java.JavaGenerator;

public class EGL2Java extends AbstractGeneratorCommand {

	public EGL2Java() {
		super();
	}

	public static void main(String[] args) {
		// set up the command processor and pass the overriding command line options
		EGL2Java genPart = new EGL2Java();
		genPart.generate(args, new JavaGenerator(genPart), null, null);
	}
}
