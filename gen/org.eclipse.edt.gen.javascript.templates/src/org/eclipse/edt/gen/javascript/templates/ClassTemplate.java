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
import org.eclipse.edt.mof.EObject;
import org.eclipse.edt.mof.codegen.api.TabbedWriter;
import org.eclipse.edt.mof.codegen.api.TemplateException;
import org.eclipse.edt.mof.egl.EGLClass;
import org.eclipse.edt.mof.egl.Field;
import org.eclipse.edt.mof.egl.Function;
import org.eclipse.edt.mof.egl.Part;

public class ClassTemplate extends PartTemplate {

	public void validateClassBody(Part part, Context ctx, Object... args) {
		validateFieldDeclarations((EGLClass) part, ctx, args);
		validateFunctions((EGLClass) part, ctx, args);
	}

	public void validateFieldDeclarations(EGLClass part, Context ctx, Object... args) throws TemplateException {
		for (Field field : part.getFields()) {
			ctx.validate(validate, field, ctx, args);
		}
	}

	public void validateFunctions(EGLClass part, Context ctx, Object... args) throws TemplateException {
		for (Function function : part.getFunctions()) {
			ctx.validate(validate, function, ctx, args);
		}
	}

	public void genRuntimeTypeName(EGLClass part, Context ctx, TabbedWriter out, Object... args) {
		out.print("egl.");
		this.genPartName(part, ctx, out, args);
	}

	public void genInstantiation(EGLClass part, Context ctx, TabbedWriter out, Object... args) {
		out.print("new ");
		genRuntimeTypeName(part, ctx, out, args);
		out.print("()");
	}
	
	public void genDefineClause(Part clazz, Context ctx, TabbedWriter out, Object...args) {
		out.print("egl.defineClass(");
		out.print(quoted(clazz.getPackageName()));
		out.print(", ");
		out.print(quoted(clazz.getName()));
		out.print(", ");
		genSuperTypePackageName(clazz, ctx, out, args);
		out.print(", ");
		genSuperTypeClassName(clazz, ctx, out, args);
		out.print(", ");
		out.println('(');
	}
	public void genBody(Part part, Context ctx, TabbedWriter out, Object... args) {
		genClassBody((EGLClass)part, ctx, out, args);
	}
	
	public void genClassBody(EGLClass part, Context ctx, TabbedWriter out, Object... args) {
		genDefaultConstructor(part, ctx, out, args);
		out.println(",");
		genSetEmptyMethod(part, ctx, out, args);
		out.println(",");
		genSetInitialMethod(part, ctx, out, args);
		out.println(",");
		genCloneMethod(part, ctx, out, args);
//		out.println(",");
//		genGetFieldSignaturesMethod(part, ctx, out, args);
//		out.println(",");
//		genGetJSONNamesMethod(part, ctx, out, args);
//		out.println(",");
//		genFromJSONMethod(part, ctx, out, args);
//		out.println(",");
//		genToJSONMethod(part, ctx, out, args);
//		out.println(",");
//		genFromXMLMethod(part, ctx, out, args);
//		out.println(",");
//		genToXMLMethod(part, ctx, out, args);
//		out.println(",");
		genFunctions(part, ctx, out, args);
	}

	public void genAdditionalConstructorParams(EGLClass part, TabbedWriter out, Object... args) throws TemplateException {
	// Default is do nothing
	}

	public void genAdditionalSuperConstructorArgs(EGLClass part, TabbedWriter out, Object... args) throws TemplateException {
	// Default is do nothing
	}

	public void genSuperTypePackageName(Part type, Context ctx, TabbedWriter out, Object... args) {

	}
	public void genSuperTypeClassName(Part type, Context ctx, TabbedWriter out, Object... args) {

	}

	public void genDefaultConstructor(EGLClass part, Context ctx, TabbedWriter out, Object... args) throws TemplateException {
		// Generate default constructor
		out.print(quoted("constructor"));
		out.println(": function() {");
		out.print("this.");
		out.print("eze$$XMLRootElementName = ");
		out.print(quoted(part.getName()));
		out.println(';');
		out.println("this.eze$$setInitial();");
		out.println('}');
	}

	public void genSetEmptyMethod(EGLClass part, Context ctx, TabbedWriter out, Object... args) throws TemplateException {
		out.print(quoted("eze$$setEmpty"));
		out.println(": function() {");
		for (Field field : part.getFields()) {
			out.print("this.");
			genName(field, ctx, out, args);
			out.print(" = ");
			ctx.gen(genDefaultValue, field, ctx, out, args);
			out.println(";");
		}
		out.println("};");
	}
	
	public void genSetInitialMethod(EGLClass part, Context ctx, TabbedWriter out, Object... args) throws TemplateException {
		// Generate default constructor
		out.print(quoted("eze$$setInitial"));
		out.println(": function() {");
		out.println("eze$setEmpty();");
		for (Field field : part.getFields()) {
			if (field.getInitializerStatements() != null)
				ctx.gen(genStatementNoBraces, field.getInitializerStatements(), ctx, out, args);
		}
		out.println("};");
			
	}
	
	/**
	 * Generate a clone method to ensure global fields are cloned as well.
	 */
	public void genCloneMethod(EGLClass part, Context ctx, TabbedWriter out, Object... args) throws TemplateException {
		out.print(quoted("eze$$clone"));
		out.println(": function() {");
		String temp1 = "ezert$$1";
		String temp2 = "ezert$$2";
		out.print("var ");
		out.print(temp1);
		out.println(" = this;");
		out.print("var ");
		out.print(temp2);
		out.print(" = ");
		genInstantiation(part, ctx, out, args);
		out.println(";");
		// clone fields
		for (Field field : part.getFields()) {
			out.print(temp2);
			out.print('.');
			genName(field, ctx, out, args);
			out.print(" = ");
			out.print(temp1);
			out.print('.');
			genName(field, ctx, out, args);
			out.println(';');
		}
		out.print("return ");
		out.print(temp2);
		out.println(";");
		out.println("};");
	}

	public void genFieldDecl(Field field, Context ctx, TabbedWriter out, Object... args) throws TemplateException {
		ctx.gen(genDeclaration, field, ctx, out, args);
	}

	public void genFieldGetterSetters(EGLClass part, Context ctx, TabbedWriter out, Object... args) throws TemplateException {
		for (Field field : part.getFields()) {
			ctx.gen(genGetterSetter, (EObject) field.getType(), ctx, out, field);
		}
	}

	public void genFunction(Function function, Context ctx, TabbedWriter out, Object... args) throws TemplateException {
		ctx.gen(genDeclaration, function, ctx, out, args);
	}

	public void genFieldDeclarations(EGLClass part, Context ctx, TabbedWriter out, Object... args) throws TemplateException {
		for (Field field : part.getFields()) {
			ctx.gen(genDeclaration, field, ctx, out, args);
		}
	}

	public void genFunctions(EGLClass part, Context ctx, TabbedWriter out, Object... args) throws TemplateException {
		for (Function function : part.getFunctions()) {
			out.println(',');
			ctx.gen(genDeclaration, function, ctx, out, args);
		}
	}

	public void genConstructors(EGLClass part, Context ctx, TabbedWriter out, Object... args) {
		ctx.gen(genDefaultConstructor, (EObject) part, ctx, out, args);
	}

}
