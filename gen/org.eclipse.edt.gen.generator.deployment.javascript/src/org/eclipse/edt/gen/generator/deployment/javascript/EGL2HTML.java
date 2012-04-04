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
package org.eclipse.edt.gen.generator.deployment.javascript;

import org.eclipse.edt.gen.AbstractGeneratorCommand;
import org.eclipse.edt.gen.GenerationException;
import org.eclipse.edt.gen.Generator;
import org.eclipse.edt.mof.egl.Part;
import org.eclipse.edt.mof.serialization.Environment;
import org.eclipse.edt.mof.serialization.IEnvironment;

public abstract class EGL2HTML extends AbstractGeneratorCommand {

	public EGL2HTML() {
		super();
	}

	public String generate(Part part, Generator generator, IEnvironment environment) {
		String result = null;

		try {
			if (environment != null)
				Environment.pushEnv(environment);
			// process the arguments and load the contributions
			if (initialize(buildArgs(), generator)) {
				generator.generate(part);
				// now try to write out the file, based on the output location and the part's type signature
				try {
					// only write the data, if there was some
					if (generator.getResult() instanceof String && ((String) generator.getResult()).length() > 0) {
						writeFile(part, generator);
						result = (String) generator.getResult();
					}
				}
				catch (Throwable e) {
					e.printStackTrace();
				}
				generator.dumpErrorMessages();
			}
		}
		catch (GenerationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		finally {
			if (environment != null)
				Environment.popEnv();
		}
		return result;
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
		args[7] = "org.eclipse.edt.gen.deployment.javascript.HTMLGenerationContributor";

		return args;
	}
}
