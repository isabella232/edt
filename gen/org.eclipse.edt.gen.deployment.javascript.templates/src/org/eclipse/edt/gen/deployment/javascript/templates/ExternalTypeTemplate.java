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

import org.eclipse.edt.gen.deployment.util.CommonUtilities;
import org.eclipse.edt.mof.egl.Annotation;
import org.eclipse.edt.mof.egl.ExternalType;

public class ExternalTypeTemplate extends JavaScriptTemplate {
	
	public void genOutputFileName(ExternalType et, LinkedHashSet<String> dependentFiles) {
		Annotation annot = et.getAnnotation( "eglx.javascript.JavaScriptObject" );
		if (annot != null) {
			String pkg = (String)annot.getValue( "relativePath" );
			String name = (String)annot.getValue( "externalName" );
			
			if (pkg == null || pkg.isEmpty()) {
				pkg = et.getPackageName().replace( '.', '/' );
			}
			if (name == null || name.isEmpty()) {
				name = et.getName();
			}
			String output = "";
			output += "\"";
			if (pkg.length() > 0) {
				output += pkg ;
				if (pkg.charAt(pkg.length() - 1) != '/') {
					output += '/' ;
				}
			}
			output += name ;
			
			output += ".js\"";
			CommonUtilities.addToDependentList(dependentFiles, output);
		}
	}
	
	public void genIncludeFiles(ExternalType et, LinkedHashSet<String> includeFiles){
		Annotation a = et.getAnnotation( "eglx.javascript.JavaScriptObject" );
		if ( a != null && a.getValue( "includeFile" ) != null ){
			String fileName = a.getValue( "includeFile" ).toString();
			if ( fileName != null && fileName.length() > 0 ){
				includeFiles.add(fileName);
			}
		}
	}
	
}
