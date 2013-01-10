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
package org.eclipse.edt.gen.deployment.javascript;

import java.util.HashMap;
import java.util.List;

import org.eclipse.edt.gen.AbstractGeneratorCommand;
import org.eclipse.edt.gen.GenerationException;
import org.eclipse.edt.gen.deployment.javascript.templates.JavaScriptTemplate;
import org.eclipse.edt.mof.egl.Part;

public abstract class DevelopmentHTMLGenerator extends ValidHTMLGenerator {	
	
	public DevelopmentHTMLGenerator(AbstractGeneratorCommand processor, List egldds,
			HashMap eglParameters, String userMsgLocale, String runtimeMsgLocale) {
		super(processor, egldds, eglParameters, userMsgLocale, runtimeMsgLocale);
	}

	public void generate(Object part) throws GenerationException {
		this.generate((Part) part, JavaScriptTemplate.genDevelopmentHTML );
	}
	
	protected void invokeGeneration(Part part, String methodName) {
		context.invoke(methodName, part, context, out, egldds, eglParameters, userMsgLocale, runtimeMsgLocale, getEnableEditing(), isContextAware(), isDebug());
	}
	
	/**
	 * Used to indicate if a file is being displayed in the Design Tab of the RUI VE
	 */
	protected boolean getEnableEditing(){
		return false;
	}
	
	protected abstract boolean isContextAware();
	
	protected boolean isDebug() {
		return false;
	}
}
