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

package org.eclipse.edt.gen.java.eglx.jtopen;

import org.eclipse.edt.gen.GenerationContributor;
import org.eclipse.edt.gen.GenerationRegistry;

public class IBMiGenerationContributor implements GenerationContributor {
	private static String[] templatePath;
	
	static {
		templatePath = new String[] { 
			"org.eclipse.edt.gen.java.templates.eglx.jtopen.templates"
		};
	}

	@Override
	public void contribute(GenerationRegistry generator) {
		generator.registerTemplatePath(templatePath);
	}
}
