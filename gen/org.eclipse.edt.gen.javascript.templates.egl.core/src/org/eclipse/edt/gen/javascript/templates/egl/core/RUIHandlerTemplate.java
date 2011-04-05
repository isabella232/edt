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
package org.eclipse.edt.gen.javascript.templates.egl.core;

import org.eclipse.edt.gen.javascript.Context;
import org.eclipse.edt.gen.javascript.templates.JavascriptTemplate;
import org.eclipse.edt.mof.codegen.api.TabbedWriter;
import org.eclipse.edt.mof.egl.Element;
import org.eclipse.edt.mof.egl.Field;
import org.eclipse.edt.mof.egl.Handler;
import org.eclipse.edt.mof.egl.MemberName;
import org.eclipse.edt.mof.egl.Stereotype;

public class RUIHandlerTemplate extends JavascriptTemplate {
	
	public static final String FieldName_InitialUI = "initialUI";
	public static final String FieldName_OnConstructionFunction = "onConstructionFunction";

	public void genClassHeader(Handler type, Context ctx, TabbedWriter out, Object... args) {
		out.print("egl.defineRUIHandler(");
		out.print(quoted(type.getPackageName()));
		out.print(", ");
		out.print(quoted(type.getName()));
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

	public void genClassBody(Handler type, Context ctx, TabbedWriter out, Object... args) {
		ctx.gen(genConstructor, (Element) type, ctx, out);
		ctx.gen(genFunctions, (Element) type, ctx, out);
		out.println("};");
	}

	public void genConstructor(Handler type, Context ctx, TabbedWriter out, Object... args) {
		// Generate default constructor
		Stereotype stereotype = (Stereotype) args[0];
		out.print(quoted("constructor"));
		out.println(": function() {");
		out.print("this.");
		out.print("eze$$XMLRootElementName = ");
		out.print(quoted(type.getName()));
		out.println(";");
		for (Field field : type.getFields()) {
			if (field.getInitializerStatements() != null)
				ctx.gen(genStatementNoBraces, field.getInitializerStatements(), ctx, out, args);
		}
		// TODO: initialUI value is not stored in RUIHandler stereotype as it is not
		// handled properly in the bindings front end that has this value. Assume
		// for now the initialUI is the field named 'ui'
		// List<MemberName> initialUI = (List<MemberName>)stereotype.getValue(FieldName_InitialUI);
		Field initialUI = type.getField("ui");
		if (initialUI != null) {
			out.print("this.initialUI = [ ");
			// ctx.foreach(initialUI, ',', genExpression, ctx, out, args);
			out.print("this.ui");
			out.println(" ];");
		}
		MemberName onConstruction = (MemberName) stereotype.getValue(FieldName_OnConstructionFunction);
		if (onConstruction != null) {
			out.print("this.");
			ctx.gen(genName, onConstruction.getMember(), ctx, out);
			out.println("();");
		}
		out.println("}");
	}
}
