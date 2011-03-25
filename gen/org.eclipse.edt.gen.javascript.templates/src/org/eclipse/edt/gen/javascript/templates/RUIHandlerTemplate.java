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
package org.eclipse.edt.gen.javascript.templates;

import org.eclipse.edt.gen.javascript.Context;
import org.eclipse.edt.mof.codegen.api.TabbedWriter;
import org.eclipse.edt.mof.codegen.api.TemplateException;
import org.eclipse.edt.mof.egl.Element;
import org.eclipse.edt.mof.egl.Field;
import org.eclipse.edt.mof.egl.Handler;
import org.eclipse.edt.mof.egl.MemberName;
import org.eclipse.edt.mof.egl.Stereotype;

public class RUIHandlerTemplate extends StereotypeTemplate {
	public static final String FieldName_InitialUI = "initialUI";
	public static final String FieldName_OnConstructionFunction = "onConstructionFunction";

	public void genPart(Stereotype stereotype, Context ctx, TabbedWriter out, Object...args) {
		Handler clazz = (Handler)args[0];
		genDefineClause(clazz, ctx, out, stereotype);
		genClassBody(clazz, ctx, out, stereotype);
		out.println("};");
	}
	
	public void genDefineClause(Handler clazz, Context ctx, TabbedWriter out, Object...args) {
		out.print("egl.defineRUIHandler(");
		out.print(quoted(clazz.getPackageName()));
		out.print(", ");
		out.print(quoted(clazz.getName()));
		out.println(", {");
		out.print(quoted("eze$$fileName"));
		out.print(" : ");
		out.print(quoted(clazz.getFileName()));
		out.println(",");
		out.print(quoted("eze$$runtimePropertiesFile"));
		out.print(" : ");
		out.print(quoted(clazz.getFullyQualifiedName().replace('.', '/')));
		out.println(",");
	}
	
	public void genClassBody(Handler clazz, Context ctx, TabbedWriter out, Object...args) {
		genDefaultConstructor(clazz, ctx, out, args);
		ctx.gen(genFunctions, (Element)clazz, ctx, out);
	}

	public void genDefaultConstructor(Handler part, Context ctx, TabbedWriter out, Object... args) throws TemplateException {
		// Generate default constructor
		Stereotype stereotype = (Stereotype)args[0];
		out.print(quoted("constructor"));
		out.println(": function() {");
		out.print("this.");
		out.print("eze$$XMLRootElementName = ");
		out.print(quoted(part.getName()));
		out.println(';');
		for (Field field : part.getFields()) {
			if (field.getInitializerStatements() != null)
				ctx.gen(genStatementNoBraces, field.getInitializerStatements(), ctx, out, args);
		}
		// TODO: initialUI value is not stored in RUIHandler stereotype as it is not
		// handled properly in the bindings front end that has this value.  Assume
		// for now the initialUI is the field named 'ui'
		// List<MemberName> initialUI = (List<MemberName>)stereotype.getValue(FieldName_InitialUI);
		Field initialUI = part.getField("ui");
		if (initialUI != null) {
			out.print("this.initialUI = [ ");
			// ctx.foreach(initialUI, ',', genExpression, ctx, out, args);
			out.print("this.ui");
			out.println(" ];");
		}
		MemberName onConstruction = (MemberName)stereotype.getValue(FieldName_OnConstructionFunction);
		if (onConstruction != null) {
			out.print("this.");
			genName(onConstruction.getMember(), ctx, out);
			out.println("();");
		}
		out.println("}");
	}


}
