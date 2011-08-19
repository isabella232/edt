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
package org.eclipse.edt.gen.deployment.javascript;

import java.util.HashMap;

import org.eclipse.edt.gen.AbstractGeneratorCommand;
import org.eclipse.edt.gen.GenerationException;
import org.eclipse.edt.gen.deployment.javascript.templates.JavaScriptTemplate;
import org.eclipse.edt.mof.egl.Part;

public class DeploymentHTMLGenerator extends ValidHTMLGenerator {
	
	// TODO Need to be removed
	public DeploymentHTMLGenerator(AbstractGeneratorCommand processor) {
		super(processor, null);
	}
	
	public DeploymentHTMLGenerator(AbstractGeneratorCommand processor, String egldd, HashMap eglParameters, String userMsgLocale, String runtimeMsgLocale ) {
		super(processor, egldd, eglParameters, userMsgLocale, runtimeMsgLocale);
	}
	
	@Override
	public void generate(Part part) throws GenerationException {
		this.generate(part, JavaScriptTemplate.genPart);
	}

}
