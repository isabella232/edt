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
package org.eclipse.edt.gen.generator.example;

import org.eclipse.edt.gen.generator.java.EGL2Java;
import org.eclipse.edt.gen.java.JavaCoreGenerator;

public class EGL2Example extends EGL2Java {

	public EGL2Example() {
		// bring in the EGL2Java's parameters first
		super();
	}

	public static void main(String[] args) {
		// set up the command processor and pass the overriding command line options. The new EGL2Example() call will cause
		// the constructor for this class to get executed, and it will invoke the super() method call to set up the EGL2Java
		// defaults. The generate(args) method call will pass the command line arguments along, and set the user's
		// values based on the parameters that have been defined. Once the parameters have been analyzed and accepted,
		// generation can begin. The new JavaCoreGenerator(genPart) defines the generator to use. You can use the default one, or
		// provide your own generator that extends the default.
		EGL2Example genPart = new EGL2Example();
		genPart.generate(args, new JavaCoreGenerator(genPart), null, null);
	}
}
