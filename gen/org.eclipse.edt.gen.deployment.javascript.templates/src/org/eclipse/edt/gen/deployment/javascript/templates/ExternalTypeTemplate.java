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

import org.eclipse.edt.gen.deployment.javascript.Context;
import org.eclipse.edt.mof.codegen.api.TabbedWriter;
import org.eclipse.edt.mof.egl.Annotation;
import org.eclipse.edt.mof.egl.ExternalType;

public class ExternalTypeTemplate extends JavaScriptTemplate {
	
	public void genDependentPart(ExternalType et, Context ctx, TabbedWriter out, Boolean addComma) {
		ctx.invoke(genOutputFileName, et, ctx, out, addComma);
		ctx.invoke(genDependentParts, et, ctx, out, Boolean.TRUE);
	}
	
	public void genOutputFileName(ExternalType et, Context ctx, TabbedWriter out, Boolean addComma) {
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
			
			if(addComma){
				out.print(", ");
			}
			
			out.print("\"");
			if (pkg.length() > 0) {
				out.print( pkg );
				if (pkg.charAt(pkg.length() - 1) != '/') {
					out.print( '/' );
				}
			}
			out.print( name );
			
			out.print(".js\"");
		}
	}
}
