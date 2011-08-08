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
package org.eclipse.edt.gen.javascriptdev.templates;

import org.eclipse.edt.gen.javascript.Context;
import org.eclipse.edt.mof.codegen.api.TabbedWriter;
import org.eclipse.edt.mof.egl.Annotation;
import org.eclipse.edt.mof.egl.Statement;

public class StatementTemplate extends org.eclipse.edt.gen.javascript.templates.StatementTemplate {

	public void genStatement(Statement stmt, Context ctx, TabbedWriter out) {
		Annotation annotation = stmt.getAnnotation("EGL_Location");
		if (annotation != null){
			Integer line = (Integer)annotation.getValue("line"); 		//TODO sbg Need constant
			Integer offset = (Integer)annotation.getValue( "off" );		//TODO sbg Need constant
			Integer length = (Integer)annotation.getValue( "len" );		//TODO sbg Need constant
			out.println( "egl.atLine(this.eze$$fileName," + line + ","
				+ offset + "," + length + ", this);" );
		}
		super.genStatement(stmt, ctx, out);
	}
}
