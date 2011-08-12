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
import org.eclipse.edt.gen.javascriptdev.Constants;
import org.eclipse.edt.mof.codegen.api.TabbedWriter;
import org.eclipse.edt.mof.egl.Field;
import org.eclipse.edt.mof.egl.Record;

public class RecordTemplate extends org.eclipse.edt.gen.javascript.templates.RecordTemplate {

	public void genGetName(Record part, Context ctx, TabbedWriter out) {
		out.println(",");
		out.print(quoted("eze$$getName"));
		out.println(": function() {");
		out.println("return \"" + part.getFullyQualifiedName() + "\";");
		out.println("}");
	}
	
	public void genGetVariables(Record part, Context ctx, TabbedWriter out) {
		out.println(",");
		out.print(quoted("eze$$getChildVariables"));
		out.println(": function() {");
		out.println("var eze$$parent = this;");
		out.print("return [");
		
		boolean first = true;
		for (Field field : part.getFields()) {
			if (first) {
				first = false;
				out.print("\n");
			}
			else {
				out.print(",\n");
			}
			
			ctx.invoke(Constants.genGetVariablesEntry, field, ctx, out);
		}
		
		out.println("\n];");
		out.println("}");
	}
}
