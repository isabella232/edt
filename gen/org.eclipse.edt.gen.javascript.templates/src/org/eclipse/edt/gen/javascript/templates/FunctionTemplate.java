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

import org.eclipse.edt.gen.javascript.CommonUtilities;
import org.eclipse.edt.gen.javascript.Constants;
import org.eclipse.edt.gen.javascript.Context;
import org.eclipse.edt.mof.codegen.api.TabbedWriter;
import org.eclipse.edt.mof.codegen.api.TemplateException;
import org.eclipse.edt.mof.egl.DeclarationExpression;
import org.eclipse.edt.mof.egl.Field;
import org.eclipse.edt.mof.egl.Function;
import org.eclipse.edt.mof.egl.FunctionParameter;
import org.eclipse.edt.mof.egl.IrFactory;
import org.eclipse.edt.mof.egl.LocalVariableDeclarationStatement;
import org.eclipse.edt.mof.egl.MemberName;
import org.eclipse.edt.mof.egl.ReturnStatement;

public class FunctionTemplate extends MemberTemplate {

	private static IrFactory factory = IrFactory.INSTANCE;

	public void validate(Function function, Context ctx, Object... args) {
		ctx.validate(validate, function.getStatementBlock(), ctx, args);
	}

	public void genDeclaration(Function function, Context ctx, TabbedWriter out, Object... args) throws TemplateException {
		out.print('"');
		genName(function, ctx, out, args);
		out.print('"');
		out.print(": function(");
		ctx.foreach(function.getParameters(), ',', genDeclaration, ctx, out, args);
		for(FunctionParameter parm : function.getParameters()) {
			if (isBoxedParameterType(ctx, parm)) {
				out.print(", ");
				out.print(eze$$func);
				out.print(", ");
				out.print(caller);
				break;
			}
		}
		out.println(") {");
		ctx.gen(genStatementNoBraces, function.getStatementBlock(), ctx, out, args);
		// we need to create a local variable for the return, if the user didn't specify one
		if (function.getType() != null
			&& (ctx.getAttribute(function, Constants.functionHasReturnStatement) == null || !((Boolean) ctx.getAttribute(function,
				Constants.functionHasReturnStatement)).booleanValue())) {
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

//	public void genAccessor(Function function, Context ctx, TabbedWriter out, Object... args) throws TemplateException {
//		out.print("new org.eclipse.edt.javart.Delegate(\"");
//		genName(function, ctx, out, args);
//		out.print("\", this");
//		for (int i = 0; i < function.getParameters().size(); i++) {
//			FunctionParameter decl = function.getParameters().get(i);
//			out.print(", ");
//			if (CommonUtilities.isBoxedParameterType(ctx, decl))
//				ctx.gen(genRuntimeTypeName, (EObject) decl.getType(), ctx, out, RuntimeTypeNameKind.JavascriptObject);
//			else
//				ctx.gen(genRuntimeTypeName, decl, ctx, out, RuntimeTypeNameKind.JavascriptObject);
//			out.print(".class");
//		}
//		out.print(")");
//	}

	public void genName(Function function, Context ctx, TabbedWriter out, Object... args) throws TemplateException {
		// check to see if we are forced to make a signature extended alias name for this function, or use the original name
		// first check to see if the ir's tell us that the function is overloaded, as we only want to do aliasing when it is
		if (function.getAnnotation("EZE_OVERLOADED_FUNCTION") == null)
			super.genName(function, ctx, out, args);
		else {
			// next determine if there are inout or out parameters, as we only need to alias then
			boolean needsAlias = false;
			for (int i = 0; i < function.getParameters().size(); i++) {
				if (CommonUtilities.isBoxedParameterType(ctx, function.getParameters().get(i)))
					needsAlias = true;
			}
			// if we need an alias, then generate it, otherwise use the original name
			if (needsAlias) {
				String alias = function.getId();
				if (function.getType() == null)
					alias += "_void";
				else
					alias += "_" + function.getType().getTypeSignature();
				for (int i = 0; i < function.getParameters().size(); i++) {
					alias += "_" + function.getParameters().get(i).getType().getTypeSignature();
				}
				// now replace the period with an underscore
				while (alias.contains("."))
					alias = alias.replace('.', '_');
				out.print(alias);
			} else
				super.genName(function, ctx, out, args);
		}
	}
}
