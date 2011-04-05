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

import org.eclipse.edt.gen.java.Constants;
import org.eclipse.edt.gen.java.Context;
import org.eclipse.edt.mof.codegen.api.TabbedWriter;
import org.eclipse.edt.mof.egl.EGLClass;
import org.eclipse.edt.mof.egl.Field;
import org.eclipse.edt.mof.egl.Function;
import org.eclipse.edt.mof.egl.Library;
import org.eclipse.edt.mof.egl.ParameterizedType;
import org.eclipse.edt.mof.egl.Part;
import org.eclipse.edt.mof.egl.Type;
import org.eclipse.edt.mof.egl.utils.IRUtils;

public class EGLClassTemplate extends JavaTemplate {

	public void validateClassBody(EGLClass part, Context ctx, Object... args) {
		ctx.validate(validateUsedParts, part, ctx, args);
		ctx.validate(validateFields, part, ctx, args);
		ctx.validate(validateFunctions, part, ctx, args);
	}

	public void validateUsedParts(EGLClass part, Context ctx, Object... args) {
		for (Part item : IRUtils.getReferencedPartsFor(part)) {
			ctx.validate(validateUsedPart, part, ctx, item);
		}
	}

	public void validateUsedPart(EGLClass part, Context ctx, Object... args) {
		ctx.validate(validate, (Part) args[0], ctx, args);
	}

	public void validateFields(EGLClass part, Context ctx, Object... args) {
		for (Field field : part.getFields()) {
			ctx.validate(validateField, part, ctx, field);
		}
	}

	public void validateField(EGLClass part, Context ctx, Object... args) {
		ctx.validate(validate, (Field) args[0], ctx, args);
	}

	public void validateFunctions(EGLClass part, Context ctx, Object... args) {
		for (Function function : part.getFunctions()) {
			ctx.validate(validateFunction, part, ctx, function);
		}
	}

	public void validateFunction(EGLClass part, Context ctx, Object... args) {
		ctx.validate(validate, (Function) args[0], ctx, args);
	}

	public void genClassHeader(EGLClass part, Context ctx, TabbedWriter out, Object... args) {
		out.print("public class ");
		ctx.gen(genClassName, part, ctx, out, args);
		out.print(" extends ");
		ctx.gen(genSuperClass, part, ctx, out, args);
		out.println(" {");
		ctx.gen(genSerialVersionUID, part, ctx, out, args);
	}

	public void genClassBody(EGLClass part, Context ctx, TabbedWriter out, Object... args) {
		ctx.gen(genFields, part, ctx, out, args);
		ctx.gen(genLibraries, part, ctx, out, args);
		ctx.gen(genConstructors, part, ctx, out, args);
		ctx.gen(genInitializeMethods, part, ctx, out, args);
		ctx.gen(genGetterSetters, part, ctx, out, args);
		ctx.gen(genLibraryAccessMethods, part, ctx, out, args);
		ctx.gen(genFunctions, part, ctx, out, args);
	}

	public void genFields(EGLClass part, Context ctx, TabbedWriter out, Object... args) {
		for (Field field : part.getFields()) {
			ctx.gen(genField, part, ctx, out, field);
		}
		// create the type constraint static hashmap, only if there are some parameterized types
		boolean needConstraints = false;
		for (Field field : part.getFields()) {
			// we only want to add constraints for parameterized types
			if (field.getType() instanceof ParameterizedType) {
				needConstraints = true;
				break;
			}
		}
		if (needConstraints) {
			out.println("private static java.util.HashMap<String, TypeConstraints> ezeTypeConstraints = new java.util.HashMap<String, TypeConstraints>();");
			out.println("static {");
			for (Field field : part.getFields()) {
				// we only want to add constraints for parameterized types
				if (field.getType() instanceof ParameterizedType) {
					out.print("ezeTypeConstraints.put(\"");
					ctx.gen(genName, field, ctx, out, args);
					out.print("\", new TypeConstraints(");
					ctx.gen(genRuntimeConstraint, field.getType(), ctx, out, args);
					out.println("));");
				}
			}
			out.println("}");
		}
	}

	public void genField(EGLClass part, Context ctx, TabbedWriter out, Object... args) {
		ctx.gen(genDeclaration, (Field) args[0], ctx, out, args);
	}

	@SuppressWarnings("unchecked")
	public void genLibraries(EGLClass part, Context ctx, TabbedWriter out, Object... args) {
		List<Library> libraries = (List<Library>) ctx.getAttribute(ctx.getClass(), Constants.Annotation_partLibrariesUsed);
		for (Library library : libraries) {
			ctx.gen(genLibrary, part, ctx, out, library);
		}
	}

	public void genLibrary(EGLClass part, Context ctx, TabbedWriter out, Object... args) {
		out.print("public ");
		ctx.gen(genRuntimeTypeName, (Type) args[0], ctx, out, TypeNameKind.EGLImplementation);
		out.println(" " + Constants.LIBRARY_PREFIX + ((Library) args[0]).getFullyQualifiedName().replace('.', '_') + ";");
	}

	public void genConstructors(EGLClass part, Context ctx, TabbedWriter out, Object... args) {
		ctx.gen(genConstructor, part, ctx, out, args);
	}

	public void genConstructor(EGLClass part, Context ctx, TabbedWriter out, Object... args) {}

	public void genInitializeMethods(EGLClass part, Context ctx, TabbedWriter out, Object... args) {
		out.println("public void ezeInitialize() {");
		ctx.gen(genInitializeMethodBody, part, ctx, out, args);
		out.println("}");
	}

	public void genInitializeMethodBody(EGLClass part, Context ctx, TabbedWriter out, Object... args) {
		for (Field field : part.getFields()) {
			ctx.gen(genInitializeMethod, part, ctx, out, field);
		}
	}

	public void genInitializeMethod(EGLClass part, Context ctx, TabbedWriter out, Object... args) {
		if (((Field) args[0]).getInitializerStatements() != null)
			ctx.gen(genStatementNoBraces, ((Field) args[0]).getInitializerStatements(), ctx, out, args);
		else {
			ctx.gen(genName, ((Field) args[0]), ctx, out, args);
			out.print(" = ");
			ctx.gen(genInitialization, ((Field) args[0]), ctx, out, args);
			out.println(";");
		}
	}

	public void genGetterSetters(EGLClass part, Context ctx, TabbedWriter out, Object... args) {
		for (Field field : part.getFields()) {
			ctx.gen(genGetterSetter, part, ctx, out, field);
		}
	}

	// we only generate getter and setters for fields within records or libraries, so do nothing if it gets back here
	public void genGetterSetter(EGLClass part, Context ctx, TabbedWriter out, Object... args) {}

	@SuppressWarnings("unchecked")
	public void genLibraryAccessMethods(EGLClass part, Context ctx, TabbedWriter out, Object... args) {
		List<Library> libraries = (List<Library>) ctx.getAttribute(ctx.getClass(), Constants.Annotation_partLibrariesUsed);
		for (Library library : libraries) {
			ctx.gen(genLibraryAccessMethod, part, ctx, out, library);
		}
	}

	public void genLibraryAccessMethod(EGLClass part, Context ctx, TabbedWriter out, Object... args) {
		out.print("public ");
		ctx.gen(genRuntimeTypeName, (Type) (Library) args[0], ctx, out, TypeNameKind.EGLImplementation);
		out.println(" " + Constants.LIBRARY_PREFIX + ((Library) args[0]).getFullyQualifiedName().replace('.', '_') + "() {");
		out.println("if (" + Constants.LIBRARY_PREFIX + ((Library) args[0]).getFullyQualifiedName().replace('.', '_') + " == null) {");
		out.print(Constants.LIBRARY_PREFIX + ((Library) args[0]).getFullyQualifiedName().replace('.', '_') + " = (");
		ctx.gen(genRuntimeTypeName, (Type) (Library) args[0], ctx, out, TypeNameKind.EGLImplementation);
		out.print(") _runUnit().getExecutable(\"");
		ctx.gen(genRuntimeTypeName, (Type) (Library) args[0], ctx, out, TypeNameKind.EGLImplementation);
		out.println("\");");
		out.println("}");
		out.println("return " + Constants.LIBRARY_PREFIX + ((Library) args[0]).getFullyQualifiedName().replace('.', '_') + ";");
		out.println("}");
	}

	public void genFunctions(EGLClass part, Context ctx, TabbedWriter out, Object... args) {
		for (Function function : part.getFunctions()) {
			ctx.gen(genFunction, part, ctx, out, function);
		}
	}

	public void genFunction(EGLClass part, Context ctx, TabbedWriter out, Object... args) {
		ctx.gen(genDeclaration, (Function) args[0], ctx, out, args);
	}

	public void genAdditionalConstructorParams(EGLClass part, Context ctx, TabbedWriter out, Object... args) {}

	public void genAdditionalSuperConstructorArgs(EGLClass part, Context ctx, TabbedWriter out, Object... args) {}

	public void genDeclaration(EGLClass part, Context ctx, TabbedWriter out, Object... args) {}

	public void genSuperClass(EGLClass part, Context ctx, TabbedWriter out, Object... args) {}
}
