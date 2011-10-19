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
import java.util.List;
import java.util.Set;

import org.eclipse.edt.compiler.ISystemEnvironment;
import org.eclipse.edt.gen.AbstractGeneratorCommand;
import org.eclipse.edt.gen.deployment.util.PartReferenceCache;
import org.eclipse.edt.mof.egl.Part;

public abstract class ValidHTMLGenerator extends HTMLGenerator {

	protected List egldds;
	protected HashMap eglParameters = new HashMap();
	protected String userMsgLocale;
	protected String runtimeMsgLocale;
	protected Set<String> propFiles;
	protected PartReferenceCache partRefCache;
	
	public ValidHTMLGenerator(AbstractGeneratorCommand processor, List egldds, Set<String> propFiles, HashMap eglParameters, String userMsgLocale, String runtimeMsgLocale, ISystemEnvironment sysEnv,
			PartReferenceCache partRefCache) {
		super(processor, null, sysEnv);
		this.egldds = egldds;
		this.eglParameters = eglParameters;
		this.userMsgLocale = userMsgLocale;
		this.runtimeMsgLocale = runtimeMsgLocale;
		this.propFiles = propFiles;
		this.partRefCache = partRefCache;
	}

	protected void invokeGeneration(Part part, String methodName) {
		context.invoke(methodName, part, context, out, egldds, propFiles, eglParameters, userMsgLocale, runtimeMsgLocale, partRefCache);
	}

}
