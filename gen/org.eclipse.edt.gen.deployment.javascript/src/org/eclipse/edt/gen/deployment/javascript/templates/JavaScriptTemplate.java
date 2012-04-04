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
package org.eclipse.edt.gen.deployment.javascript.templates;

import org.eclipse.edt.mof.codegen.api.AbstractTemplate;

public abstract class JavaScriptTemplate extends AbstractTemplate {
	
	public static final String genOutputFileName = "genOutputFileName";
	public static final String genDeploymentHTML = "genDeploymentHTML";
	public static final String genDependentParts = "genDependentParts";
	public static final String genDependentPart = "genDependentPart";
	public static final String genErrorHTML = "genErrorHTML";
	public static final String genCompileErrorHTML = "genCompileErrorHTML";
	public static final String genDevelopmentHTML = "genDevelopmentHTML";
	
	public static final String genDependentCSSs = "genDependentCSSs";
	public static final String genCSSFiles = "genCSSFiles";
	
	public static final String genDependentProps = "genDependentProps";
	
	public static final String genBindFiles = "genBindFiles";
	
	public static final String genDependentIncludeFiles = "genDependentIncludeFiles";
	public static final String genIncludeFiles = "genIncludeFiles";
}
