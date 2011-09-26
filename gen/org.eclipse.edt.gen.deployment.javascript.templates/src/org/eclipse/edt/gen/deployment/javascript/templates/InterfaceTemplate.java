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
package org.eclipse.edt.gen.deployment.javascript.templates;

import java.util.LinkedHashSet;

import org.eclipse.edt.gen.deployment.javascript.Context;
import org.eclipse.edt.mof.egl.Interface;

public class InterfaceTemplate extends JavaScriptTemplate {
	
	public void genOutputFileName(Interface part, LinkedHashSet dependentFiles) {
		// Do nothing.
	}
	
	public void genDependentParts(Interface part, Context ctx, LinkedHashSet dependentFiles, LinkedHashSet handledParts) {
		// Do nothing.
	}
	
	public void genDependentIncludeFiles(Interface part, Context ctx, LinkedHashSet includeFiles, LinkedHashSet handledParts) {
		// Do nothing.
	}
	
	public void genDependentCSSs(Interface part, Context ctx, LinkedHashSet cssFiles, LinkedHashSet handledParts) {
		// Do nothing.
	}
	
	public void genDependentProps(Interface part, Context ctx, LinkedHashSet propFiles, LinkedHashSet handledParts) {
		// Do nothing.
	}
}
