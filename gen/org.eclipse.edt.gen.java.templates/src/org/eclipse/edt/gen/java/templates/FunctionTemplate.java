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
import org.eclipse.edt.gen.java.Constants;
import org.eclipse.edt.gen.java.Context;
import org.eclipse.edt.mof.codegen.api.TabbedWriter;
import org.eclipse.edt.mof.egl.DeclarationExpression;
import org.eclipse.edt.mof.egl.Field;
import org.eclipse.edt.mof.egl.Function;
import org.eclipse.edt.mof.egl.FunctionParameter;
import org.eclipse.edt.mof.egl.LocalVariableDeclarationStatement;
import org.eclipse.edt.mof.egl.MemberAccess;
import org.eclipse.edt.mof.egl.MemberName;
import org.eclipse.edt.mof.egl.Part;
import org.eclipse.edt.mof.egl.ReturnStatement;

public class FunctionTemplate extends JavaTemplate {

	public void preGen(Function function, Context ctx) {
		ctx.invoke(preGen, function.getStatementBlock(), ctx);
	}
	public void genFunctionHeader(Function function, Context ctx, TabbedWriter out) {
		// process the function
		ctx.invokeSuper(this, genDeclaration, function, ctx, out);
		// remember what function we are processing
		ctx.setCurrentFunction(function);
		ctx.invoke(genRuntimeTypeName, function, ctx, out, TypeNameKind.JavaPrimitive);
		out.print(" ");
		ctx.invoke(genName, function, ctx, out);
		out.print("(");
		ctx.foreach(function.getParameters(), ',', genDeclaration, ctx, out);
		out.println(") {");
	}
	public void genFunctionBody(Function function, Context ctx, TabbedWriter out) {
		ctx.invoke(genStatementNoBraces, function.getStatementBlock(), ctx, out);
		// we need to create a local variable for the return, if the user didn't specify one
		if (function.getType() != null
			&& (ctx.getAttribute(function, org.eclipse.edt.gen.Constants.SubKey_functionHasReturnStatement) == null || !((Boolean) ctx.getAttribute(function,
				org.eclipse.edt.gen.Constants.SubKey_functionHasReturnStatement)).booleanValue())) {
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
	}
	public void genDeclaration(Function function, Context ctx, TabbedWriter out) {
		// write out the debug extension data
		CommonUtilities.generateSmapExtension(function, ctx);
		ctx.invoke(genFunctionHeader, function, ctx, out);
		ctx.invoke(genFunctionBody, function, ctx, out);
		// we always write out smap data for the final brace, just in case there is no return statement
		ctx.genSmapEnd(function, out);
		// write out the method ending brace
		out.println("}");
	}

	public void genAccessor(Function function, Context ctx, TabbedWriter out) {
		out.print("new org.eclipse.edt.javart.Delegate(\"");
		ctx.invoke(genName, function, ctx, out);
		out.print("\", ");
		if ( Boolean.TRUE.equals( ctx.get( ExternalTypeTemplate.DELEGATE_IN_INNER_CLASS ) ) )
		{
			ctx.invoke(genRuntimeTypeName, ctx.getAttribute(ctx.getClass(), Constants.SubKey_partBeingGenerated), ctx, out);
			out.print(".");
		}
		out.print("this");
		for (int i = 0; i < function.getParameters().size(); i++) {
			FunctionParameter decl = function.getParameters().get(i);
			out.print(", ");
			if (org.eclipse.edt.gen.CommonUtilities.isBoxedParameterType(decl, ctx) && !decl.isConst())
				out.print("AnyBoxedObject.class");
			else
				ctx.invoke(genRuntimeClassTypeName, decl, ctx, out, TypeNameKind.JavaObject);
		}
		out.print(")");
	}

	public void genAccessor(Function function, Context ctx, TabbedWriter out, MemberAccess arg) {
		out.print("new org.eclipse.edt.javart.Delegate(\"");
		ctx.invoke(genName, function, ctx, out);
		out.print("\", ");
		if (((Part) ctx.getAttribute(ctx.getClass(), Constants.SubKey_partBeingGenerated)).equals(arg.getQualifier()))
			out.print("this");
		else
			ctx.invoke(genExpression, arg.getQualifier(), ctx, out);
		for (int i = 0; i < function.getParameters().size(); i++) {
			FunctionParameter decl = function.getParameters().get(i);
			out.print(", ");
			if (org.eclipse.edt.gen.CommonUtilities.isBoxedParameterType(decl, ctx) && !decl.isConst())
				out.print("AnyBoxedObject.class");
			else
				ctx.invoke(genRuntimeClassTypeName, decl, ctx, out, TypeNameKind.JavaObject);
		}
		out.print(")");
	}
	public void genFunctionParametersSignature(Function function, Context ctx, TabbedWriter out) {
		out.print("@FunctionSignature(name=\"");
		ctx.invoke(genName, function, ctx, out);
		out.print("\", parameters={");
		ctx.foreach(function.getParameters(), ',', genFunctionParametersSignature, ctx, out);
		if(function.getReturnType() != null){
			if(function.getParameters() != null && function.getParameters().size() > 0){
				out.print(", ");
			}
			FunctionParameter ret = ctx.getFactory().createFunctionParameter();
			ret.setType(function.getReturnType());
			ret.setName("return");
			ctx.invoke(genFunctionParametersSignature, ret, ctx, out);
		}
		out.println("})");
	}	
}
