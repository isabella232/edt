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
package org.eclipse.edt.gen.javascript.templates.egl.core;

import org.eclipse.edt.gen.javascript.Context;
import org.eclipse.edt.gen.javascript.templates.JavaScriptTemplate;
import org.eclipse.edt.mof.codegen.api.TabbedWriter;
import org.eclipse.edt.mof.egl.Handler;
import org.eclipse.edt.mof.egl.MemberName;
import org.eclipse.edt.mof.egl.Stereotype;

public class RUIHandlerTemplate extends JavaScriptTemplate {

	public static final String FieldName_InitialUI = "initialUI";
	public static final String FieldName_OnConstructionFunction = "onConstructionFunction";

	public void genClassHeader(Handler type, Context ctx, TabbedWriter out) {
		out.print("egl.defineRUIHandler(");
		out.print(quoted(type.getCaseSensitivePackageName().toLowerCase()));
		out.print(", ");
		out.print(quoted(type.getCaseSensitiveName()));
		out.println(", {");
		out.print(quoted("eze$$fileName"));
		out.print(" : ");
		out.print(quoted(type.getFileName()));
		out.println(",");
		out.print(quoted("eze$$runtimePropertiesFile"));
		out.print(" : ");
		out.print(quoted(type.getFullyQualifiedName().replace('.', '/')));
		out.println(",");
	}

	public void genConstructor(Handler type, Context ctx, TabbedWriter out) {
		// Generate default constructor
		out.print(quoted("constructor"));
		out.println(": function() {");

		Stereotype stereotype = type.getStereotype();
		if ((stereotype != null) && ("RUIHandler".equals(stereotype.getEClass().getCaseSensitiveName()))){
			MemberName onConstruction = (MemberName) stereotype.getValue(FieldName_OnConstructionFunction);
			if (onConstruction != null) {
				out.print("this.");
				ctx.invoke(genName, onConstruction.getMember(), ctx, out);
				out.println("();");
			}
		}

		out.println("}");
	}
}
