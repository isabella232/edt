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
package org.eclipse.edt.gen.generator.javascript;

import org.eclipse.edt.gen.AbstractGeneratorCommand;
import org.eclipse.edt.gen.javascript.JavaScriptGenerator;

public class EGL2JavaScript extends AbstractGeneratorCommand {

	public EGL2JavaScript() {
		super();
	}

	public static void main(String[] args) {
		// set up the command processor and pass the overriding command line options
		EGL2JavaScript genPart = new EGL2JavaScript();
		genPart.generate(args, new JavaScriptGenerator(genPart), null, null);
	}
}
