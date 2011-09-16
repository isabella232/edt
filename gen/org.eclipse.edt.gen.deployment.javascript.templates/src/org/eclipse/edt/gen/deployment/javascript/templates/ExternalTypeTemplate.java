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
import java.util.Set;

import org.eclipse.edt.gen.deployment.javascript.Context;
import org.eclipse.edt.gen.deployment.util.CommonUtilities;
import org.eclipse.edt.mof.codegen.api.TabbedWriter;
import org.eclipse.edt.mof.egl.Annotation;
import org.eclipse.edt.mof.egl.ExternalType;
import org.eclipse.edt.mof.egl.Handler;
import org.eclipse.edt.mof.egl.Part;
import org.eclipse.edt.mof.egl.utils.IRUtils;

public class ExternalTypeTemplate extends JavaScriptTemplate {
	
	public void genOutputFileName(ExternalType et, Context ctx, LinkedHashSet dependentFiles) {
		Annotation annot = et.getAnnotation( "eglx.javascript.JavaScriptObject" );
		if (annot != null) {
			String pkg = (String)annot.getValue( "relativePath" );
			String name = (String)annot.getValue( "externalName" );
			
			if (pkg == null) {
				pkg = et.getPackageName().replace( '.', '/' );
			}
			if (name == null) {
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
			dependentFiles.add(output);
		}
	}
	
	public void genIncludeFiles(ExternalType et, TabbedWriter out, LinkedHashSet includeFiles){
		Annotation a = et.getAnnotation( "eglx.javascript.JavaScriptObject" );
		if ( a != null && a.getValue( "includeFile" ) != null ){
			String fileName = a.getValue( "includeFile" ).toString();
			if ( fileName != null && fileName.length() > 0 ){
				includeFiles.add(fileName);
			}
		}
	}
	
}
