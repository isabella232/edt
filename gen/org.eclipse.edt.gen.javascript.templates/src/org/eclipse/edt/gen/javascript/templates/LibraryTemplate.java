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

import java.util.List;

import org.eclipse.edt.gen.javascript.Context;
import org.eclipse.edt.mof.codegen.api.TabbedWriter;
import org.eclipse.edt.mof.egl.Field;
import org.eclipse.edt.mof.egl.Library;
import org.eclipse.edt.mof.egl.Part;
import org.eclipse.edt.mof.egl.Type;

public class LibraryTemplate extends JavaScriptTemplate {

	public void genSuperClass(Library library, Context ctx, TabbedWriter out, Object... args) {
		out.print("ExecutableBase");
	}
	
	public void genClassBody(Library library, Context ctx, TabbedWriter out, Object... args) {
		ctx.gen(genConstructors, library, ctx, out, args);
		ctx.gen(genFunctions, library, ctx, out, args);
		ctx.gen(genFields, library, ctx, out, args);
		ctx.gen(genGetterSetters, library, ctx, out, args);
	}
	
	public void genClassHeader(Library library, Context ctx, TabbedWriter out, Object... args) {
		
		//TODO sbg consider refactoring into a separate extension
		out.print("egl.defineRUILibrary(");
		out.print(singleQuoted(library.getPackageName().toLowerCase()));
		out.print(", ");
		out.print(singleQuoted(library.getName())); 
		out.println(", ");
		out.println("{");
		out.print("'eze$$fileName': ");
		out.print(singleQuoted(library.getFileName()));
		out.println(", ");
		out.print("'eze$$runtimePropertiesFile': ");
		out.print(singleQuoted(library.getFullyQualifiedName()));
		out.println(", ");
	}

	public void genName(Library library, Context ctx, TabbedWriter out, Object... args) {
		out.print("egl." + library.getPackageName().toLowerCase() + "." + library.getName());   //TODO Use Aliaser stuff from RBD
	}

	public void genAccessor(Library library, Context ctx, TabbedWriter out, Object... args) {
		ctx.gen("genName", library, ctx, out, args);
		out.print("['$inst']");   
	}

	public void genConstructor(Library library, Context ctx, TabbedWriter out, Object... args) {
		out.print(quoted("constructor"));
		out.println(": function() {");
		
		out.print("if(");
		ctx.gen(genAccessor, library, ctx, out,args);
		out.print(") return ");
		ctx.gen(genAccessor, library, ctx, out,args);
		out.println(";");
		
		ctx.gen(genAccessor, library, ctx, out,args);
		out.println("=this;");
		
		out.println("this.jsrt$SysVar = new egl.egl.core.SysVar();");

		List<Part> usedParts = library.getUsedParts();
		for (Part part : usedParts) {
			ctx.gen(genInstantiation, part, ctx, out,args);
			out.println(";");
		}
		
		out.println("}");
	}
	
	public void genSetEmptyMethods(Library library, Context ctx, TabbedWriter out, Object... args) {
	}
	
	public void genSetEmptyMethod(Library library, Context ctx, TabbedWriter out, Object... args) {
	}
	
	public void genInitializeMethods(Library library, Context ctx, TabbedWriter out, Object... args) {
	}
	
	public void genInstantiation(Type type, Context ctx, TabbedWriter out, Object... args) {
		out.print("new ");
		ctx.gen(genName, type, ctx, out,args);
		out.print("()");
	}
	
	public void genGetterSetter(Library library, Context ctx, TabbedWriter out, Object... args) {
		ctx.gen(genGetter, (Field) args[0], ctx, out, args);
		ctx.gen(genSetter, (Field) args[0], ctx, out, args);
	}

	public void genRuntimeTypeName(Library library, Context ctx, TabbedWriter out, Object... args) {
		ctx.gen(genPartName, library, ctx, out, args);
	}
	
	public void genQualifier(Library library, Context ctx, TabbedWriter out, Object... args) {
		// No qualifier (such as "this") is required for library members
	}
}
