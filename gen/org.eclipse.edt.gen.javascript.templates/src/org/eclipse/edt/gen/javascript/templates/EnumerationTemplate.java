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
package org.eclipse.edt.gen.javascript.templates;

import java.util.List;

import org.eclipse.edt.gen.javascript.Context;
import org.eclipse.edt.gen.javascript.JavaScriptAliaser;
import org.eclipse.edt.mof.EEnumLiteral;
import org.eclipse.edt.mof.codegen.api.TabbedWriter;
import org.eclipse.edt.mof.egl.Enumeration;
import org.eclipse.edt.mof.egl.Type;

public class EnumerationTemplate extends JavaScriptTemplate {

	public void preGenClassBody(Enumeration part, Context ctx) {}

	public void genPart(Enumeration part, Context ctx, TabbedWriter out) {
		ctx.invoke(genClassHeader, (Type)part, ctx, out);
		out.pushIndent();
		ctx.invoke(genClassBody, (Type)part, ctx, out);
		out.println("}");
		out.popIndent();
		out.println(");");
		List<EEnumLiteral> enums = part.getEntries();
		if (enums != null && enums.size() != 0) {
			for (int idx = 0; idx < enums.size(); idx++) {
				ctx.invoke(genRuntimeTypeName, (Type)part, ctx, out, TypeNameKind.JavascriptImplementation);
				out.print("['");
				ctx.invoke(genName, enums.get(idx), ctx, out);
				out.print("'] = ");
				ctx.putAttribute(part, genConstructorOptions, enums.get(idx).getValue() == 0 ? Integer.valueOf(idx + 1) : Integer.valueOf(enums.get(idx).getValue())  );
				ctx.invoke(genInstantiation, (Type)part, ctx, out);
				out.println(";");
			}
		}
		out.println(";");
	}

	public void genConstructorOptions(Enumeration type, Context ctx, TabbedWriter out) {
		Integer literalValue = (Integer)ctx.getAttribute((Type)type, genConstructorOptions);
		out.print(((Integer)literalValue).toString());
	}

	public void genConstructor(Enumeration part, Context ctx, TabbedWriter out) {
		// Generate default constructor
		out.print(quoted("constructor"));
		out.println(": function(valueIn) {");
		out.println("this.value = valueIn;");
		out.println("}");
	}

	public void genDefaultValue(Enumeration part, Context ctx, TabbedWriter out) {
		out.print("null");
	}
	public void genClassBody(Enumeration part, Context ctx, TabbedWriter out) {
		ctx.invoke(genConstructors, (Type)part, ctx, out);
	}
	
	public void genConstructors(Enumeration part, Context ctx, TabbedWriter out) {
		ctx.invoke(genConstructor, (Type)part, ctx, out);
	}

	public void genClassHeader(Enumeration part, Context ctx, TabbedWriter out) {
		out.print("egl.defineClass(");
		out.print(singleQuoted(part.getPackageName().toLowerCase()));
		out.print(", ");
		out.print(quoted(part.getName()));
		out.print(", \"eglx.lang\", \"Enumeration\",");
		out.println("{");
	}

	public void genAccessor(Enumeration part, Context ctx, TabbedWriter out) {
		ctx.invoke(genRuntimeTypeName, (Type)part, ctx, out, TypeNameKind.JavascriptImplementation);
	}
	
	public void genModuleName(Enumeration part, StringBuilder buf) {
		buf.append("\"");
		String pkg = part.getPackageName();
		if (pkg.length() > 0) {
			buf.append(JavaScriptAliaser.packageNameAlias(pkg.split("[.]"), '/'));
			buf.append('/');
		}
		buf.append(JavaScriptAliaser.getAlias(part.getId()));
		buf.append("\"");
	}

}
