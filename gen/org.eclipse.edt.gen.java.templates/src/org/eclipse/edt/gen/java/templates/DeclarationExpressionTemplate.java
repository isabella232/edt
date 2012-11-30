/*******************************************************************************
 * Copyright © 2011, 2012 IBM Corporation and others.
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
import org.eclipse.edt.mof.egl.Expression;
import org.eclipse.edt.mof.egl.Field;
import org.eclipse.edt.mof.egl.MemberName;
import org.eclipse.edt.mof.egl.NewExpression;
import org.eclipse.edt.mof.egl.utils.TypeUtils;

public class DeclarationExpressionTemplate extends JavaTemplate {

	public void genDeclarationExpression(DeclarationExpression expr, Context ctx, TabbedWriter out) {
		for (Field field : expr.getFields()) {
			ctx.put( field, Boolean.TRUE );
			ctx.invoke(genDeclarationExpressionField, expr, ctx, out, field);
			ctx.remove( field );
		}
	}

	public void genDeclarationExpressionField(DeclarationExpression expr, Context ctx, TabbedWriter out, Field field) {
		// write out the debug extension data
		CommonUtilities.generateSmapExtension(field, ctx);
		ctx.invoke(genRuntimeTypeName, field, ctx, out, TypeNameKind.JavaPrimitive);
		out.print(" ");
		ctx.invoke(genName, field, ctx, out);
		// if the initializer statements are not against the currect field, then we need to do the initialization in
		// addition to the statements
		if (field.getInitializerStatements() != null
			&& field.getInitializerStatements().getStatements().size() == 1
			&& field.getInitializerStatements().getStatements().get(0) instanceof AssignmentStatement
			&& ((AssignmentStatement) field.getInitializerStatements().getStatements().get(0)).getAssignment().getLHS() instanceof MemberName
			&& ((MemberName) ((AssignmentStatement) field.getInitializerStatements().getStatements().get(0)).getAssignment().getLHS()).getMember()
				.equals(field)) {
			if (TypeUtils.isReferenceType(expr.getType()) || ctx.mapsToPrimitiveType(expr.getType())) {
				out.println(";");
				// we need to run the temporary variables separately, otherwise we might not get wraps
				ctx.invoke(genInitializeStatement, field, ctx, out);
			} else {
				// as this is a value type that doesn't map to a primitive, we are going to end up doing an ezeCopyTo, so
				// simply assign the result to the variable
				out.print(" = ");
				// we need to run the temporary variables separately, otherwise we might not get wraps
				Expression rhs = ((AssignmentStatement) field.getInitializerStatements().getStatements().get(0)).getAssignment().getRHS();
				// avoid initialization when the initializer is going to create a new value
				if (rhs instanceof NewExpression && rhs.getType().equals(field.getType())) {
					ctx.invoke(genExpression, rhs, ctx, out);
					out.println(";");
				} else {
					out.print("null");
					out.println(";");
					// we need to run the temporary variables separately, otherwise we might not get wraps
					ctx.invoke(genInitializeStatement, field, ctx, out);
				}
			}
		} else {
			out.print(" = ");
			// this logic will not combine, because it isn't safe to
			ctx.invoke(genInitialization, field, ctx, out);
			out.println(";");
			// now check for any statements to be processed
			if (field.getInitializerStatements() != null) {
				if (field.getInitializerStatements().getStatements() != null && field.getInitializerStatements().getStatements().size() > 0)
					ctx.genSmapEnd(field.getInitializerStatements().getStatements().get(0), out);
				ctx.invoke(genStatementNoBraces, field.getInitializerStatements(), ctx, out);
			}
		}
	}
}
