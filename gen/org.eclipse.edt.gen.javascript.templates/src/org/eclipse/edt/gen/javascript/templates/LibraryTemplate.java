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

import org.eclipse.edt.gen.javascript.Constants;
import org.eclipse.edt.gen.javascript.Context;
import org.eclipse.edt.mof.codegen.api.TabbedWriter;
import org.eclipse.edt.mof.egl.Field;
import org.eclipse.edt.mof.egl.Library;
import org.eclipse.edt.mof.egl.Part;
import org.eclipse.edt.mof.egl.Type;

public class LibraryTemplate extends JavaScriptTemplate {

	@SuppressWarnings("unchecked")
	public void validate(Library library, Context ctx, Object... args) {
		// process anything else the superclass needs to do
		ctx.validateSuper(validate, Library.class, library, ctx, args);
		// when we get here, it is because a part is being referenced by the original part being validated. Add it to the
		// parts used table if it doesn't already exist
		boolean found = false;
		List<Library> libraries = (List<Library>) ctx.getAttribute(ctx.getClass(), Constants.Annotation_partLibrariesUsed);
		for (Library lib : libraries) {
			if (library.getTypeSignature().equalsIgnoreCase(lib.getTypeSignature())) {
				found = true;
				break;
			}
		}
		if (!found)
			libraries.add(library);
	}

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
		// TODO sbg consider refactoring into a separate extension
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
		// TODO Use Aliaser stuff from RBD
		out.print("egl." + library.getPackageName().toLowerCase() + "." + library.getName());
	}

	public void genAccessor(Library library, Context ctx, TabbedWriter out, Object... args) {
		ctx.gen(genName, library, ctx, out, args);
		out.print("['$inst']");
	}

	@SuppressWarnings("unchecked")
	public void genConstructor(Library library, Context ctx, TabbedWriter out, Object... args) {
		out.print(quoted("constructor"));
		out.println(": function() {");
		out.print("if(");
		ctx.gen(genAccessor, library, ctx, out, args);
		out.print(") return ");
		ctx.gen(genAccessor, library, ctx, out, args);
		out.println(";");
		ctx.gen(genAccessor, library, ctx, out, args);
		out.println("=this;");
		out.println("this.jsrt$SysVar = new egl.egl.core.SysVar();");
		// instantiate each user part
		List<Part> usedParts = library.getUsedParts();
		for (Part part : usedParts) {
			ctx.gen(genInstantiation, part, ctx, out, args);
			out.println(";");
		}
		// instantiate each system library
		List<Library> libraries = (List<Library>) ctx.getAttribute(ctx.getClass(), Constants.Annotation_partLibrariesUsed);
		for (Library part : libraries) {
			// if this is a system library, then generate code
			if (ctx.mapsToNativeType((Type) part)) {
				ctx.gen(genInstantiation, part, ctx, out, args);
				out.println(";");
			}
		}
		out.println("}");
	}

	public void genSetEmptyMethods(Library library, Context ctx, TabbedWriter out, Object... args) {}

	public void genSetEmptyMethod(Library library, Context ctx, TabbedWriter out, Object... args) {}

	public void genInitializeMethods(Library library, Context ctx, TabbedWriter out, Object... args) {}

	public void genInstantiation(Type type, Context ctx, TabbedWriter out, Object... args) {
		out.print("new ");
		ctx.gen(genName, type, ctx, out, args);
		out.print("()");
	}

	public void genGetterSetter(Library library, Context ctx, TabbedWriter out, Object... args) {
		ctx.gen(genGetter, (Field) args[0], ctx, out, args);
		ctx.gen(genSetter, (Field) args[0], ctx, out, args);
	}

	public void genRuntimeTypeName(Library library, Context ctx, TabbedWriter out, Object... args) {
		ctx.gen(genPartName, library, ctx, out, args);
	}

	public void genContainerBasedAccessor(Library library, Context ctx, TabbedWriter out, Object... args) {
		out.print("new egl.egl.jsrt.Delegate(");
		ctx.gen(genAccessor, library, ctx, out, args);
		out.print(",");
		ctx.gen(genName, library, ctx, out, args);
		out.print(".prototype.");
		ctx.gen(genName, args[0], ctx, out, args);
		out.print(")");
	}
}
