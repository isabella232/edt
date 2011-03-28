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
import org.eclipse.edt.mof.egl.LogicAndDataPart;
import org.eclipse.edt.mof.egl.ParameterizedType;
import org.eclipse.edt.mof.egl.Part;
import org.eclipse.edt.mof.egl.ReturnStatement;
import org.eclipse.edt.mof.egl.Statement;
import org.eclipse.edt.mof.egl.Type;
import org.eclipse.edt.mof.egl.utils.IRUtils;

public class ClassTemplate extends PartTemplate {

	public void validateClassBody(Part part, Context ctx, Object... args) {
		validateUsedParts((EGLClass) part, ctx, args);
		validateFields((EGLClass) part, ctx, args);
		validateFunctions((EGLClass) part, ctx, args);
	}

	public void validateUsedParts(EGLClass part, Context ctx, Object... args) {
		for (Part item : IRUtils.getReferencedPartsFor(part)) {
			validateUsedPart(item, ctx, args);
		}
	}

	public void validateUsedPart(Part part, Context ctx, Object... args) {
			ctx.validate(validate, part, ctx, args);
	}

	public void validateFields(EGLClass part, Context ctx, Object... args) {
		for (Field field : part.getFields()) {
			validateField(field, ctx, args);
		}
	}

	public void validateField(Field field, Context ctx, Object... args) {
		ctx.validate(validate, field, ctx, args);
	}

	public void validateFunctions(EGLClass part, Context ctx, Object... args) {
		for (Function function : part.getFunctions()) {
			validateFunction(function, ctx, args);
		}
	}

	public void validateFunction(Function function, Context ctx, Object... args) {
		ctx.validate(validate, function, ctx, args);
	}

	public void genClassBody(Part part, Context ctx, TabbedWriter out, Object... args) {
		genFields((EGLClass) part, ctx, out, args);
		genLibraries((EGLClass) part, ctx, out, args);
		genConstructors((EGLClass) part, ctx, out, args);
		genInitializeMethods((EGLClass) part, ctx, out, args);
		genFieldGetterSetters((EGLClass) part, ctx, out, args);
		genLibraryAccessMethods((EGLClass) part, ctx, out, args);
		genFunctions((EGLClass) part, ctx, out, args);
	}

	public void genAdditionalConstructorParams(EGLClass part, TabbedWriter out, Object... args) {
		// nothing to do here
	}

	public void genAdditionalSuperConstructorArgs(EGLClass part, TabbedWriter out, Object... args) {
		// nothing to do here
	}

	public void genClassHeader(Part part, Context ctx, TabbedWriter out, Object... args) {
		out.print("public class ");
		genClassName(part, ctx, out, args);
		out.print(" extends ");
		genSuperClass(part, ctx, out, args);
		out.println(" {");
		genSerialVersionUID(part, ctx, out, args);
	}

	public void genSuperClass(EGLClass type, Context ctx, TabbedWriter out, Object... args) {
		// nothing to do here
	}

	public void genDeclaration(EGLClass type, Context ctx, TabbedWriter out, Object... args) {
		// nothing to do here
	}

	public void genFieldGetterSetters(EGLClass part, Context ctx, TabbedWriter out, Object... args) {
		for (Field field : part.getFields()) {
			genFieldGetterSetter(field, ctx, out, args);
		}
	}

	public void genFieldGetterSetter(Field field, Context ctx, TabbedWriter out, Object... args) {
		ctx.gen(genGetterSetter, field.getType(), ctx, out, field);
	}

	@SuppressWarnings("unchecked")
	public void genLibraryAccessMethods(EGLClass part, Context ctx, TabbedWriter out, Object... args) {
		List<Library> libraries = (List<Library>) ctx.getAttribute(ctx.getClass(), Constants.Annotation_partLibrariesUsed);
		for (Library library : libraries) {
			genLibraryAccessMethod(library, ctx, out, args);
		}
	}

	public void genLibraryAccessMethod(Library library, Context ctx, TabbedWriter out, Object... args) {
		out.print("public ");
		ctx.gen(genRuntimeTypeName, (Type) library, ctx, out, TypeNameKind.EGLImplementation);
		out.println(" " + Constants.LIBRARY_PREFIX + library.getFullyQualifiedName().replace('.', '_') + "() {");
		out.println("if (" + Constants.LIBRARY_PREFIX + library.getFullyQualifiedName().replace('.', '_') + " == null) {");
		out.print(Constants.LIBRARY_PREFIX + library.getFullyQualifiedName().replace('.', '_') + " = (");
		ctx.gen(genRuntimeTypeName, (Type) library, ctx, out, TypeNameKind.EGLImplementation);
		out.print(") _runUnit().getExecutable(\"");
		ctx.gen(genRuntimeTypeName, (Type) library, ctx, out, TypeNameKind.EGLImplementation);
		out.println("\");");
		out.println("}");
		out.println("return " + Constants.LIBRARY_PREFIX + library.getFullyQualifiedName().replace('.', '_') + ";");
		out.println("}");
	}

	public void genFields(EGLClass part, Context ctx, TabbedWriter out, Object... args) {
		for (Field field : part.getFields()) {
			genField(field, ctx, out, args);
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

	public void genField(Field field, Context ctx, TabbedWriter out, Object... args) {
		ctx.gen(genDeclaration, field, ctx, out, args);
	}

	@SuppressWarnings("unchecked")
	public void genLibraries(EGLClass part, Context ctx, TabbedWriter out, Object... args) {
		List<Library> libraries = (List<Library>) ctx.getAttribute(ctx.getClass(), Constants.Annotation_partLibrariesUsed);
		for (Library library : libraries) {
			genLibrary(library, ctx, out, args);
		}
	}

	public void genLibrary(Library library, Context ctx, TabbedWriter out, Object... args) {
		out.print("public ");
		ctx.gen(genRuntimeTypeName, (Type) library, ctx, out, TypeNameKind.EGLImplementation);
		out.println(" " + Constants.LIBRARY_PREFIX + library.getFullyQualifiedName().replace('.', '_') + ";");
	}

	public void genFunctions(EGLClass part, Context ctx, TabbedWriter out, Object... args) {
		for (Function function : part.getFunctions()) {
			genFunction(function, ctx, out, args);
		}
	}

	public void genFunction(Function function, Context ctx, TabbedWriter out, Object... args) {
		ctx.gen(genDeclaration, function, ctx, out, args);
	}

	/**
	 * Return whether the DataStructure needs an initialize method. An initialize method is needed when there are
	 * initializers, set value blocks, or fields that are redefined records.
	 * @return boolean
	 */
	private boolean needToInitialize(EGLClass part) {
		Function initFunc = part.getFunction(LogicAndDataPart.INIT_FUNCTION_NAME);
		if (initFunc != null) {
			List<Statement> stmts = initFunc.getStatements();
			if (stmts != null) {
				if (stmts.size() > 1) {
					return true;
				} else if (stmts.size() == 1) {
					// If the only statement is a ReturnStatement, don't
					// consider it as an initializer.
					if (!(stmts.get(0) instanceof ReturnStatement)) {
						return true;
					}
				}
			}
		}
		return false;
	}

	public void genConstructors(EGLClass part, Context ctx, TabbedWriter out, Object... args) {
		ctx.gen(genConstructor, part, ctx, out, args);
	}

	public void genInitialize(EGLClass part, TabbedWriter out, Object... args) {
		if (needToInitialize(part) && part.getAnnotation(Constants.REDEFINED_ANNOTATION) == null) {
			out.println("initialize( ezeProgram );");
		}
	}

	public void genInitializeMethods(EGLClass part, Context ctx, TabbedWriter out, Object... args) {
		out.println("public void ezeInitialize() {");
		// Run through the fields for the initializers
		for (Field field : part.getFields()) {
			genInitializeMethod(field, ctx, out, args);
		}
		out.println("}");
	}

	public void genInitializeMethod(Field field, Context ctx, TabbedWriter out, Object... args) {
		if (field.getInitializerStatements() != null)
			ctx.gen(genStatementNoBraces, field.getInitializerStatements(), ctx, out, args);
		else {
			ctx.gen(genName, field, ctx, out, args);
			out.print(" = ");
			ctx.gen(genInitialization, field, ctx, out, args);
			out.println(';');
		}
	}
}
