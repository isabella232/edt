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
package org.eclipse.edt.gen.java.templates;

import java.util.List;

import org.eclipse.edt.gen.java.CommonUtilities;
import org.eclipse.edt.gen.java.Constants;
import org.eclipse.edt.gen.java.Context;
import org.eclipse.edt.mof.codegen.api.TabbedWriter;
import org.eclipse.edt.mof.egl.Field;
import org.eclipse.edt.mof.egl.Library;

public class LibraryTemplate extends JavaTemplate {

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
		if (!found) {
			libraries.add(library);
			CommonUtilities.generateSmapExtension(library, ctx);
		}
	}

	public void genSuperClass(Library library, Context ctx, TabbedWriter out, Object... args) {
		out.print("ExecutableBase");
	}

	public void genAccessor(Library library, Context ctx, TabbedWriter out, Object... args) {
		out.print(Constants.LIBRARY_PREFIX + library.getFullyQualifiedName().replace('.', '_') + "()");
	}

	public void genConstructor(Library library, Context ctx, TabbedWriter out, Object... args) {
		out.print("public ");
		ctx.gen(genClassName, library, ctx, out, args);
		out.print("( RunUnit ru");
		ctx.gen(genAdditionalConstructorParams, library, ctx, out, args);
		out.println(" ) {");
		out.print("super( ru");
		ctx.gen(genAdditionalSuperConstructorArgs, library, ctx, out, args);
		out.println(" );");
		out.println("ezeInitialize();");
		out.println("}");

		out.print("public ");
		genRuntimeTypeName(library, ctx, out, args);
		out.println(" " + Constants.LIBRARY_PREFIX + library.getFullyQualifiedName().replace('.', '_') + "() {");
		out.println("return this;");
		out.println("}");
	}

	public void genGetterSetter(Library library, Context ctx, TabbedWriter out, Object... args) {
		ctx.gen(genGetter, (Field) args[0], ctx, out, args);
		ctx.gen(genSetter, (Field) args[0], ctx, out, args);
	}

	public void genRuntimeTypeName(Library library, Context ctx, TabbedWriter out, Object... args) {
		ctx.gen(genPartName, library, ctx, out, args);
	}
}
