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
package org.eclipse.edt.gen.javascriptdev;

import org.eclipse.edt.gen.generator.javascript.EGL2JavaScript;
import org.eclipse.edt.gen.javascript.JavaScriptGenerator;

public class JavaScriptDevGenerator extends EGL2JavaScript {

	public JavaScriptDevGenerator() {
		// bring in the EGL2JavaScript's parameters first
		super();
	}

	public static void main(String[] args) {
		// set up the command processor and pass the overriding command line options. The new JavaScriptDevGenerator() call will cause
		// the constructor for this class to get executed, and it will invoke the super() method call to set up the EGL2Java
		// defaults. The generate(args) method call will pass the command line arguments along, and set the user's
		// values based on the parameters that have been defined. Once the parameters have been analyzed and accepted,
		// generation can begin. The new JavaGenerator(genPart) defines the generator to use. You can use the default one, or
		// provide your own generator that extends the default.
		JavaScriptDevGenerator genPart = new JavaScriptDevGenerator();
		genPart.generate(args, new JavaScriptGenerator(genPart), null, null);
	}
}
