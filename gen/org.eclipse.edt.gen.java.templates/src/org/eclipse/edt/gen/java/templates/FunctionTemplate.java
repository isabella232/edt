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

import org.eclipse.edt.gen.java.CommonUtilities;
import org.eclipse.edt.gen.java.Context;
import org.eclipse.edt.mof.codegen.api.TabbedWriter;
import org.eclipse.edt.mof.egl.DeclarationExpression;
import org.eclipse.edt.mof.egl.Field;
import org.eclipse.edt.mof.egl.Function;
import org.eclipse.edt.mof.egl.FunctionParameter;
import org.eclipse.edt.mof.egl.LocalVariableDeclarationStatement;
import org.eclipse.edt.mof.egl.MemberName;
import org.eclipse.edt.mof.egl.ReturnStatement;

public class FunctionTemplate extends JavaTemplate {

	public void preGen(Function function, Context ctx) {
		ctx.invoke(preGen, function.getStatementBlock(), ctx);
	}

	public void genDeclaration(Function function, Context ctx, TabbedWriter out) {
		// write out the debug extension data
		CommonUtilities.generateSmapExtension(function, ctx);
		// process the function
		ctx.invokeSuper(this, genDeclaration, function, ctx, out);
		// remember what function we are processing
		ctx.setCurrentFunction(function.getName());
		ctx.invoke(genRuntimeTypeName, function, ctx, out, TypeNameKind.JavaPrimitive);
		out.print(" ");
		ctx.invoke(genName, function, ctx, out);
		out.print("(");
		// if this is the main function, we need to generate List<String> args
		if (function.getName().equalsIgnoreCase("main"))
			out.print("java.util.List<String> ezeargs");
		else
			ctx.foreach(function.getParameters(), ',', genDeclaration, ctx, out);
		out.println(") ");
		out.println("{");
		ctx.invoke(genStatementNoBraces, function.getStatementBlock(), ctx, out);
		// we need to create a local variable for the return, if the user didn't specify one
		if (function.getType() != null
			&& (ctx.getAttribute(function, org.eclipse.edt.gen.Constants.SubKey_functionHasReturnStatement) == null || !((Boolean) ctx.getAttribute(
				function, org.eclipse.edt.gen.Constants.SubKey_functionHasReturnStatement)).booleanValue())) {
			String temporary = ctx.nextTempName();
			LocalVariableDeclarationStatement localDeclaration = factory.createLocalVariableDeclarationStatement();
			localDeclaration.setContainer(function);
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
			ctx.invoke(genStatement, localDeclaration, ctx, out);
			// create a return statement
			ReturnStatement returnStatement = factory.createReturnStatement();
			returnStatement.setContainer(function);
			returnStatement.setExpression(nameExpression);
			ctx.invoke(genStatement, returnStatement, ctx, out);
		}
		// we always write out smap data for the final brace, just in case there is no return statement
		ctx.genSmapEnd(function, out);
		// write out the method ending brace
		out.println("}");
	}

	public void genAccessor(Function function, Context ctx, TabbedWriter out) {
		out.print("new org.eclipse.edt.javart.Delegate(\"");
		ctx.invoke(genName, function, ctx, out);
		out.print("\", this");
		for (int i = 0; i < function.getParameters().size(); i++) {
			FunctionParameter decl = function.getParameters().get(i);
			out.print(", ");
			if (org.eclipse.edt.gen.CommonUtilities.isBoxedParameterType(decl, ctx))
				ctx.invoke(genRuntimeTypeName, decl.getType(), ctx, out, TypeNameKind.JavaObject);
			else
				ctx.invoke(genRuntimeTypeName, decl, ctx, out, TypeNameKind.JavaObject);
			out.print(".class");
		}
		out.print(")");
	}
}
