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

import org.eclipse.edt.compiler.core.IEGLConstants;
import org.eclipse.edt.gen.java.CommonUtilities;
import org.eclipse.edt.gen.java.Constants;
import org.eclipse.edt.gen.java.Context;
import org.eclipse.edt.mof.codegen.api.TabbedWriter;
import org.eclipse.edt.mof.egl.Annotation;
import org.eclipse.edt.mof.egl.DeclarationExpression;
import org.eclipse.edt.mof.egl.Field;
import org.eclipse.edt.mof.egl.Function;
import org.eclipse.edt.mof.egl.FunctionParameter;
import org.eclipse.edt.mof.egl.IrFactory;
import org.eclipse.edt.mof.egl.LocalVariableDeclarationStatement;
import org.eclipse.edt.mof.egl.MemberName;
import org.eclipse.edt.mof.egl.ReturnStatement;
import org.eclipse.edt.mof.egl.Type;

public class FunctionTemplate extends MemberTemplate {

	private static IrFactory factory = IrFactory.INSTANCE;

	public void validate(Function function, Context ctx, Object... args) {
		ctx.validate(validate, function.getStatementBlock(), ctx, args);
	}

	public void genDeclaration(Function function, Context ctx, TabbedWriter out, Object... args) {
		// write out the debug extension data
		generateDebugExtension(function, ctx);
		// process the function
		super.genDeclaration(function, ctx, out, args);
		// remember what function we are processing
		ctx.setCurrentFunction(function.getName());
		genRuntimeTypeName(function, ctx, out, args);
		out.print(' ');
		genName(function, ctx, out, args);
		out.print('(');
		// if this is the main function, we need to generate List<String> args
		if (function.getName().equalsIgnoreCase("main"))
			out.print("java.util.List<String> args");
		else
			ctx.foreach(function.getParameters(), ',', genDeclaration, ctx, out, args);
		out.println(") ");
		out.println("{");
		ctx.gen(genStatementNoBraces, function.getStatementBlock(), ctx, out, args);
		// we need to create a local variable for the return, if the user didn't specify one
		if (function.getType() != null
			&& (ctx.getAttribute(function, Constants.Annotation_functionHasReturnStatement) == null || !((Boolean) ctx.getAttribute(function,
				Constants.Annotation_functionHasReturnStatement)).booleanValue())) {
			String temporary = ctx.nextTempName();
			LocalVariableDeclarationStatement localDeclaration = factory.createLocalVariableDeclarationStatement();
			localDeclaration.setFunctionMember(function);
			DeclarationExpression declarationExpression = factory.createDeclarationExpression();
			Field field = factory.createField();
			field.setName(temporary);
			field.setType(function.getType());
			field.setIsNullable(function.isNullable());
			// we need to create the member access
			MemberName nameExpression = factory.createMemberName();
			nameExpression.setMember(field);
			nameExpression.setId(field.getName());
			// add the field to the declaration expression
			declarationExpression.getFields().add(field);
			// connect the declaration expression to the local declaration
			localDeclaration.setExpression(declarationExpression);
			ctx.gen(genStatement, localDeclaration, ctx, out, args);
			// create a return statement
			ReturnStatement returnStatement = factory.createReturnStatement();
			returnStatement.setFunctionMember(function);
			returnStatement.setExpression(nameExpression);
			ctx.gen(genStatement, returnStatement, ctx, out, args);
		}
		out.println('}');
	}

	public void genAccessor(Function function, Context ctx, TabbedWriter out, Object... args) {
		out.print("new org.eclipse.edt.javart.Delegate(\"");
		genName(function, ctx, out, args);
		out.print("\", this");
		for (int i = 0; i < function.getParameters().size(); i++) {
			FunctionParameter decl = function.getParameters().get(i);
			out.print(", ");
			if (CommonUtilities.isBoxedParameterType(ctx, decl))
				ctx.gen(genRuntimeTypeName, decl.getType(), ctx, out, TypeNameKind.JavaObject);
			else
				ctx.gen(genRuntimeTypeName, decl, ctx, out, TypeNameKind.JavaObject);
			out.print(".class");
		}
		out.print(")");
	}

	private void generateDebugExtension(Function function, Context ctx) {
		// get the line number. If it is not found, then we can't write the debug extension
		Annotation annotation = function.getAnnotation(IEGLConstants.EGL_LOCATION);
		if (annotation != null && annotation.getValue(IEGLConstants.EGL_PARTLINE) != null) {
			ctx.getDebugExtension().append("" + ((Integer) annotation.getValue(IEGLConstants.EGL_PARTLINE)).intValue() + ";");
			ctx.getDebugExtension().append("F:" + function.getName() + ";(");
			for (int i = 0; i < function.getParameters().size(); i++) {
				ctx.getDebugExtension().append(generateJavaTypeSignature(function.getParameters().get(i).getType(), false));
			}
			ctx.getDebugExtension().append(")" + generateJavaTypeSignature(function.getReturnType(), true) + ";\n");
		}
	}

	private String generateJavaTypeSignature(Type type, boolean isReturn) {
		String signature = "";
		return signature;
	}
}
