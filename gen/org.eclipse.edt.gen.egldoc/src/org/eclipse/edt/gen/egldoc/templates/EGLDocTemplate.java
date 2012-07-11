/*******************************************************************************
 * Copyright © 2012 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.edt.gen.egldoc.templates;

import org.eclipse.edt.mof.codegen.api.AbstractTemplate;

public class EGLDocTemplate extends AbstractTemplate {
	
	// Constants that represent all the method names invoked using the dynamic Template.gen() methods
	// This allows one to find all references to invocations of the methods being invoked dynamically
	public static final String genPart = "genPart";
	public static final String genBody = "genBody";
	
	public static final String genHead = "genHead";
	public static final String genTop  = "genTop";
	// these are used by the validation step. preGen is used to preGen individual items within the part being generated.
	// preGenPart is invoked by the generator and should not be overridden or used by extending logic
	public static final String preGenPart = "preGenPart";
}
