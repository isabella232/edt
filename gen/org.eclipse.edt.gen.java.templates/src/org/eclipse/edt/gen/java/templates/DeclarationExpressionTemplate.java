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
import org.eclipse.edt.mof.EObject;
import org.eclipse.edt.mof.codegen.api.TabbedWriter;
import org.eclipse.edt.mof.egl.AssignmentStatement;
import org.eclipse.edt.mof.egl.DeclarationExpression;
import org.eclipse.edt.mof.egl.DelegateInvocation;
import org.eclipse.edt.mof.egl.Expression;
import org.eclipse.edt.mof.egl.Field;
import org.eclipse.edt.mof.egl.FunctionInvocation;
import org.eclipse.edt.mof.egl.InvocationExpression;
import org.eclipse.edt.mof.egl.QualifiedFunctionInvocation;
import org.eclipse.edt.mof.egl.StatementBlock;
import org.eclipse.edt.mof.impl.AbstractVisitor;

public class DeclarationExpressionTemplate extends JavaTemplate {

	public void genDeclarationExpression(DeclarationExpression expr, Context ctx, TabbedWriter out) {
		for (Field field : expr.getFields()) {
			// write out the debug extension data
			CommonUtilities.generateSmapExtension(field, ctx);
			// process the field
			ctx.invoke(genRuntimeTypeName, field, ctx, out, TypeNameKind.JavaPrimitive);
			out.print(" ");
			// check to see if it is safe to combine a declaration with an assignment. to do this, we need to check to see if
			// there is a set of initializer statements and if the 1st one is an assignment that uses the same field and the
			// assignment doesn't have side effects, then we are okay to combine
			if (field.getInitializerStatements() != null
				&& field.getInitializerStatements() instanceof StatementBlock
				&& ((StatementBlock) field.getInitializerStatements()).getStatements().size() > 0
				&& ((StatementBlock) field.getInitializerStatements()).getStatements().get(0) instanceof AssignmentStatement
				&& !hasSideEffects(((AssignmentStatement) ((StatementBlock) field.getInitializerStatements()).getStatements().get(0)).getAssignment().getRHS(),
					ctx)) {
				// this logic will combine
				ctx.invoke(genInitializeStatement, field.getType(), ctx, out, field);
				// as this is an expression that also creates a new line with the above println method, it throws off the
				// smap ending line number by 1. We need to issue a call to correct this
				ctx.setSmapLastJavaLineNumber(out.getLineNumber() - 1);
			} else {
				ctx.invoke(genName, field, ctx, out);
				out.print(" = ");
				// this logic will not combine, because it isn't safe to
				ctx.invoke(genInitialization, field, ctx, out);
				out.println(";");
				// as this is an expression that also creates a new line with the above println method, it throws off the
				// smap ending line number by 1. We need to issue a call to correct this
				ctx.setSmapLastJavaLineNumber(out.getLineNumber() - 1);
				// now check for any statements to be processed
				if (field.getInitializerStatements() != null)
					ctx.invoke(genStatementNoBraces, field.getInitializerStatements(), ctx, out);
			}
		}
	}

	public static boolean hasSideEffects(Expression expr, Context ctx) {
		return (new CheckSideEffects()).checkSideEffect(expr, ctx);
	}

	public static class CheckSideEffects extends AbstractVisitor {
		boolean has = false;
		Context ctx;

		public boolean checkSideEffect(Expression expr, Context ctx) {
			this.ctx = ctx;
			disallowRevisit();
			setReturnData(false);
			expr.accept(this);
			return (Boolean) getReturnData();
		}

		public boolean visit(EObject obj) {
			return false;
		}

		public boolean visit(Expression expr) {
			if (has)
				return false;
			return true;
		}

		public boolean visit(FunctionInvocation expr) {
			processInvocation(expr);
			return false;
		}

		public boolean visit(DelegateInvocation expr) {
			processInvocation(expr);
			return false;
		}

		public boolean visit(QualifiedFunctionInvocation expr) {
			processInvocation(expr);
			return false;
		}

		private void processInvocation(InvocationExpression object) {
			// we need to scan the function arguments for any conditions that require temporary variables to be set
			// up. Things like IN args, INOUT args with java primitives, OUT arg initialization, etc.
			for (int i = 0; i < object.getTarget().getParameters().size(); i++) {
				if (org.eclipse.edt.gen.CommonUtilities.isArgumentToBeAltered(object.getTarget().getParameters().get(i), object.getArguments().get(i), ctx)) {
					has = true;
					setReturnData(has);
					break;
				}
			}
			return;
		}
	}
}
