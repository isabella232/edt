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
		// TODO Auto-generated constructor stub
	}

	@Override
	public String[] getNativeTypePath() {
		// TODO Auto-generated method stub
		return new String[0];
	}

	@Override
	public String[] getPrimitiveTypePath() {
		// TODO Auto-generated method stub
		return new String[0];
	}

	@Override
	public String[] getEGLMessagePath() {
//		return new String[0];
		return new String[] { "org.eclipse.edt.gen.deployment.javascript.EGLMessages" };
	}

	@Override
	public String[] getTemplatePath() {
		return new String[] { "org.eclipse.edt.gen.deployment.javascript.templates.templates" };
	}
	
	public String generate(Part part, Generator generator, IEnvironment environment) {
		String result = null;
		
		if (environment != null)
			Environment.pushEnv(environment);
		try {			
			generator.generate(part);
			// now try to write out the file, based on the output location and the part's type signature
			try {
				// only write the data, if there was some
				if (generator.getResult() instanceof String && ((String)generator.getResult()).length() > 0){
					writeFile(part, generator);
					result = (String)generator.getResult();
				}
					
			}
			catch (Throwable e) {
				e.printStackTrace();
			}						
			generator.dumpErrorMessages();
		}
		catch (GenerationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
		finally {
			if (environment != null) {
				Environment.popEnv();
			}
		}
		
		return result;
	}
	

}
