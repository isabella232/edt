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
package org.eclipse.edt.gen.deployment.javascript;

import java.util.HashMap;
import java.util.List;

import org.eclipse.edt.gen.AbstractGeneratorCommand;
import org.eclipse.edt.mof.egl.Part;

public abstract class ValidHTMLGenerator extends HTMLGenerator {

	protected List egldds;
	protected HashMap eglParameters = new HashMap();
	protected String userMsgLocale;
	protected String runtimeMsgLocale;
	
	public ValidHTMLGenerator(AbstractGeneratorCommand processor, List egldds, HashMap eglParameters, String userMsgLocale, String runtimeMsgLocale) {
		super(processor, null);
		this.egldds = egldds;
		this.eglParameters = eglParameters;
		this.userMsgLocale = userMsgLocale;
		this.runtimeMsgLocale = runtimeMsgLocale;
	}

	protected void invokeGeneration(Part part, String methodName) {
		context.invoke(methodName, part, context, out, egldds, eglParameters, userMsgLocale, runtimeMsgLocale);
	}

}
