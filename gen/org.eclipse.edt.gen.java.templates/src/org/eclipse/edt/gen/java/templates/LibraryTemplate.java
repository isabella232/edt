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
import org.eclipse.edt.mof.egl.Part;

public class LibraryTemplate extends JavaTemplate {

	@SuppressWarnings("unchecked")
	public void preGen(Library library, Context ctx) {
		// process anything else the superclass needs to do
		ctx.invokeSuper(this, preGen, library, ctx);
		// ignore adding this entry to the list, if it is the part we are currently generating
		if (((Part) ctx.getAttribute(ctx.getClass(), Constants.SubKey_partBeingGenerated)).getFullyQualifiedName().equalsIgnoreCase(
			library.getFullyQualifiedName()))
			return;
		// when we get here, it is because a part is being referenced by the original part being generated. Add it to the
		// parts used table if it doesn't already exist
		boolean found = false;
		List<Library> libraries = (List<Library>) ctx.getAttribute(ctx.getClass(), Constants.SubKey_partLibrariesUsed);
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

	public void genSuperClass(Library library, Context ctx, TabbedWriter out) {
		out.print("ExecutableBase");
	}

	public void genAccessor(Library library, Context ctx, TabbedWriter out) {
		out.print(Constants.LIBRARY_PREFIX + library.getFullyQualifiedName().replace('.', '_') + "()");
	}

	public void genConstructor(Library library, Context ctx, TabbedWriter out) {
		out.print("public ");
		ctx.invoke(genClassName, library, ctx, out);
		out.print("( RunUnit ru");
		ctx.invoke(genAdditionalConstructorParams, library, ctx, out);
		out.println(" ) {");
		out.print("super( ru");
		ctx.invoke(genAdditionalSuperConstructorArgs, library, ctx, out);
		out.println(" );");
		out.println("ezeInitialize();");
		out.println("}");

		out.print("public ");
		genRuntimeTypeName(library, ctx, out, TypeNameKind.JavaPrimitive);
		out.println(" " + Constants.LIBRARY_PREFIX + library.getFullyQualifiedName().replace('.', '_') + "() {");
		out.println("return this;");
		out.println("}");
	}

	public void genGetterSetter(Library library, Context ctx, TabbedWriter out, Field arg) {
		ctx.invoke(genGetter, arg, ctx, out);
		ctx.invoke(genSetter, arg, ctx, out);
	}

	public void genXmlTransient(Library part, TabbedWriter out) {
		out.println("@javax.xml.bind.annotation.XmlTransient");
	}

	public void genRuntimeTypeName(Library library, Context ctx, TabbedWriter out, TypeNameKind arg) {
		ctx.invoke(genPartName, library, ctx, out);
	}
}
