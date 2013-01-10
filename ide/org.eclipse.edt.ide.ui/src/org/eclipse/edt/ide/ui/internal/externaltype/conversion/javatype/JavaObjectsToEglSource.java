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
package org.eclipse.edt.ide.ui.internal.externaltype.conversion.javatype;

import org.eclipse.edt.gen.AbstractGeneratorCommand;
import org.eclipse.edt.gen.generator.eglsource.EglSourceGenerator;
import org.eclipse.edt.mof.serialization.Environment;
import org.eclipse.edt.mof.serialization.IEnvironment;

public class JavaObjectsToEglSource extends AbstractGeneratorCommand {
	public JavaObjectsToEglSource() {
		super();
	}

	public void generate(Object object, EglSourceGenerator generator, IEnvironment environment) {
		try {
			if (environment != null){
				Environment.pushEnv(environment);
			}
			
			// process the arguments and load the contributions
			if (initialize(buildArgs(), generator)) {
				// start up the generator, passing the command processor
				try {				
					generator.generate(object);
				}
				catch (Exception e) {
					e.printStackTrace();
					if (generator != null)
						System.out.print(generator.getResult());
				}
				generator.dumpErrorMessages();
			}
		}
		catch (Exception e) {
			System.out.print(e.getMessage());
		}
		finally {
			if (environment != null){
				Environment.popEnv();
			}
		}
	}
	
	private String[] buildArgs() {
		String[] args = new String[8];

		// this isn't used but it's a required parameter.
		args[0] = "-o";
		args[1] = "notused";

		// this isn't used but it's a required parameter.
		args[2] = "-p";
		args[3] = "notused";

		// this isn't used but it's a required parameter.
		args[4] = "-r";
		args[5] = "notused";

		// add the contribution
		args[6] = "-c";
		args[7] = "org.eclipse.edt.ide.ui.internal.externaltype.conversion.javatype.JavaObjectsToEglContributor";

		return args;
	}
}
