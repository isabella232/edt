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
import org.eclipse.edt.mof.egl.AssignmentStatement;
import org.eclipse.edt.mof.egl.DeclarationExpression;
import org.eclipse.edt.mof.egl.Field;
import org.eclipse.edt.mof.egl.MemberName;
import org.eclipse.edt.mof.egl.ParameterKind;
import org.eclipse.edt.mof.egl.StatementBlock;
import org.eclipse.edt.mof.egl.utils.TypeUtils;

public class DeclarationExpressionTemplate extends JavaTemplate {

	public void genDeclarationExpression(DeclarationExpression expr, Context ctx, TabbedWriter out) {
		for (Field field : expr.getFields()) {
			// write out the debug extension data
			CommonUtilities.generateSmapExtension(field, ctx);
			// process the field
			ctx.invoke(genRuntimeTypeName, field, ctx, out, TypeNameKind.JavaPrimitive);
			out.print(" ");
			// if the initializer statements are not against the currect field, then we need to do the initialization in
			// addition to the statements
			if (field.getInitializerStatements() != null
				&& field.getInitializerStatements() instanceof StatementBlock
				&& ((StatementBlock) field.getInitializerStatements()).getStatements().size() > 0
				&& ((StatementBlock) field.getInitializerStatements()).getStatements().get(0) instanceof AssignmentStatement
				&& ((AssignmentStatement) ((StatementBlock) field.getInitializerStatements()).getStatements().get(0)).getAssignment().getLHS() instanceof MemberName
				&& ((MemberName) ((AssignmentStatement) ((StatementBlock) field.getInitializerStatements()).getStatements().get(0)).getAssignment().getLHS())
					.getMember().equals(field)) {
				// if this is a value type, we need to define the field and then process the combined initialize statements
				if (TypeUtils.isValueType(field.getType())) {
					ctx.invoke(genName, field, ctx, out);
					out.print(" = ");
					if (ctx.getAttribute(field, org.eclipse.edt.gen.Constants.SubKey_functionArgumentTemporaryVariable) != null
						&& ctx.getAttribute(field, org.eclipse.edt.gen.Constants.SubKey_functionArgumentTemporaryVariable) != ParameterKind.PARM_IN)
						out.print("null");
					else
						ctx.invoke(genInitialization, field, ctx, out);
					out.println(";");
					// as this is an expression that also creates a new line with the above println method, it throws off the
					// smap ending line number by 1. We need to issue a call to correct this
					ctx.setSmapLastJavaLineNumber(out.getLineNumber() - 1);
				}
				// this logic will combine
				ctx.invoke(genInitializeStatement, field, ctx, out, true);
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
}
